import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    public static int taskId = 0;
    private HashMap<Integer, Task> tasksList = new HashMap<>();
    private HashMap<Integer, Epic> epicsList = new HashMap<>();
    private HashMap<Integer, SubTask> subTasksList = new HashMap<>();
    Scanner scanner = new Scanner(System.in);

    public void createAndSaveTask(TasksTypes taskType) {
        String name;
        String description;
        int epicIdNumber;
        switch (taskType) {
            case TASK:
                System.out.println("Введите имя задачи:");
                name = scanner.nextLine();
                System.out.println("Введите описание задачи:");
                description = scanner.nextLine();
                Task task = new Task(name, description);
                saveTask(task);
                break;
            case EPIC:
                System.out.println("Введите имя эпика:");
                name = scanner.nextLine();
                System.out.println("Введите описание эпика:");
                description = scanner.nextLine();
                Epic epic = new Epic(name, description);
                saveTask(epic);
                break;
            case SUBTASK:
                System.out.println("Введите имя подзадачи:");
                name = scanner.nextLine();
                System.out.println("Введите описание подзадачи:");
                description = scanner.nextLine();
                System.out.println("Введите ID эпика, к которому подзадача должна относиться:");
                epicIdNumber = scanner.nextInt();
                SubTask subTask = new SubTask(name, description, epicIdNumber);
                saveTask(subTask);
                break;
            default:
                System.out.println("Нет такого типа задачи");

        }

    }

    public void saveTask(Task task) {
        task.idNumber = ++taskId;
        tasksList.put(taskId, task);
        System.out.println("Задача успешно добавлена");
    }

    public void saveTask(Epic epic) {
        epic.idNumber = ++taskId;
        epicsList.put(taskId, epic);
        System.out.println("Эпик успешно добавлен");
    }

    public void saveTask(SubTask subTask) {
        if (epicsList.containsKey(subTask.epicIdNumber)) {
            subTask.idNumber = ++taskId;
            subTasksList.put(taskId, subTask);
            HashMap<Integer, SubTask> subTasksFromEpic = getSubTasksFromEpic(subTask.epicIdNumber);
            refreshEpicStatus(subTasksFromEpic, subTask.epicIdNumber);
            System.out.println("SubTask успешно добавлен и прилинкован к эпику");
        } else {
            System.out.println("Как вы собираетесь добавить подзадачу, если Эпик еще не создан? Создайте сначала эпик");
        }
    }

    private void refreshEpicStatus(HashMap<Integer, SubTask> subTasksFromEpic, int epicIdNumber) {
        if (subTasksFromEpic.isEmpty() || isAllStatusEqual(StatusCodes.NEW, subTasksFromEpic)) {
            epicsList.get(epicIdNumber).status = StatusCodes.NEW;
        } else if (isAllStatusEqual(StatusCodes.DONE, subTasksFromEpic)) {
            epicsList.get(epicIdNumber).status = StatusCodes.DONE;
        } else {
            epicsList.get(epicIdNumber).status = StatusCodes.IN_PROGRESS;
        }
    }

    private boolean isAllStatusEqual(StatusCodes statusCodes, HashMap<Integer, SubTask> subTasksFromEpic) {
        int c = 0;
        int size = subTasksFromEpic.size();
        for (SubTask subTask : subTasksFromEpic.values()) {
            if (subTask.status == statusCodes) {
                c++;
            }
        }
        return size == c;
    }

    public void deleteAllTasksWithType(TasksTypes taskType) {
        switch (taskType) {
            case TASK:
                tasksList.clear();
                System.out.println("Список с обычными задачами очищен");
                break;
            case EPIC:
                epicsList.clear();
                subTasksList.clear();
                System.out.println("Список эпиков очищен вместе со списком подзадач (а иначе смысл подзадач без эпиков)");
                break;
            case SUBTASK:
                subTasksList.clear();
                for (Epic epic : epicsList.values()) {
                    epic.status = StatusCodes.NEW;
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

    public void refreshTask(TasksTypes taskType) {
        int idNumber;
        String name;
        String description;
        StatusCodes status;
        String str;
        switch (taskType) {
            case TASK:
                System.out.println("Введите ID задачи, которую хотите обновить:");
                idNumber = scanner.nextInt();
                scanner.nextLine();
                if (!tasksList.containsKey(idNumber)) {
                    System.out.println("Нет такой задачи для редактирования");
                    return;
                }
                System.out.println("Введите новое имя задачи:");
                name = scanner.nextLine();
                System.out.println("Введите новое описание задачи:");
                description = scanner.nextLine();
                System.out.println("Введите новый статус задачи:");
                str = scanner.nextLine().toUpperCase();
                status = StatusCodes.valueOf(str);
                Task taskFresh = new Task(name, description, idNumber, status);
                tasksList.remove(idNumber);
                tasksList.put(idNumber, taskFresh);
                break;
            case EPIC:
                System.out.println("Задачи типа EPIC редактировать таким образом нельзя");
                break;
            case SUBTASK:
                System.out.println("Введите ID задачи, которую хотите обновить:");
                idNumber = scanner.nextInt();
                scanner.nextLine();
                if (!subTasksList.containsKey(idNumber)) {
                    System.out.println("Нет такой задачи для редактирования");
                    return;
                }
                System.out.println("Введите новое имя задачи:");
                name = scanner.nextLine();
                System.out.println("Введите новое описание задачи:");
                description = scanner.nextLine();
                System.out.println("Введите новый статус задачи:");
                str = scanner.nextLine().toUpperCase();
                status = StatusCodes.valueOf(str);
                int epicId = subTasksList.get(idNumber).epicIdNumber;
                SubTask subTaskFresh = new SubTask(name, description, idNumber, status, epicId);
                subTasksList.remove(idNumber);
                subTasksList.put(idNumber, subTaskFresh);
                refreshEpicStatus(getSubTasksFromEpic(epicId), epicId);
                break;
            default:
                System.out.println("Нет такого типа задачи");
        }
    }

    public void deleteViaId(int taskId) {
        if (tasksList.containsKey(taskId)) {
            tasksList.remove(taskId);
        } else if (epicsList.containsKey(taskId)) {
            epicsList.remove(taskId);
        } else if (subTasksList.containsKey(taskId)) {
            int epicId = subTasksList.get(taskId).epicIdNumber;
            subTasksList.remove(taskId);
            refreshEpicStatus(getSubTasksFromEpic(epicId), epicId);
        } else {
            System.out.println("Нет задачи с таким id");
        }
    }

    public HashMap<Integer, SubTask> getSubTasksFromEpic(int epicTaskId) {
        HashMap<Integer, SubTask> response = new HashMap<>();
        if (!epicsList.containsKey(epicTaskId)) {
            System.out.println("Нет Epic с таким ID");
            return response;
        } else {
            for (SubTask subTask : subTasksList.values()) {
                if (subTask.epicIdNumber == epicTaskId) {
                    response.put(subTask.idNumber, subTask);
                }
            }
        }
        return response;
    }

    public HashMap<Integer, Task> getTasksList() {
        return tasksList;
    }

    public HashMap<Integer, Epic> getEpicsList() {
        return epicsList;
    }

    public HashMap<Integer, SubTask> getSubTasksList() {
        return subTasksList;
    }
}
