package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import model.*;


public class TaskManager {
    public static int taskId = 0;
    private HashMap<Integer, Task> tasksList = new HashMap<>();
    private HashMap<Integer, Epic> epicsList = new HashMap<>();
    private HashMap<Integer, SubTask> subTasksList = new HashMap<>();

    public int saveTask(Task task) {
        task.setIdNumber(++taskId);
        tasksList.put(taskId, task);
        return taskId;
    }

    public int saveTask(Epic epic) {
        epic.setIdNumber(++taskId);
        epicsList.put(taskId, epic);
        return taskId;
    }

    public Integer saveTask(SubTask subTask) {
        if (epicsList.containsKey(subTask.getEpicIdNumber())) {
            subTask.setIdNumber(++taskId);
            subTasksList.put(taskId, subTask);
            HashMap<Integer, SubTask> subTasksFromEpic = getSubTasksFromEpic(subTask.getEpicIdNumber());
            refreshEpicStatus(subTasksFromEpic, subTask.getEpicIdNumber());
            return taskId;
        }
        return null;
    }

    private void refreshEpicStatus(HashMap<Integer, SubTask> subTasksFromEpic, int epicIdNumber) {
        if (subTasksFromEpic.isEmpty() || isAllStatusEqual(StatusCodes.NEW, subTasksFromEpic)) {
            epicsList.get(epicIdNumber).setStatus(StatusCodes.NEW);
        } else if (isAllStatusEqual(StatusCodes.DONE, subTasksFromEpic)) {
            epicsList.get(epicIdNumber).setStatus(StatusCodes.DONE);
        } else {
            epicsList.get(epicIdNumber).setStatus(StatusCodes.IN_PROGRESS);
        }
    }

    private boolean isAllStatusEqual(StatusCodes statusCodes, HashMap<Integer, SubTask> subTasksFromEpic) {
        int c = 0;
        int size = subTasksFromEpic.size();
        for (SubTask subTask : subTasksFromEpic.values()) {
            if (subTask.getStatus() == statusCodes) {
                c++;
            }
        }
        return size == c;
    }

    public void deleteAllTasksWithType(TasksTypes taskType) {
        switch (taskType) {
            case TasksTypes.TASK:
                tasksList.clear();
                System.out.println("Список с обычными задачами очищен");
                break;
            case TasksTypes.EPIC:
                epicsList.clear();
                subTasksList.clear();
                System.out.println("Список эпиков очищен вместе со списком подзадач (а иначе смысл подзадач без эпиков)");
                break;
            case TasksTypes.SUBTASK:
                subTasksList.clear();
                for (Epic epic : epicsList.values()) {
                    epic.setStatus(StatusCodes.NEW);
                }
                System.out.println("Список с подзадачами очищен");
                break;
            default:
                System.out.println("Нет такого типа задачи");
        }
    }

    public Task getTaskViaId(int taskId) {
        if (tasksList.containsKey(taskId)) {
            return tasksList.get(taskId);
        } else if (epicsList.containsKey(taskId)) {
            return epicsList.get(taskId);
        } else if (subTasksList.containsKey(taskId)) {
            return subTasksList.get(taskId);
        } else {
            System.out.println("Нет задачи с таким id, вернулось null");
            return null;
        }
    }

    public Integer refreshTask(Task refreshTask) {
        int idNumber;
        TasksTypes taskType = refreshTask.getType();
        switch (taskType) {
            case TasksTypes.TASK:
                idNumber = refreshTask.getIdNumber();
                if (!tasksList.containsKey(idNumber)) {
                    return null;
                }
                tasksList.remove(idNumber);
                tasksList.put(idNumber, refreshTask);
                return idNumber;
            case TasksTypes.SUBTASK:
                idNumber = refreshTask.getIdNumber();
                if (!subTasksList.containsKey(idNumber)) {
                    return null;
                }
                int epicId = subTasksList.get(idNumber).getEpicIdNumber();
                subTasksList.remove(idNumber);
                SubTask refreshSubTask = (SubTask) refreshTask;
                refreshSubTask.setEpicIdNumber(epicId);
                subTasksList.put(idNumber, refreshSubTask);
                refreshEpicStatus(getSubTasksFromEpic(epicId), epicId);
                return idNumber;
            default:
                return null;
        }
    }

    public void deleteViaId(int taskId) {
        if (tasksList.containsKey(taskId)) {
            tasksList.remove(taskId);
        } else if (epicsList.containsKey(taskId)) {
            epicsList.remove(taskId);
        } else if (subTasksList.containsKey(taskId)) {
            int epicId = subTasksList.get(taskId).getEpicIdNumber();
            subTasksList.remove(taskId);
            refreshEpicStatus(getSubTasksFromEpic(epicId), epicId);
        } else {
            System.out.println("Нет задачи с таким id");
        }
    }

    public HashMap<Integer, SubTask> getSubTasksFromEpic(int epicTaskId) {
        HashMap<Integer, SubTask> response = new HashMap<>();
        if (!epicsList.containsKey(epicTaskId)) {
            System.out.println("Нет model.Epic с таким ID");
            return response;
        } else {
            for (SubTask subTask : subTasksList.values()) {
                if (subTask.getEpicIdNumber() == epicTaskId) {
                    response.put(subTask.getIdNumber(), subTask);
                }
            }
        }
        return response;
    }

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasksList.values());
    }
}
