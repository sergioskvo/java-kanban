import java.util.ArrayList;

public class Epic extends Task {
    public String name;
    public String description;
    public int idNumber;
    public StatusCodes status;

    public Epic(String name, String description) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.status = StatusCodes.NEW;
        taskCountIncrease();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", idNumber=" + idNumber +
                ", status=" + status +
                '}';
    }
}
