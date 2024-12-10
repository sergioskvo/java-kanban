package controllers;

import model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int saveTask(Task task) {
        super.saveTask(task);
        save();
        return task.getIdNumber();
    }

    @Override
    public int saveTask(Epic epic) {
        super.saveTask(epic);
        save();
        return epic.getIdNumber();
    }

    @Override
    public Integer saveTask(SubTask subTask) {
        Integer taskId = super.saveTask(subTask);
        save();
        return taskId;
    }

    @Override
    public void deleteAllTasksWithType(TasksTypes taskType) {
        super.deleteAllTasksWithType(taskType);
        save();
    }

    @Override
    public Integer refreshTask(Task refreshTask) {
        Integer idNumber = super.refreshTask(refreshTask);
        save();
        return idNumber;
    }

    @Override
    public void deleteViaId(int taskId) {
        super.deleteViaId(taskId);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (file.length() == 0) {
                writer.write("id,type,name,status,description,epic\n");
            }
            for (Task task : super.getTasksList()) {
                writer.write((toString(task)));
                writer.newLine();
            }

            for (Epic epic : super.getEpicsList()) {
                writer.write((toString(epic)));
                writer.newLine();
            }

            for (SubTask subTask : super.getSubTasksList()) {
                writer.write((toString(subTask)));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл: " + file.getName(), e);
        }
    }

    private String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getIdNumber(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task instanceof SubTask ? ((SubTask) task).getEpicIdNumber() : ""
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Пропускаем заголовок
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.saveTask((Epic) task);
                } else if (task instanceof SubTask) {
                    manager.saveTask((SubTask) task);
                } else {
                    manager.saveTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла: " + file.getName(), e);
        }

        return manager;
    }

    private static Task fromString(String line) {
        String[] parts = line.split(",");
        int idNumber = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        StatusCodes status = StatusCodes.valueOf(parts[3]);
        String description = parts[4];
        String epicIdNumber = parts.length > 5 ? parts[5] : "";

        Task task = null;
        switch (type) {
            case "TASK":
                task = new Task(name, description, idNumber, status);
                break;
            case "EPIC":
                task = new Epic(name, description, idNumber, status);
                break;
            case "SUBTASK":
                int epicId = Integer.parseInt(epicIdNumber);
                task = new SubTask(name, description, idNumber, status, epicId);
                break;
        }
        return task;
    }
}
