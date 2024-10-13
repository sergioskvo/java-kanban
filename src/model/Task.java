package model;

import model.StatusCodes;

public class Task {
    private String name;
    private String description;
    private int idNumber;
    private StatusCodes status;


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = StatusCodes.NEW;
    }

    public Task(String name, String description, int idNumber, StatusCodes status) {
        this.name = name;
        this.description = description;
        this.idNumber = idNumber;
        this.status = status;
    }

    public int getIdNumber() {
        return idNumber;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public StatusCodes getStatus() {
        return status;
    }

    public void setStatus(StatusCodes status) {
        this.status = status;
    }
    public TasksTypes getType() {
        return TasksTypes.TASK;
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", idNumber=" + idNumber +
                ", status=" + status +
                '}';
    }
}
