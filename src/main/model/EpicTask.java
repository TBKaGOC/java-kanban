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
            LocalDateTime startTime = subtasks.values()
                    .stream()
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder())
                    .orElse(nullLocalDateTime);

            setStartTime(startTime);

            LocalDateTime endTime = subtasks.values()
                    .stream()
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(nullLocalDateTime);

            setEndTime(endTime);
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

            try {
                for (Subtask t: sortedSubtask) {
                    if (InMemoryTaskManager.isTaskIntersect(subtask, t)) {
                        throw new IntersectionOfTasksException();
                    }
                }
            } catch (IntersectionOfTasksException e) {
                subtasks.remove(id);
                System.out.println("Пересечения времени выаолнения тасков не должно быть");
                return;
            }

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

    @Override
    public TaskType getType() {
        return TaskType.EPIC_TASK;
    }
}
