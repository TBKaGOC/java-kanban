package main.model;

public enum TaskStatus {
    NEW,
    DONE,
    IN_PROGRESS;

    public static TaskStatus getStatusFromString(String status) {
        return switch (status) {
            case "NEW" -> NEW;
            case "DONE" -> DONE;
            case "IN_PROGRESS" -> IN_PROGRESS;
            default -> null;
        };
    }
}
