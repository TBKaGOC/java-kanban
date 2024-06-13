package main.manager;

public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    public static TaskManager getDefault() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager(historyManager);
        }


        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
}
