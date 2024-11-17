package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    public void areTwoSubTaskWithTheSameIdEqual() {
        SubTask subTask1 = new SubTask("subTask1","subTask1", 3);
        subTask1.setIdNumber(1);
        subTask1.setStatus(StatusCodes.NEW);
        SubTask subTask2 = new SubTask("subTask2","subTask2", 4);
        subTask2.setIdNumber(1);
        subTask2.setStatus(StatusCodes.DONE);
        assertEquals(subTask1, subTask2);
    }

    @Test
    public void areTwoSubTaskWithDifferentIdNotEqual() {
        SubTask subTask1 = new SubTask("subTask1","subTask1", 3);
        subTask1.setIdNumber(2);
        subTask1.setStatus(StatusCodes.DONE);
        SubTask subTask2 = new SubTask("subTask1","subTask1", 3);
        subTask2.setIdNumber(1);
        subTask2.setStatus(StatusCodes.DONE);
        assertNotEquals(subTask1, subTask2);
    }

}