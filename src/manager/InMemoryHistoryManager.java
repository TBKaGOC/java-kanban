package manager;

import model.Task;
import utility.LinkedTaskMap;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedTaskMap<Integer, Task> history = new LinkedTaskMap<>();

    @Override
    public void add(Task task) {
        history.remove(task.getId());
        history.put(task.getId(), task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history.values();
    }

    @Override
    public void remove() {
        history = new LinkedTaskMap<>();
    }

    @Override
    public void remove(int id) {
        history.remove(id);
    }
}
