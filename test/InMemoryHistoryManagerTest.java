package test;

import main.manager.*;
import main.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryHistoryManagerTest {
    private final Task task1 = new Task("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(1000));
    private final Task task2 = new Task("2task", "2thForExamination", TaskStatus.IN_PROGRESS,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(2000));
    private final Task task3 = new Task("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(3000));

    private final TaskManager manager = Managers.getDefault();

    @BeforeEach
    public void addAll() {
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
        manager.deleteAllSubtasks();
        Managers.getDefaultHistory().remove();

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
    }

    @Test
    public void shouldGetHistoryOfLastElements() {
        List<Task> history = new ArrayList<>();
        history.add(task2);
        history.add(task3);
        history.add(task1);


        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());

        Assertions.assertEquals(Managers.getDefaultHistory().getHistory(), history);
    }

    @Test
    public void shouldGetHistoryOfLastElementsWithRepeatingElement() {
        List<Task> history = new ArrayList<>();
        history.add(task2);
        history.add(task3);
        history.add(task1);


        manager.getTask(task2.getId());
        manager.getTask(task1.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());

        Assertions.assertEquals(Managers.getDefaultHistory().getHistory(), history);
    }

    @Test
    public void shouldGetEmptyHistory() {
        List<Task> history = new ArrayList<>();

        Assertions.assertEquals(Managers.getDefaultHistory().getHistory(), history);
    }

    @Test
    public void shouldGetHistoryOfLastElementsIfDeleteElementInCentre() {
        List<Task> history = new ArrayList<>();
        history.add(task2);
        history.add(task1);


        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());

        Managers.getDefaultHistory().remove(task3.getId());

        Assertions.assertEquals(Managers.getDefaultHistory().getHistory(), history);
    }

    @Test
    public void shouldGetHistoryOfLastElementsIfDeleteElementInHead() {
        List<Task> history = new ArrayList<>();
        history.add(task3);
        history.add(task1);


        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());

        Managers.getDefaultHistory().remove(task2.getId());

        Assertions.assertEquals(Managers.getDefaultHistory().getHistory(), history);
    }

    @Test
    public void shouldGetHistoryOfLastElementsIfDeleteElementInTail() {
        List<Task> history = new ArrayList<>();
        history.add(task2);
        history.add(task3);


        manager.getTask(task2.getId());
        manager.getTask(task3.getId());
        manager.getTask(task1.getId());

        Managers.getDefaultHistory().remove(task1.getId());

        Assertions.assertEquals(Managers.getDefaultHistory().getHistory(), history);
    }
}
