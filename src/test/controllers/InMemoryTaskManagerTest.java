package controllers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager inMemoryTaskManager;
    private TaskManager inMemoryTaskManagerForList;

    @BeforeEach
    void beforeEach() {
        InMemoryTaskManager.taskId = 0;
        inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManagerForList = Managers.getDefault();
        SubTask testSubTask1 = new SubTask("SubTask1", "SubTask1", 3);
        SubTask testSubTask2 = new SubTask("SubTask2", "SubTask2", 1);
        SubTask testSubTask3 = new SubTask("SubTask3", "SubTask3", 2);
        Epic testEpic1 = new Epic("Epic1", "Epic1");
        Epic testEpic2 = new Epic("Epic2", "Epic2");
        Epic testEpic3 = new Epic("Epic3", "Epic3");
        inMemoryTaskManagerForList.saveTask(testEpic1);
        inMemoryTaskManagerForList.saveTask(testEpic2);
        inMemoryTaskManagerForList.saveTask(testEpic3);
        inMemoryTaskManagerForList.saveTask(testSubTask1);
        inMemoryTaskManagerForList.saveTask(testSubTask2);
        inMemoryTaskManagerForList.saveTask(testSubTask3);
        Task testTask1 = new Task("Task1", "Task1");
        Task testTask2 = new Task("Task2", "Task2");
        inMemoryTaskManagerForList.saveTask(testTask1);
        inMemoryTaskManagerForList.saveTask(testTask2);
    }

    @Test
    void saveTask() {
        Task task1 = new Task("Task1", "Task1");
        Task task2 = new Task("Task2", "Task2");
        inMemoryTaskManager.saveTask(task1);
        inMemoryTaskManager.saveTask(task2);
        assertEquals(2, inMemoryTaskManager.getTasksList().size());
    }

    @Test
    void testSaveSubTaskWithoutEpic() {
        SubTask task1 = new SubTask("SubTask1", "SubTask1", 3);
        SubTask task2 = new SubTask("SubTask2", "SubTask2", 4);
        SubTask task3 = new SubTask("SubTask3", "SubTask3", 5);
        inMemoryTaskManager.saveTask(task1);
        inMemoryTaskManager.saveTask(task2);
        inMemoryTaskManager.saveTask(task3);
        assertEquals(0, inMemoryTaskManager.getSubTasksList().size());
    }

    @Test
    void testSaveSubTaskWith2Epic() {
        SubTask task1 = new SubTask("SubTask1", "SubTask1", 9);
        SubTask task2 = new SubTask("SubTask2", "SubTask2", 144);
        SubTask task3 = new SubTask("SubTask3", "SubTask3", 10);
        Epic epic1 = new Epic("Task1", "Task1");
        Epic epic2 = new Epic("Task2", "Task2");
        Epic epic3 = new Epic("Task3", "Task3");
        inMemoryTaskManager.saveTask(epic1);
        inMemoryTaskManager.saveTask(epic2);
        inMemoryTaskManager.saveTask(epic3);
        inMemoryTaskManager.saveTask(task1);
        inMemoryTaskManager.saveTask(task2);
        inMemoryTaskManager.saveTask(task3);
        assertEquals(2, inMemoryTaskManager.getSubTasksList().size());
    }

    @Test
    void testSaveEpic() {
        Epic epic1 = new Epic("Task1", "Task1");
        Epic epic2 = new Epic("Task2", "Task2");
        Epic epic3 = new Epic("Task3", "Task3");
        inMemoryTaskManager.saveTask(epic1);
        inMemoryTaskManager.saveTask(epic2);
        inMemoryTaskManager.saveTask(epic3);
        assertEquals(3, inMemoryTaskManager.getEpicsList().size());
    }

    @Test
    void deleteAllTasksWithType() {
        int listSizePre = inMemoryTaskManagerForList.getEpicsList().size();
        assertEquals(3, listSizePre);
        inMemoryTaskManagerForList.deleteAllTasksWithType(TasksTypes.EPIC);
        int listSizeAfter = inMemoryTaskManagerForList.getEpicsList().size();
        assertEquals(0, listSizeAfter);

    }

    @Test
    void getTaskViaIdThatExists() {
        Task expectedResult = new Task("Тест", "Тест");
        expectedResult.setIdNumber(7);
        assertEquals(expectedResult, inMemoryTaskManagerForList.getTaskViaId(7));
    }

    @Test
    void getTaskViaIdThatDoesNotExists() {
        assertNull(inMemoryTaskManagerForList.getTaskViaId(55484758));
    }

    @Test
    void refreshTaskThatExists() {
        Task expectedResult = new Task("Тест", "Тест");
        expectedResult.setIdNumber(8);
        expectedResult.setStatus(StatusCodes.DONE);
        assertEquals(8, inMemoryTaskManagerForList.refreshTask(expectedResult));
        assertEquals(StatusCodes.DONE, inMemoryTaskManagerForList.getTaskViaId(8).getStatus());

    }

    @Test
    void refreshEpicTaskThatExists() {
        Epic expectedResult = new Epic("Тест", "Тест описание");
        expectedResult.setIdNumber(3);
        expectedResult.setStatus(StatusCodes.NEW);
        assertEquals(3, inMemoryTaskManagerForList.refreshTask(expectedResult));
        assertEquals("Тест", inMemoryTaskManagerForList.getTaskViaId(3).getName());
        assertEquals("Тест описание", inMemoryTaskManagerForList.getTaskViaId(3).getDescription());
    }

    @Test
    void refreshTaskThatDoesNotExists() {
        Task expectedResult = new Task("Тест", "Тест");
        expectedResult.setIdNumber(10004);
        expectedResult.setStatus(StatusCodes.DONE);
        assertNotEquals(10004, inMemoryTaskManagerForList.refreshTask(expectedResult));
        assertNull(inMemoryTaskManagerForList.refreshTask(expectedResult));
    }

    @Test
    void deleteViaId() {
        inMemoryTaskManagerForList.deleteViaId(3);
        assertNull(inMemoryTaskManagerForList.getTaskViaId(4));
        assertNull(inMemoryTaskManagerForList.getTaskViaId(3));
    }

    @Test
    void getSubTasksFromEpicThatExists() {
        SubTask expectSubTask1 = new SubTask("SubTask1", "SubTask1", 3);
        expectSubTask1.setIdNumber(4);
        expectSubTask1.setStatus(StatusCodes.NEW);
        HashMap<Integer, SubTask> expectResult = new HashMap<>();
        expectResult.put(4, expectSubTask1);
        assertEquals(expectResult, inMemoryTaskManagerForList.getSubTasksFromEpic(3));
    }

    @Test
    void getSubTasksFromEpicThatDoesNotExists() {
        HashMap<Integer, SubTask> expectResult = new HashMap<>();
        assertEquals(expectResult, inMemoryTaskManagerForList.getSubTasksFromEpic(73438473));
    }

    @Test
    void getTasksListTest() {
        Task testTask1 = new Task("Task1", "Task1");
        Task testTask2 = new Task("Task2", "Task2");
        testTask1.setStatus(StatusCodes.NEW);
        testTask2.setStatus(StatusCodes.NEW);
        testTask1.setIdNumber(7);
        testTask2.setIdNumber(8);
        ArrayList<Task> expectedResult = new ArrayList<>();
        expectedResult.add(testTask1);
        expectedResult.add(testTask2);
        assertEquals(expectedResult, inMemoryTaskManagerForList.getTasksList());
    }

    @Test
    void getEpicsListTest() {
        Epic testEpic1 = new Epic("Epic1", "Epic1");
        Epic testEpic2 = new Epic("Epic2", "Epic2");
        Epic testEpic3 = new Epic("Epic3", "Epic3");
        testEpic1.setStatus(StatusCodes.NEW);
        testEpic2.setStatus(StatusCodes.NEW);
        testEpic3.setStatus(StatusCodes.NEW);
        testEpic1.setIdNumber(1);
        testEpic2.setIdNumber(2);
        testEpic3.setIdNumber(3);
        ArrayList<Epic> expectedResult = new ArrayList<>();
        expectedResult.add(testEpic1);
        expectedResult.add(testEpic2);
        expectedResult.add(testEpic3);
        assertEquals(expectedResult, inMemoryTaskManagerForList.getEpicsList());
    }

    @Test
    void getSubTasksList() {
        SubTask testSubTask1 = new SubTask("SubTask1", "SubTask1", 3);
        SubTask testSubTask2 = new SubTask("SubTask2", "SubTask2", 1);
        SubTask testSubTask3 = new SubTask("SubTask3", "SubTask3", 2);
        testSubTask1.setStatus(StatusCodes.NEW);
        testSubTask2.setStatus(StatusCodes.NEW);
        testSubTask3.setStatus(StatusCodes.NEW);
        testSubTask1.setIdNumber(4);
        testSubTask2.setIdNumber(5);
        testSubTask3.setIdNumber(6);
        ArrayList<SubTask> expectedResult = new ArrayList<>();
        expectedResult.add(testSubTask1);
        expectedResult.add(testSubTask2);
        expectedResult.add(testSubTask3);
        assertEquals(expectedResult, inMemoryTaskManagerForList.getSubTasksList());
    }
}