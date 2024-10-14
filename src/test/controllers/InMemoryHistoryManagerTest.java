package controllers;

import model.Epic;
import model.StatusCodes;
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
        SubTask testSubTask2 = new SubTask("SubTask2", "SubTask2", 1);
        SubTask testSubTask3 = new SubTask("SubTask3", "SubTask3", 2);
        Epic testEpic1 = new Epic("Epic1", "Epic1");
        Epic testEpic2 = new Epic("Epic2", "Epic2");
        Epic testEpic3 = new Epic("Epic3", "Epic3");
        Epic testEpic4 = new Epic("Epic3", "Epic3");
        Task testTask1 = new Task("Task1", "Task1");
        Task testTask2 = new Task("Task2", "Task2");
        Task testTask3 = new Task("Тест", "Тест");
        historyManagerTest.historyCashAddAndCheck(testSubTask1);
        historyManagerTest.historyCashAddAndCheck(testSubTask2);
        historyManagerTest.historyCashAddAndCheck(testSubTask3);
        historyManagerTest.historyCashAddAndCheck(testEpic1);
        historyManagerTest.historyCashAddAndCheck(testEpic2);
        historyManagerTest.historyCashAddAndCheck(testEpic3);
        historyManagerTest.historyCashAddAndCheck(testEpic4);
        historyManagerTest.historyCashAddAndCheck(testTask1);
        historyManagerTest.historyCashAddAndCheck(testTask2);
        historyManagerTest.historyCashAddAndCheck(testTask3);
    }

    @Test
    void getHistoryTest() {
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
        assertEquals(expectedResult, historyManagerTest.getHistory());
    }

    @Test
    void historyCashAddAndCheckTest() {
        ArrayList<Task> expectedResult = new ArrayList<>();
        SubTask testSubTask2 = new SubTask("SubTask2", "SubTask2", 1);
        SubTask testSubTask3 = new SubTask("SubTask3", "SubTask3", 2);
        Epic testEpic1 = new Epic("Epic1", "Epic1");
        Epic testEpic2 = new Epic("Epic2", "Epic2");
        Epic testEpic3 = new Epic("Epic3", "Epic3");
        Epic testEpic4 = new Epic("Epic3", "Epic3");
        Task testTask1 = new Task("Task1", "Task1");
        Task testTask2 = new Task("Task2", "Task2");
        Task testTask3 = new Task("Тест", "Тест");
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
        historyManagerTest.historyCashAddAndCheck(testEpic5);
        assertEquals(expectedResult, historyManagerTest.getHistory());


    }
}