public class SubTask extends Task {
    public String name;
    public String description;
    public int idNumber;
    public StatusCodes status;
    public int epicIdNumber;


    public SubTask(String name, String description, int epicIdNumber) {
        super(name, description);
        this.name = name;
        this.description = description;
        this.status = StatusCodes.NEW;
        this.epicIdNumber = epicIdNumber;
        taskCountIncrease();
    }

    public SubTask(String name, String description, int idNumber, StatusCodes status, int epicIdNumber) {
        super(name, description, idNumber, status);
        this.name = name;
        this.description = description;
        this.idNumber = idNumber;
        this.status = status;
        this.epicIdNumber = epicIdNumber;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", idNumber=" + idNumber +
                ", status=" + status +
                ", epicIdNumber=" + epicIdNumber +
                '}';
    }
}
