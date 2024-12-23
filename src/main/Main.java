import controllers.FileBackedTaskManager;
import controllers.HttpTaskServer;
import model.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        Task task1 = new Task("Завтрак", "Приготовить завтрак", 23, StatusCodes.DONE,
                Duration.of(4, ChronoUnit.HOURS), LocalDateTime.of(2024, 12, 20, 10, 0, 0));
        Task task23 = new Task("Обед", "Приготовить обед", 43, StatusCodes.NEW,
                Duration.of(4, ChronoUnit.HOURS),
                LocalDateTime.of(2024, 12, 20, 15, 0, 0));
//        Task task2 = new Task("Обед", "Приготовить обед");
        Epic epic1 = new Epic("Написать код проекта", "Написать 2 проекта", 3,
                StatusCodes.NEW);
        SubTask subTask1 = new SubTask("Написать проект спринта 4", "Написать проект спринта 4",
                24, StatusCodes.NEW, 3, Duration.of(4, ChronoUnit.HOURS),
                LocalDateTime.of(2024, 12, 20, 22, 0, 0));
//        SubTask subTask2 = new SubTask("Написать проект спринта 5", "Написать проект спринта 5", 3);
//        Epic epic2 = new Epic("Заказать вещи","Заказать вещи");
//        SubTask subTask3 = new SubTask("Заказать шорты", "Заказать шорты", 6);
//        TaskManager taskManager = new InMemoryTaskManager();
//        taskManager.saveTask(task1);
//        taskManager.saveTask(task2);
//        taskManager.saveTask(epic1);
//        taskManager.saveTask(subTask1);
//        taskManager.saveTask(subTask2);
//        taskManager.saveTask(epic2);
//        taskManager.saveTask(subTask3);
//        taskManager.deleteViaId(1);
//        System.out.println(taskManager.getTasksList());
//        System.out.println(taskManager.getEpicsList());
//        System.out.println(taskManager.getSubTasksList());
//        SubTask subTask5 = new SubTask("Написан проект 4", "Написано!!", 4, StatusCodes.DONE,3);
//        taskManager.refreshTask(subTask5);
//        System.out.println(taskManager.getTasksList());
//        System.out.println(taskManager.getEpicsList());
//        System.out.println(taskManager.getSubTasksList());
//        SubTask subTask6 = new SubTask("Написан проект 5", "Написано!!", 5, StatusCodes.DONE,3);
//        taskManager.refreshTask(subTask6);
//        System.out.println(taskManager.getTasksList());
//        System.out.println(taskManager.getEpicsList());
//        System.out.println(taskManager.getSubTasksList());
//        SubTask subTask4 = new SubTask("Написать проект спринта 6", "Написать проект спринта 6", 3);
//        taskManager.saveTask(subTask4);
//        System.out.println(taskManager.getTasksList());
//        System.out.println(taskManager.getEpicsList());
//        System.out.println(taskManager.getSubTasksList());
//        taskManager.deleteAllTasksWithType(TasksTypes.SUBTASK);
//        System.out.println(taskManager.getTasksList());
//        System.out.println(taskManager.getEpicsList());
//        System.out.println(taskManager.getSubTasksList());
        Task task45 = new Task("Заffdвтрак", "Приготовить завтракfrr", 233, StatusCodes.DONE,
                Duration.of(4, ChronoUnit.HOURS),
                LocalDateTime.of(2024, 12, 21, 15, 0, 0));
        File file = new File("src/resources/test_sprint7.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.saveTask(task1);
        fileBackedTaskManager.saveTask(task23);
        fileBackedTaskManager.saveTask(epic1);
        fileBackedTaskManager.saveTask(subTask1);
//        fileBackedTaskManager.saveTask(epic2);
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

//        manager.saveTask(task45);
        System.out.println(manager.getEpicsList().get(0).getSubtasks());

        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
//        System.out.println(manager.getSubTasksList());

    }
}
