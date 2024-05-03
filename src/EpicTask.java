import java.util.HashMap;

public class EpicTask extends Task{
    private HashMap<Integer, Subtask> subtasks;
    public EpicTask(String title, String description, TaskStatus status, int code) {
        super(title, description, status, code);
        subtasks = new HashMap<>();
    }

    public void addSubtask(int code, Subtask subtask) {
        subtasks.put(code, subtask);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void deleteAllSubtask() {
        if (!subtasks.isEmpty()) {
            subtasks = new HashMap<>();
        }
    }

    public void deleteSubtask(int code) {
        if (subtasks.containsKey(code)) {
            subtasks.remove(code);
        }
    }

    public void updatingSubtask(int code, Subtask sub) {
        if (subtasks.containsKey(code)) {
            subtasks.replace(code, sub);
        }
    }
}
