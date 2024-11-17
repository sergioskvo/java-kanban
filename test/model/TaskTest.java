package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void areTwoTaskWithTheSameIdEqual() {
        Task task1 = new Task("Task1","Task1");
        task1.setIdNumber(1);
        task1.setStatus(StatusCodes.NEW);
        Task task2 = new Task("Task2","Task2");
        task2.setIdNumber(1);
        task2.setStatus(StatusCodes.DONE);
        assertEquals(task1, task2);
    }

    @Test
    public void areTwoTaskWithDifferentIdNotEqual() {
        Task task1 = new Task("Task1","Task1");
        task1.setIdNumber(2);
        task1.setStatus(StatusCodes.NEW);
        Task task2 = new Task("Task1","Task1");
        task2.setIdNumber(1);
        task2.setStatus(StatusCodes.NEW);
        assertNotEquals(task1, task2);
    }
}