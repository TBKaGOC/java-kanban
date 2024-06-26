package main.model;

import java.util.HashMap;
import java.util.Map;

public class EpicTask extends Task {
    private Map<Integer, Subtask> subtasks;

    public EpicTask(String title, String description, TaskStatus status, int id) {
        super(title, description, status, id);
        subtasks = new HashMap<>();
    }

    public void addSubtask(int id, Subtask subtask) {
        subtasks.put(id, subtask);
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void deleteAllSubtask() {
        if (!subtasks.isEmpty()) {
            subtasks = new HashMap<>();
        }
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    public boolean containsSubtask(int id) {
        return subtasks.containsKey(id);
    }

    public void updatingSubtask(int id, Subtask sub) {
        if (subtasks.containsKey(id)) {
            subtasks.replace(id, sub);
        }
    }

    @Override
    public String createStringToSave() {
        StringBuilder builder = new StringBuilder(getId() + ",EPICTASK," + getTitle() + "," + getStatus() +
                "," + getDescription() + ":::");

        for (Subtask subtask: subtasks.values()) {
            builder.append(subtask.createStringToSave() + ";");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                "subtasks=" + subtasks +
                '}';
    }
}
