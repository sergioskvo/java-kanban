package model;


public class Epic extends Task {

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int idNumber, StatusCodes status) {
        super(name, description, idNumber, status);
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
