package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void areTwoEpicWithTheSameIdEqual() {
        Epic epic1 = new Epic("1 epic","1 epic");
        epic1.setIdNumber(1);
        epic1.setStatus(StatusCodes.NEW);
        Epic epic2 = new Epic("2 epic","2 epic");
        epic2.setIdNumber(1);
        epic2.setStatus(StatusCodes.DONE);
        assertEquals(epic1, epic2);
    }

    @Test
    public void areTwoEpicWithDifferentIdNotEqual() {
        Epic epic1 = new Epic("1 epic","1 epic");
        epic1.setIdNumber(2);
        epic1.setStatus(StatusCodes.NEW);
        Epic epic2 = new Epic("1 epic","1 epic");
        epic2.setIdNumber(1);
        epic2.setStatus(StatusCodes.NEW);
        assertNotEquals(epic1, epic2);
    }
}

