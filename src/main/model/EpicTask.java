package main.model;

import main.exception.IntersectionOfTasksException;
import main.manager.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class EpicTask extends Task {
    private Map<Integer, Subtask> subtasks;
    private Set<Subtask> sortedSubtask;

    public EpicTask(String title,
                    String description,
                    TaskStatus status,
                    int id,
                    Duration duration,
                    LocalDateTime startTime) {
        super(title, description, status, id, duration, startTime);
        subtasks = new HashMap<>();
        sortedSubtask = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        setStartAndEndTime();
        setDuration(Duration.ofMillis(0));
    }

    public void setStartAndEndTime() {
        if (!subtasks.isEmpty()) {
            Optional<Subtask> startTime = subtasks.values()
                    .stream()
                    .filter(e -> e.getStartTime() != null)
                    .min(Comparator.comparing(Task::getStartTime));

            if (startTime.isPresent()) {
                setStartTime(startTime.get().getStartTime());
            } else {
                setStartTime(nullLocalDateTime);
            }

            Optional<Subtask> endTime = subtasks.values()
                    .stream()
                    .filter(e -> e.getEndTime() != null)
                    .max(Comparator.comparing(Task::getEndTime));

            if (endTime.isPresent()) {
                setEndTime(endTime.get().getEndTime());
            } else {
                setEndTime(nullLocalDateTime);
            }
        } else {
            setStartTime(nullLocalDateTime);
            setEndTime(nullLocalDateTime);
        }
    }

    public void setDurationForEpic() {
        if (!subtasks.isEmpty()) {
            subtasks.values().forEach(e -> setDuration(getDuration().plus(e.getDuration())));
        } else {
            setDuration(Duration.ofSeconds(0));
        }
    }

    public void addSubtask(int id, Subtask subtask) {
        subtasks.put(id, subtask);
        setStartAndEndTime();
        setDurationForEpic();
        if (!subtask.getStartTime().equals(Subtask.getNullLocalDateTime())) {
            sortedSubtask.remove(subtask);
            sortedSubtask.forEach(el -> {
                if (InMemoryTaskManager.isTaskIntersect(subtask, el)) {
                    throw new IntersectionOfTasksException();
                }
            });
            sortedSubtask.add(subtask);
        }
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Set<Subtask> getSortedSubtask() {
        return sortedSubtask;
    }

    public void deleteAllSubtask() {
        if (!subtasks.isEmpty()) {
            subtasks = new HashMap<>();
        }
        setStartAndEndTime();
        setDurationForEpic();

        sortedSubtask = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            if (!subtasks.get(id).getStartTime().equals(getNullLocalDateTime())) {
                sortedSubtask.remove(subtasks.get(id));
            }
            subtasks.remove(id);
            setStartAndEndTime();
            setDurationForEpic();
        }
    }

    public boolean containsSubtask(int id) {
        return subtasks.containsKey(id);
    }

    public void updatingSubtask(int id, Subtask sub) {
        if (subtasks.containsKey(id)) {
            if (!sub.getStartTime().equals(getNullLocalDateTime())) {
                sortedSubtask.remove(sub);
                sortedSubtask.add(sub);
            }

            subtasks.replace(id, sub);
        }

        setStartAndEndTime();
        setDurationForEpic();
    }

    @Override
    public String createStringToSave() {
        StringBuilder builder = new StringBuilder(getId() + ",EPICTASK," + getTitle() + "," + getStatus() +
                "," + getDescription() + "," + getDuration().toSeconds() + "," +
                getStartTime().format(pattern) + ":::");

        subtasks.values().forEach(subtask -> builder.append(subtask.createStringToSave()).append(";"));
        return builder.toString();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", duration=" + getDuration().toSeconds() +
                ", startTime=" + getStartTime().format(pattern) +
                ", subtasks=" + subtasks +
                '}';
    }
}
