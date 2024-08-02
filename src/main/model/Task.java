package main.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private final String title;
    private final String description;
    private TaskStatus status;
    private int id;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    static final LocalDateTime nullLocalDateTime = LocalDateTime.of(0, 1, 1, 0,  0);

    public Task(String title,
                String description,
                TaskStatus status,
                int id,
                Duration duration,
                LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;


        if (duration != null && startTime != null) {
            this.duration = duration;
            this.startTime = startTime;
            endTime = startTime.plus(duration);
        } else {
            this.duration = Duration.ofMillis(0);
            this.startTime = nullLocalDateTime;
            this.endTime = nullLocalDateTime;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return this.id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String createStringToSave() {
        return getId() + ",TASK," + getTitle() + "," + getStatus() + "," + getDescription() + "," +
                duration.toSeconds() + "," + startTime.format(pattern);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", duration=" + getDuration().toSeconds() +
                ", startTime=" + getStartTime().format(pattern) +
                '}';
    }

    public static LocalDateTime getNullLocalDateTime() {
        return nullLocalDateTime;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }
}
