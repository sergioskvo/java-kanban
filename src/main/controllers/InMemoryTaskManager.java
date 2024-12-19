package controllers;

import exceptions.TaskOverlapException;
import model.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public static int taskId = 0;
    private HashMap<Integer, Task> tasksList = new HashMap<>();
    private HashMap<Integer, Epic> epicsList = new HashMap<>();
    private HashMap<Integer, SubTask> subTasksList = new HashMap<>();
    private TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int saveTask(Task task) throws TaskOverlapException {
        if (isTaskOverlapping(task, prioritizedTasks)) {
            throw new TaskOverlapException("Задача пересекается с уже существующей задачей");
        }
        if (task.getIdNumber() == null) {
            task.setIdNumber(++taskId);
        }
        tasksList.put(task.getIdNumber(), task);
        updatePrioritizedTasks(task);
        return task.getIdNumber();
    }

    @Override
    public int saveTask(Epic epic) {
        if (epic.getIdNumber() == null) {
            epic.setIdNumber(++taskId);
        }
        epicsList.put(epic.getIdNumber(), epic);
        return epic.getIdNumber();
    }

    @Override
    public Integer saveTask(SubTask subTask) throws TaskOverlapException {
        if (isTaskOverlapping(subTask, prioritizedTasks)) {
            throw new TaskOverlapException("Подзадача пересекается с уже существующей задачей");
        }
        if (epicsList.containsKey(subTask.getEpicIdNumber())) {
            if (subTask.getIdNumber() == null) {
                subTask.setIdNumber(++taskId);
            }
            subTasksList.put(subTask.getIdNumber(), subTask);
            HashMap<Integer, SubTask> subTasksFromEpic = getSubTasksFromEpic(subTask.getEpicIdNumber());
            refreshEpicStatus(subTasksFromEpic, subTask.getEpicIdNumber());
            epicsList.get(subTask.getEpicIdNumber()).recalculateEpicDetails(subTask, 1);
            updatePrioritizedTasks(subTask);
            return subTask.getIdNumber();
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
        return subTasksFromEpic.values()
                .stream()
                .allMatch(subTask -> subTask.getStatus() == statusCodes);
    }

    @Override
    public void deleteAllTasksWithType(TasksTypes taskType) {
        switch (taskType) {
            case TasksTypes.TASK:
                deleteAllTasksByTypeInHistory(tasksList);
                tasksList.clear();
                break;
            case TasksTypes.EPIC:
                deleteAllTasksByTypeInHistory(epicsList);
                epicsList.clear();
                deleteAllTasksByTypeInHistory(subTasksList);
                subTasksList.clear();
                break;
            case TasksTypes.SUBTASK:
                deleteAllTasksByTypeInHistory(subTasksList);
                subTasksList.clear();
                epicsList.values().forEach(epic -> {
                    epic.setStatus(StatusCodes.NEW);
                    epic.getSubtasks().clear();
                });
                break;
            default:
        }
    }

    private void deleteAllTasksByTypeInHistory(HashMap<Integer, ? extends Task> tasksList) {
        tasksList.keySet().forEach(historyManager::remove);
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
        int idNumber = refreshTask.getIdNumber();
        TasksTypes taskType = refreshTask.getType();
        switch (taskType) {
            case TasksTypes.TASK:
                if (!tasksList.containsKey(idNumber)) {
                    return null;
                }
                tasksList.remove(idNumber);
                tasksList.put(idNumber, refreshTask);
                historyManager.add(refreshTask);
                return idNumber;
            case TasksTypes.SUBTASK:
                if (!subTasksList.containsKey(idNumber)) {
                    return null;
                }
                int epicId = subTasksList.get(idNumber).getEpicIdNumber();
                subTasksList.remove(idNumber);
                SubTask refreshSubTask = (SubTask) refreshTask;
                refreshSubTask.setEpicIdNumber(epicId);
                subTasksList.put(idNumber, refreshSubTask);
                historyManager.add(refreshSubTask);
                refreshEpicStatus(getSubTasksFromEpic(epicId), epicId);
                return idNumber;
            case TasksTypes.EPIC:
                if (!epicsList.containsKey(idNumber)) {
                    return null;
                }
                Epic epicForRefresh = epicsList.get(idNumber);
                epicsList.remove(idNumber);
                epicForRefresh.setName(refreshTask.getName());
                epicForRefresh.setDescription(refreshTask.getDescription());
                epicsList.put(idNumber, epicForRefresh);
                historyManager.add(epicForRefresh);
                return idNumber;
            default:
                return null;
        }
    }

    @Override
    public void deleteViaId(int taskId) {
        if (tasksList.containsKey(taskId)) {
            tasksList.remove(taskId);
            prioritizedTasks.remove(tasksList.get(taskId));
            historyManager.remove(taskId);
        } else if (epicsList.containsKey(taskId)) {
            List<SubTask> idSubTasksForDel = List.copyOf(epicsList.get(taskId).getSubtasks());
            idSubTasksForDel.forEach(subTask -> {
                subTasksList.remove(subTask.getIdNumber());
                prioritizedTasks.remove(subTask);
                historyManager.remove(subTask.getIdNumber());
            });
            epicsList.remove(taskId);
            prioritizedTasks.remove(epicsList.get(taskId));
            historyManager.remove(taskId);
        } else if (subTasksList.containsKey(taskId)) {
            int epicId = subTasksList.get(taskId).getEpicIdNumber();
            epicsList.get(epicId).recalculateEpicDetails(subTasksList.get(taskId), -1);
            subTasksList.remove(taskId);
            prioritizedTasks.remove(subTasksList.get(taskId));
            historyManager.remove(taskId);
            refreshEpicStatus(getSubTasksFromEpic(epicId), epicId);
        }
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasksFromEpic(int epicTaskId) {
        HashMap<Integer, SubTask> response = new HashMap<>();
        if (!epicsList.containsKey(epicTaskId)) {
            return response;
        } else {
            subTasksList.values().stream()
                    .filter(subTask -> subTask.getEpicIdNumber() == epicTaskId)
                    .forEach(subTask -> {
                        response.put(subTask.getIdNumber(), subTask);
                        historyManager.add(subTask);
                    });
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updatePrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private boolean isTaskOverlapping(Task newTask, Set<Task> existingTasks) {
        return existingTasks.stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, newTask));
    }

    private boolean isOverlapping(Task task1, Task task2) {
        Optional<LocalDateTime> task1Start = Optional.ofNullable(task1.getStartTime());
        Optional<LocalDateTime> task1End = Optional.ofNullable(task1.getEndTime());
        Optional<LocalDateTime> task2Start = Optional.ofNullable(task2.getStartTime());
        Optional<LocalDateTime> task2End = Optional.ofNullable(task2.getEndTime());
        return task1End.flatMap(end1 ->
                task2Start.flatMap(start2 ->
                        task2End.flatMap(end2 ->
                                task1Start.map(start1 ->
                                        end1.isAfter(start2) && end2.isAfter(start1)
                                )
                        )
                )
        ).orElse(false);
    }
}
