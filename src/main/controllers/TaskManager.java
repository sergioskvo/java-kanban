package controllers;

import exceptions.TaskOverlapException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TasksTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    int saveTask(Task task) throws TaskOverlapException;

    int saveTask(Epic epic);

    Integer saveTask(SubTask subTask) throws TaskOverlapException;

    void deleteAllTasksWithType(TasksTypes taskType);

    Task getTaskViaId(int taskId);

    Integer refreshTask(Task refreshTask);

    void deleteViaId(int taskId);

    HashMap<Integer, SubTask> getSubTasksFromEpic(int epicTaskId);

    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<SubTask> getSubTasksList();

    List<Task> getPrioritizedTasks();
}
