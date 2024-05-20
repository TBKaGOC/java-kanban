package manager;

import model.Task;
import utility.TaskUtilities;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        TaskUtilities.addToEndList(history, task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void remove() {
        history = new LinkedList<>();
    }
}
