package main.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    public Subtask(String title,
                   String description,
                   TaskStatus status,
                   int id,
                   Duration duration,
                   LocalDateTime startTime) {
        super(title, description, status, id, duration, startTime);
    }

    @Override
    public String createStringToSave() {
        return getId() + ",SUBTASK," + getTitle() + "," + getStatus() + "," + getDescription() + "," +
                getDuration().toSeconds() + "," + getStartTime().format(pattern);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", duration=" + getDuration().toSeconds() +
                ", startTime=" + getStartTime().format(pattern) +
                "}";
    }


}
