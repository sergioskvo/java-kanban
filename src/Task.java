public class Task {
    public static int taskCount = 0;
    public String name;
    public String description;
    public int idNumber;
    public StatusCodes status;


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = StatusCodes.NEW;
        taskCountIncrease();
    }

    public Task(String name, String description, int idNumber, StatusCodes status) {
        this.name = name;
        this.description = description;
        this.idNumber = idNumber;
        this.status = status;
    }

    public static void taskCountIncrease() {
        taskCount++;
    }

    public static void taskCountDecrease() {
        taskCount--;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", idNumber=" + idNumber +
                ", status=" + status +
                '}';
    }
}
