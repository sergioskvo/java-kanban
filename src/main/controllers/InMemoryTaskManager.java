package controllers;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    public static int taskId = 0;
    private HashMap<Integer, Task> tasksList = new HashMap<>();
    private HashMap<Integer, Epic> epicsList = new HashMap<>();
    private HashMap<Integer, SubTask> subTasksList = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int saveTask(Task task) {
        task.setIdNumber(++taskId);
        tasksList.put(taskId, task);
        return taskId;
    }

    @Override
    public int saveTask(Epic epic) {
        epic.setIdNumber(++taskId);
        epicsList.put(taskId, epic);
        return taskId;
    }

    @Override
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

    @Override
    public void deleteAllTasksWithType(TasksTypes taskType) {
        switch (taskType) {
            case TasksTypes.TASK:
                tasksList.clear();
                break;
            case TasksTypes.EPIC:
                epicsList.clear();
                subTasksList.clear();
                break;
            case TasksTypes.SUBTASK:
                subTasksList.clear();
                for (Epic epic : epicsList.values()) {
                    epic.setStatus(StatusCodes.NEW);
                }
                break;
            default:
        }
    }

    @Override
    public Task getTaskViaId(int taskId) {
        if (tasksList.containsKey(taskId)) {
            historyManager.add(tasksList.get(taskId));
            return tasksList.get(taskId);
        } else if (epicsList.containsKey(taskId)) {
            historyManager.add(epicsList.get(taskId));
            return epicsList.get(taskId);
        } else if (subTasksList.containsKey(taskId)) {
            historyManager.add(subTasksList.get(taskId));
            return subTasksList.get(taskId);
        } else {
            return null;
        }
    }

    @Override
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
            case TasksTypes.EPIC:
                idNumber = refreshTask.getIdNumber();
                if (!epicsList.containsKey(idNumber)) {
                    return null;
                }
                Epic epicForRefresh = epicsList.get(idNumber);
                epicsList.remove(idNumber);
                epicForRefresh.setName(refreshTask.getName());
                epicForRefresh.setDescription(refreshTask.getDescription());
                epicsList.put(idNumber, epicForRefresh);
                return idNumber;
            default:
                return null;
        }
    }

    @Override
    public void deleteViaId(int taskId) {
        if (tasksList.containsKey(taskId)) {
            tasksList.remove(taskId);
        } else if (epicsList.containsKey(taskId)) {
            ArrayList<Integer> idSubTasksForDel = new ArrayList<>();
            for (SubTask subTask : subTasksList.values()) {
                if (subTask.getEpicIdNumber() == taskId) {
                    idSubTasksForDel.add(subTask.getIdNumber());
                }
            }
            for (int i : idSubTasksForDel) {
                subTasksList.remove(i);
            }
            epicsList.remove(taskId);
        } else if (subTasksList.containsKey(taskId)) {
            int epicId = subTasksList.get(taskId).getEpicIdNumber();
            subTasksList.remove(taskId);
            refreshEpicStatus(getSubTasksFromEpic(epicId), epicId);
        }
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasksFromEpic(int epicTaskId) {
        HashMap<Integer, SubTask> response = new HashMap<>();
        if (!epicsList.containsKey(epicTaskId)) {
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

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasksList.values());
    }
}
