package manager;

import model.Task;
import utility.TaskUtilities;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> history = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        TaskUtilities.addToEndList(history, task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) history.clone();
    }

    @Override
    public void remove() {
        history = new ArrayList<>(10);
    }
}
