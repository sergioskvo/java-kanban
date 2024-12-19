package controllers;

import exceptions.ManagerSaveException;
import exceptions.TaskOverlapException;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int saveTask(Task task) throws TaskOverlapException {
        super.saveTask(task);
        save();
        return task.getIdNumber();
    }

    @Override
    public int saveTask(Epic epic) {
        Map<Integer, SubTask> subTasksForEpic = super.getSubTasksFromEpic(epic.getIdNumber());
        subTasksForEpic.values().forEach(subtask -> epic.recalculateEpicDetails(subtask, 1));
        super.saveTask(epic);
        save();
        return epic.getIdNumber();
    }

    @Override
    public Integer saveTask(SubTask subTask) throws TaskOverlapException {
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
            // Если файл пустой, записываем заголовок
            if (file.length() == 0) {
                writer.write("id,type,name,status,description,startTime,duration,endTime,epicId\n");
            }
            super.getTasksList().stream()
                    .map(this::toString)
                    .forEach(taskStr -> writeToFile(writer, taskStr));
            super.getEpicsList().stream()
                    .map(this::toString)
                    .forEach(epicStr -> writeToFile(writer, epicStr));
            super.getSubTasksList().stream()
                    .map(this::toString)
                    .forEach(subTaskStr -> writeToFile(writer, subTaskStr));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл: " + file.getName(), e);
        }
    }


    private void writeToFile(BufferedWriter writer, String str) {
        try {
            writer.write(str);
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл", e);
        }
    }

    private String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s,%s",
                task.getIdNumber(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime() == null ? "дата старта не задана" : task.getStartTime().format(formatter),
                task.getDuration() == null ? 0 : task.getDuration().toMinutes(),
                task.getStartTime() == null || task.getDuration() == null ? "дата окончания не задана" :
                        (task instanceof Epic ? ((Epic) task).getEndTime().format(formatter)
                                : task.getEndTime().format(formatter)),
                task instanceof SubTask ? ((SubTask) task).getEpicIdNumber() : ""
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<SubTask> subtaskList = new ArrayList<>();
            // Пропускаем заголовок
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.saveTask((Epic) task);
                } else if (task instanceof SubTask) {
                    subtaskList.add((SubTask) task);
                } else {
                    manager.saveTask(task);
                }
            }
            subtaskList.forEach(subTask -> {
                try {
                    manager.saveTask(subTask);
                } catch (TaskOverlapException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из файла: " + file.getName(), e);
        } catch (TaskOverlapException e) {
            throw new RuntimeException(e);
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
        LocalDateTime startTime = parts[5].equals("дата старта не задана") ? null : LocalDateTime.parse(parts[5], formatter);
        Duration duration = Long.parseLong(parts[6]) == 0 ? null : Duration.ofMinutes(Long.parseLong(parts[6]));
        LocalDateTime endTime = parts[7].equals("дата окончания не задана") ? null : LocalDateTime.parse(parts[7], formatter);
        String epicIdNumber = parts.length > 8 ? parts[8] : "";

        Task task = null;
        switch (type) {
            case "TASK":
                task = new Task(name, description, idNumber, status, duration, startTime);
                break;
            case "EPIC":
                task = new Epic(name, description, idNumber, status);
                break;
            case "SUBTASK":
                int epicId = Integer.parseInt(epicIdNumber);
                task = new SubTask(name, description, idNumber, status, epicId, duration, startTime);
                break;
        }
        return task;
    }
}
