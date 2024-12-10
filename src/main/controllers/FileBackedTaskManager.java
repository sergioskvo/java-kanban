package controllers;
import model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager{
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
}
