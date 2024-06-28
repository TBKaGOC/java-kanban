package main.model;

public enum TaskStatus {
    NEW,
    DONE,
    IN_PROGRESS;

    public static TaskStatus getStatusFromString(String status) {
        switch (status) {
            case "NEW":
                return NEW;
            case "DONE":
                return DONE;
            case "IN_PROGRESS":
                return IN_PROGRESS;
        }
        return null;
    }
}
