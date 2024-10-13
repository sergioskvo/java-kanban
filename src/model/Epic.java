package model;

import model.StatusCodes;
import model.Task;

public class Epic extends Task {

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public TasksTypes getType() {
        return TasksTypes.EPIC;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", idNumber=" + getIdNumber() +
                ", status=" + getStatus() +
                '}';
    }
}
