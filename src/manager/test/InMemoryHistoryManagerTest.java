package manager.test;

import manager.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class InMemoryHistoryManagerTest {
    private static Task task1 = new Task("1task", "1thForExamination",
            TaskStatus.NEW, InMemoryTaskManager.getNewId());
    private static Task task2 = new Task("2task", "2thForExamination",
            TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId());
    private static Task task3 = new Task("3task", "3thForExamination",
            TaskStatus.DONE, InMemoryTaskManager.getNewId());

    private static EpicTask eTask1 = new EpicTask("1task", "1thForExamination",
            TaskStatus.NEW, InMemoryTaskManager.getNewId());
    private static EpicTask eTask2 = new EpicTask("2task", "2thForExamination",
            TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId());
    private static EpicTask eTask3 = new EpicTask("3task", "3thForExamination",
            TaskStatus.DONE, InMemoryTaskManager.getNewId());

    private static Subtask sTask1 = new Subtask("1task", "1thForExamination",
            TaskStatus.NEW, InMemoryTaskManager.getNewId());
    private static Subtask sTask2 = new Subtask("2task", "2thForExamination",
            TaskStatus.DONE, InMemoryTaskManager.getNewId());
    private static Subtask sTask3 = new Subtask("3task", "3thForExamination",
            TaskStatus.DONE, InMemoryTaskManager.getNewId());

    private static TaskManager manager = Managers.getDefault();

    @BeforeEach
    public void addAll() {
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
        manager.deleteAllSubtasks();
        Managers.getDefaultHistory().remove();

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        manager.addEpicTask(eTask1);
        manager.addEpicTask(eTask2);
        manager.addEpicTask(eTask3);

        manager.addSubtask(sTask1, eTask1.getId());
        manager.addSubtask(sTask2, eTask1.getId());
        manager.addSubtask(sTask3, eTask1.getId());

        manager.addSubtask(sTask1, eTask2.getId());
        manager.addSubtask(sTask2, eTask2.getId());
        manager.addSubtask(sTask3, eTask2.getId());

        manager.addSubtask(sTask1, eTask3.getId());
        manager.addSubtask(sTask2, eTask3.getId());
        manager.addSubtask(sTask3, eTask3.getId());
    }

    @Test
    public void shouldGetHistoryOfLastElements() {
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
}
