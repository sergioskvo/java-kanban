package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    public void areTwoTaskWithTheSameIdEqual() {
        Task Task1 = new Task("Task1","Task1");
        Task1.setIdNumber(1);
        Task1.setStatus(StatusCodes.NEW);
        Task Task2 = new Task("Task2","Task2");
        Task2.setIdNumber(1);
        Task2.setStatus(StatusCodes.DONE);
        assertEquals(Task1, Task2);
    }

    @Test
    public void areTwoTaskWithDifferentIdNotEqual() {
        Task Task1 = new Task("Task1","Task1");
        Task1.setIdNumber(2);
        Task1.setStatus(StatusCodes.NEW);
        Task Task2 = new Task("Task1","Task1");
        Task2.setIdNumber(1);
        Task2.setStatus(StatusCodes.NEW);
        assertNotEquals(Task1, Task2);
    }
}