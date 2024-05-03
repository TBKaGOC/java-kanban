public class Subtask extends Task{
    public Subtask(String title, String description, TaskStatus status, int code) {
        super(title, description, status, code);
    }

    @Override
    public String toString() {
        return "Subtask{}";
    }
}
