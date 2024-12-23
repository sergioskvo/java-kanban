package model;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private LocalDateTime epicEndTime;
    private final List<SubTask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Integer idNumber, StatusCodes status) {
        super(name, description, idNumber, status, Duration.ZERO, null);
    }

    public Epic() {
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return epicEndTime;
    }

    @Override
    public void setEndTime(LocalDateTime endTime) {
        this.epicEndTime = endTime;
    }

    public void recalculateEpicDetails(SubTask subtaskForEpic, int num) {
        if (num == -1) {
            subtasks.remove(subtaskForEpic);
        } else {
            subtasks.add(subtaskForEpic);
        }
        LocalDateTime earliestStartTime = null;
        LocalDateTime latestEndTime = null;
        Duration totalDuration = Duration.ZERO;

        for (SubTask subtask : subtasks) {
            if (subtask.getStartTime() != null) {
                if (earliestStartTime == null || subtask.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = subtask.getStartTime();
                }
                LocalDateTime subtaskEndTime = subtask.getEndTime();
                if (subtaskEndTime != null && (latestEndTime == null || subtaskEndTime.isAfter(latestEndTime))) {
                    latestEndTime = subtaskEndTime;
                }
            }
            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }
        setStartTime(earliestStartTime);
        if (earliestStartTime != null && latestEndTime != null) {
            setDuration(totalDuration);
            setEndTime(latestEndTime);
        } else {
            setDuration(Duration.ZERO);
            setEndTime(null);
        }
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
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                ", subtasksCount=" + subtasks.size() +
                '}';
    }
}
