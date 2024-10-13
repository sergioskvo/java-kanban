package model;

import model.StatusCodes;
import model.Task;

public class SubTask extends Task {
    private int epicIdNumber;


    public SubTask(String name, String description, int epicIdNumber) {
        super(name, description);
        this.epicIdNumber = epicIdNumber;
    }

    public SubTask(String name, String description, int idNumber, StatusCodes status, int epicIdNumber) {
        super(name, description, idNumber, status);
        this.epicIdNumber = epicIdNumber;
    }

    public int getEpicIdNumber() {
        return epicIdNumber;
    }

    public void setEpicIdNumber(int epicIdNumber) {
        this.epicIdNumber = epicIdNumber;
    }

    @Override
    public TasksTypes getType() {
        return TasksTypes.SUBTASK;
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", idNumber=" + getIdNumber() +
                ", status=" + getStatus() +
                ", epicIdNumber=" + epicIdNumber +
                '}';
    }
}
