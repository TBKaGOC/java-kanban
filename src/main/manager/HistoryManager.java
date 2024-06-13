package main.manager;

import main.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove();

    void remove(int id);
}
