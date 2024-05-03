import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private TaskStatus status;
    private final int code;

    public Task(String title, String description, TaskStatus status, int code) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return code == task.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }
}
