package model;


import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicIdNumber;


    public SubTask(String name, String description, int epicIdNumber) {
        super(name, description);
        this.epicIdNumber = epicIdNumber;
    }

    public SubTask(String name, String description, Integer idNumber, StatusCodes status, int epicIdNumber,
                   Duration duration, LocalDateTime startTime) {
        super(name, description, idNumber, status, duration, startTime);
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
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                ", epicIdNumber=" + epicIdNumber +
                '}';
    }
}
