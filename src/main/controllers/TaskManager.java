package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TasksTypes;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    int saveTask(Task task);

    int saveTask(Epic epic);

    Integer saveTask(SubTask subTask);

    void deleteAllTasksWithType(TasksTypes taskType);

    Task getTaskViaId(int taskId);

    Integer refreshTask(Task refreshTask);

    void deleteViaId(int taskId);

    HashMap<Integer, SubTask> getSubTasksFromEpic(int epicTaskId);

    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<SubTask> getSubTasksList();
}
