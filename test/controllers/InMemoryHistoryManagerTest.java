package controllers;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {
    private HistoryManager historyManagerTest;

    @BeforeEach
    void beforeTest() {
        historyManagerTest = Managers.getDefaultHistory();
        SubTask testSubTask1 = new SubTask("SubTask1", "SubTask1", 3);
        testSubTask1.setIdNumber(1);
        SubTask testSubTask2 = new SubTask("SubTask2", "SubTask2", 1);
        testSubTask2.setIdNumber(2);
        SubTask testSubTask3 = new SubTask("SubTask3", "SubTask3", 2);
        testSubTask3.setIdNumber(3);
        Epic testEpic1 = new Epic("Epic1", "Epic1");
        testEpic1.setIdNumber(4);
        Epic testEpic2 = new Epic("Epic2", "Epic2");
        testEpic2.setIdNumber(5);
        Epic testEpic3 = new Epic("Epic3", "Epic3");
        testEpic3.setIdNumber(6);
        Epic testEpic4 = new Epic("Epic3", "Epic3");
        testEpic4.setIdNumber(7);
        Task testTask1 = new Task("Task1", "Task1");
        testTask1.setIdNumber(8);
        Task testTask2 = new Task("Task2", "Task2");
        testTask2.setIdNumber(9);
        Task testTask3 = new Task("Тест", "Тест");
        testTask3.setIdNumber(10);
        historyManagerTest.add(testSubTask1);
        historyManagerTest.add(testSubTask2);
        historyManagerTest.add(testSubTask3);
        historyManagerTest.add(testEpic1);
        historyManagerTest.add(testEpic2);
        historyManagerTest.add(testEpic3);
        historyManagerTest.add(testEpic4);
        historyManagerTest.add(testTask1);
        historyManagerTest.add(testTask2);
        historyManagerTest.add(testTask3);
    }

    @Test
    void getTasksTest() {
        ArrayList<Task> expectedResult = new ArrayList<>();
        SubTask testSubTask1 = new SubTask("SubTask1", "SubTask1", 3);
        SubTask testSubTask2 = new SubTask("SubTask2", "SubTask2", 1);
        SubTask testSubTask3 = new SubTask("SubTask3", "SubTask3", 2);
        Epic testEpic1 = new Epic("Epic1", "Epic1");
        Epic testEpic2 = new Epic("Epic2", "Epic2");
        Epic testEpic3 = new Epic("Epic3", "Epic3");
        Epic testEpic4 = new Epic("Epic3", "Epic3");
        Task testTask1 = new Task("Task1", "Task1");
        Task testTask2 = new Task("Task2", "Task2");
        Task testTask3 = new Task("Тест", "Тест");
        testSubTask1.setIdNumber(1);
        testSubTask2.setIdNumber(2);
        testSubTask3.setIdNumber(3);
        testEpic1.setIdNumber(4);
        testEpic2.setIdNumber(5);
        testEpic3.setIdNumber(6);
        testEpic4.setIdNumber(7);
        testTask1.setIdNumber(8);
        testTask2.setIdNumber(9);
        testTask3.setIdNumber(10);
        expectedResult.add(testSubTask1);
        expectedResult.add(testSubTask2);
        expectedResult.add(testSubTask3);
        expectedResult.add(testEpic1);
        expectedResult.add(testEpic2);
        expectedResult.add(testEpic3);
        expectedResult.add(testEpic4);
        expectedResult.add(testTask1);
        expectedResult.add(testTask2);
        expectedResult.add(testTask3);
        assertEquals(expectedResult, historyManagerTest.getTasks());
    }

    @Test
    void addMethodInHistoryManagerTest() {
        ArrayList<Task> expectedResult = new ArrayList<>();
        SubTask testSubTask1 = new SubTask("SubTask1", "SubTask1", 3);
        SubTask testSubTask2 = new SubTask("SubTask2", "SubTask2", 1);
        SubTask testSubTask3 = new SubTask("SubTask3", "SubTask3", 2);
        Epic testEpic1 = new Epic("Epic1", "Epic1");
        Epic testEpic2 = new Epic("Epic2", "Epic2");
        Epic testEpic3 = new Epic("Epic3", "Epic3");
        Epic testEpic4 = new Epic("Epic3", "Epic3");
        Task testTask1 = new Task("Task1", "Task1");
        Task testTask2 = new Task("Task2", "Task2");
        Task testTask3 = new Task("Тест", "Тест");
        testSubTask1.setIdNumber(1);
        testSubTask2.setIdNumber(2);
        testSubTask3.setIdNumber(3);
        testEpic1.setIdNumber(4);
        testEpic2.setIdNumber(5);
        testEpic3.setIdNumber(6);
        testEpic4.setIdNumber(7);
        testTask1.setIdNumber(8);
        testTask2.setIdNumber(9);
        testTask3.setIdNumber(10);
        expectedResult.add(testSubTask1);
        expectedResult.add(testSubTask2);
        expectedResult.add(testSubTask3);
        expectedResult.add(testEpic1);
        expectedResult.add(testEpic2);
        expectedResult.add(testEpic3);
        expectedResult.add(testEpic4);
        expectedResult.add(testTask1);
        expectedResult.add(testTask2);
        expectedResult.add(testTask3);
        Epic testEpic5 = new Epic("Epic5", "Epic5");
        expectedResult.add(testEpic5);
        historyManagerTest.add(testEpic5);
        assertEquals(expectedResult, historyManagerTest.getTasks());


    }
}