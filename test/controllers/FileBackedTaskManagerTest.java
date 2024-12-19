package controllers;

import exceptions.TaskOverlapException;
import model.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @Test
    void testLoadFromFile() throws IOException {
        InMemoryTaskManager.taskId = 0;
        tempFile = File.createTempFile("testFile", ".csv");
        tempFile.deleteOnExit(); // чтобы удалить после завершения тестов
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,type,name,status,description,startTime,duration,endTime,epicId\n");
            writer.write("1,TASK,Task1,NEW,Description task1,2024-12-20 15:00:00,240,2024-12-20 19:00:00,\n");
            writer.write("2,EPIC,Epic1,DONE,Description epic1,2024-12-21 15:00:00,240,2024-12-21 19:00:00,\n");
            writer.write("3,SUBTASK,Subtask1,DONE,Description subtask1,2024-12-22 15:00:00,240,2024-12-22 19:00:00,2\n");
        }
        manager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = manager.getTasksList();
        List<Epic> epics = manager.getEpicsList();
        List<SubTask> subTasks = manager.getSubTasksList();
        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subTasks.size());
        assertEquals(1, tasks.get(0).getIdNumber());
        assertEquals("Task1", tasks.get(0).getName());
        assertEquals("NEW", tasks.get(0).getStatus().name());
        assertEquals("2024-12-20 15:00:00", tasks.get(0).getStartTime().format(FileBackedTaskManager.formatter));
        assertEquals("2024-12-20 19:00:00", tasks.get(0).getEndTime().format(FileBackedTaskManager.formatter));
        assertEquals(Duration.ofHours(4), tasks.get(0).getDuration());
        assertEquals(2, epics.get(0).getIdNumber());
        assertEquals("Epic1", epics.get(0).getName());
        assertEquals("DONE", epics.get(0).getStatus().name());
        assertEquals("2024-12-22 15:00:00", epics.get(0).getStartTime().format(FileBackedTaskManager.formatter));
        assertEquals("2024-12-22 19:00:00", epics.get(0).getEndTime().format(FileBackedTaskManager.formatter));
        assertEquals(Duration.ofHours(4), epics.get(0).getDuration());
        assertEquals(3, subTasks.get(0).getIdNumber());
        assertEquals("Subtask1", subTasks.get(0).getName());
        assertEquals("DONE", subTasks.get(0).getStatus().name());
        assertEquals("2024-12-22 15:00:00", subTasks.get(0).getStartTime().format(FileBackedTaskManager.formatter));
        assertEquals("2024-12-22 19:00:00", subTasks.get(0).getEndTime().format(FileBackedTaskManager.formatter));
        assertEquals(Duration.ofHours(4), subTasks.get(0).getDuration());
        assertEquals(2, subTasks.get(0).getEpicIdNumber());  // Проверка на epicId для Subtask
    }

    @Test
    void testSave() throws IOException, TaskOverlapException {
        InMemoryTaskManager.taskId = 0;
        File tempFile = File.createTempFile("testSaveFile", ".csv");
        tempFile.deleteOnExit(); // чтобы удалить после завершения тестов
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task1", "Description task1", 1, StatusCodes.NEW, Duration.ofHours(3), LocalDateTime.of(2024, 12, 20, 10, 0, 0));
        Epic epic = new Epic("Epic1", "Description epic1", 2, StatusCodes.NEW);
        SubTask subTask = new SubTask("Subtask1", "Description subtask1", 3, StatusCodes.NEW, 2, Duration.ofHours(4), LocalDateTime.of(2024, 12, 20, 13, 0, 0));
        manager.saveTask(task);
        manager.saveTask(epic);
        manager.saveTask(subTask);
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubTasksList());
        System.out.println(manager.getTasksList());
        // Чекаем, что файл не пустой
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String line;
            boolean headerFound = false;
            boolean taskFound = false;
            boolean epicFound = false;
            boolean subTaskFound = false;

            while ((line = reader.readLine()) != null) {
                // Чекаем на наличие заголовка и данных
                if (line.contains("id,type,name,status,description,startTime,duration,endTime,epicId")) {
                    headerFound = true;
                }
                if (line.contains("Task1")) taskFound = true;
                if (line.contains("Epic1")) epicFound = true;
                if (line.contains("Subtask1")) subTaskFound = true;
            }
            assertTrue(headerFound);
            assertTrue(taskFound);
            assertTrue(epicFound);
            assertTrue(subTaskFound);
        }
    }
}

