package model;

public class Subtask extends Task {
    public Subtask(String title, String description, TaskStatus status, int id) {
        super(title, description, status, id);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                "}";
    }
}
