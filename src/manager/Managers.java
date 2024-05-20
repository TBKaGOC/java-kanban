package manager;

public class Managers {
    private static TaskManager taskManager = new InMemoryTaskManager();
    private static HistoryManager historyManager = new InMemoryHistoryManager();

    public Managers(TaskManager taskManager, HistoryManager historyManager) {
        Managers.taskManager = taskManager;
        Managers.historyManager = historyManager;
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
