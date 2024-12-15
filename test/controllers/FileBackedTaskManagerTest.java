package controllers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
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
            writer.write("id,type,name,status,description,epic\n");
            writer.write("1,TASK,Task1,NEW,Description task1,\n");
            writer.write("2,EPIC,Epic1,DONE,Description epic1,\n");
            writer.write("3,SUBTASK,Subtask1,DONE,Description subtask1,2\n");
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
        assertEquals(2, epics.get(0).getIdNumber());
        assertEquals("Epic1", epics.get(0).getName());
        assertEquals("DONE", epics.get(0).getStatus().name());
        assertEquals(3, subTasks.get(0).getIdNumber());
        assertEquals("Subtask1", subTasks.get(0).getName());
        assertEquals("DONE", subTasks.get(0).getStatus().name());
        assertEquals(2, subTasks.get(0).getEpicIdNumber());  // Проверка на epicId для Subtask
    }

    @Test
    void testSave() throws IOException {
        InMemoryTaskManager.taskId = 0;
        File tempFile = File.createTempFile("testSaveFile", ".csv");
        tempFile.deleteOnExit(); // чтобы удалить после завершения тестов
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Task1", "Description task1", 1, StatusCodes.NEW);
        Epic epic = new Epic("Epic1", "Description epic1", 2, StatusCodes.NEW);
        SubTask subTask = new SubTask("Subtask1", "Description subtask1", 3, StatusCodes.NEW, 2);
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
                if (line.contains("id,type,name,status,description,epic")) {
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

