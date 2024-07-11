package test;

import main.manager.FileBackedTaskManager;
import main.manager.InMemoryTaskManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.model.EpicTask;
import main.model.Subtask;
import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public abstract class TaskManagerTest<T extends TaskManager> {
    private final Task task1 = new Task("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(1000));
    private final Task task2 = new Task("2task", "2thForExamination", TaskStatus.IN_PROGRESS,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(2000));
    private final Task task3 = new Task("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(3000));

    private final EpicTask eTask1 = new EpicTask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(4000));
    private final EpicTask eTask2 = new EpicTask("2task", "2thForExamination", TaskStatus.IN_PROGRESS,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(5000));
    private final EpicTask eTask3 = new EpicTask("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(6000));

    private final Subtask sTask1 = new Subtask("1task", "1thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(7000));
    private final Subtask sTask2 = new Subtask("2task", "2thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(8000));
    private final Subtask sTask3 = new Subtask("3task", "3thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(9000));

    private final Subtask sTask4 = new Subtask("4task", "4thForExamination", TaskStatus.NEW,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(10000));
    private final Subtask sTask5 = new Subtask("5task", "5thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(11000));
    private final Subtask sTask6 = new Subtask("6task", "6thForExamination", TaskStatus.DONE,
            InMemoryTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().minusSeconds(12000));

    public T manager;

    @BeforeEach
    public void addAll() throws IOException {
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

        manager.addSubtask(sTask4, eTask2.getId());
        manager.addSubtask(sTask5, eTask2.getId());

        manager.addSubtask(sTask6, eTask3.getId());
    }

    @Test
    public void shouldAddTaskToMemory() {
        Assertions.assertTrue(manager.containsTask(task1));
    }

    @Test
    public void shouldAddEpicTaskToMemory() {
        Assertions.assertTrue(manager.containsTask(eTask1));
    }

    @Test
    public void shouldAddSubtaskToMemory() {
        Assertions.assertTrue(manager.containsTask(sTask1));
    }

    @Test
    public void shouldGetTaskFromMemory() {
        Assertions.assertEquals(manager.getTask(task2.getId()).get(), task2);
    }

    @Test
    public void shouldGetEpicTaskFromMemory() {
        Assertions.assertEquals(manager.getEpicTask(eTask2.getId()).get(), eTask2);
    }

    @Test
    public void shouldGetSubtaskFromMemory() {
        Assertions.assertEquals(manager.getSubtask(sTask2.getId()).get(), sTask2);
    }

    @Test
    public void shouldGetSortedTask() {
        Set<Task> taskSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        taskSet.add(eTask3);
        taskSet.add(eTask2);
        taskSet.add(eTask1);
        taskSet.add(task3);
        taskSet.add(task2);
        taskSet.add(task1);

        Assertions.assertEquals(manager.getSortedTask(), taskSet);
    }

    @Test
    public void shouldGetAllTaskFromMemory() {
        HashMap<Integer, Task> allTasks = new HashMap<>();
        allTasks.put(task1.getId(), task1);
        allTasks.put(task2.getId(), task2);
        allTasks.put(task3.getId(), task3);
        Assertions.assertEquals(manager.getTasks(), allTasks);
    }

    @Test
    public void shouldGetAllEpicTaskFromMemory() {
        HashMap<Integer, EpicTask> allEpics = new HashMap<>();
        allEpics.put(eTask1.getId(), eTask1);
        allEpics.put(eTask2.getId(), eTask2);
        allEpics.put(eTask3.getId(), eTask3);
        Assertions.assertEquals(manager.getEpicTasks(), allEpics);
    }

    @Test
    public void shouldGetAllSubtaskFromMemory() {
        HashMap<Integer, Subtask> allSubs = new HashMap<>();
        allSubs.put(sTask1.getId(), sTask1);
        allSubs.put(sTask2.getId(), sTask2);
        allSubs.put(sTask3.getId(), sTask3);
        allSubs.put(sTask4.getId(), sTask4);
        allSubs.put(sTask5.getId(), sTask5);
        allSubs.put(sTask6.getId(), sTask6);
        Assertions.assertEquals(manager.getSubtasks(), allSubs);
    }

    @Test
    public void shouldGetAllSubtaskOfEpicFromMemory() {
        HashMap<Integer, Subtask> allSubsOfEpic = new HashMap<>();
        allSubsOfEpic.put(sTask1.getId(), sTask1);
        allSubsOfEpic.put(sTask2.getId(), sTask2);
        allSubsOfEpic.put(sTask3.getId(), sTask3);
        Assertions.assertEquals(manager.getSubtasksOfEpic(eTask1.getId()), allSubsOfEpic);
    }

    @Test
    public void shouldUpdateTaskInMemory() {
        Task new1 = new Task("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, task1.getId(), Duration.ofSeconds(234322), LocalDateTime.now());
        manager.updatingTask(new1);
        Assertions.assertEquals(manager.getTask(new1.getId()).get(), new1);
    }

    @Test
    public void shouldUpdateEpicTaskInMemory() {
        EpicTask new1 = new EpicTask("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, eTask1.getId(), Duration.ofSeconds(234322), LocalDateTime.now());
        manager.updatingEpicTask(new1);
        Assertions.assertEquals(manager.getEpicTask(new1.getId()).get(), new1);
    }

    @Test
    public void shouldUpdateSubtaskInMemory() {
        Subtask new1 = new Subtask("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, sTask1.getId(), Duration.ofSeconds(234322), LocalDateTime.now());
        manager.updatingSubtask(new1);
        Assertions.assertEquals(manager.getSubtask(new1.getId()).get(), new1);
    }

    @Test
    public void shouldDeleteTaskInMemory() {
        manager.deleteTask(task1.getId());
        Assertions.assertFalse(manager.containsTask(task1));
    }

    @Test
    public void shouldDeleteEpicTaskInMemory() {
        manager.deleteEpicTask(eTask1.getId());
        Assertions.assertFalse(manager.containsTask(eTask1));
    }

    @Test
    public void shouldDeleteSubtaskInMemory() {
        manager.deleteSubtask(sTask1.getId());
        Assertions.assertFalse(manager.containsTask(sTask1));
    }

    @Test
    public void shouldEpicTaskContainsIdDeletedSubtask() {
        manager.deleteSubtask(sTask1.getId());
        Assertions.assertFalse(eTask1.containsSubtask(sTask1.getId()));
    }

    @Test
    public void shouldAddSubtaskWithoutEpicInTaskManager() {
        Subtask testSub = new Subtask("ts", "ts", TaskStatus.NEW, InMemoryTaskManager.getNewId(),
                Duration.ofSeconds(1), LocalDateTime.now());
        manager.addSubtask(testSub, InMemoryTaskManager.getNewId());

        Assertions.assertFalse(manager.containsTask(testSub));
    }

    @Test
    public void shouldCorrectSetEpicStatusWithTwoNewSubStatus() {
        Subtask testTask1 = new Subtask("tt1", "tt1", TaskStatus.NEW,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(10000));
        Subtask testTask2 = new Subtask("tt2", "tt2", TaskStatus.NEW,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(15000));

        EpicTask epicTaskForTest = new EpicTask("e", "e", TaskStatus.DONE,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(10000), LocalDateTime.now().plusSeconds(5000));

        manager.addEpicTask(epicTaskForTest);

        manager.addSubtask(testTask1, epicTaskForTest.getId());
        manager.addSubtask(testTask2, epicTaskForTest.getId());

        Assertions.assertEquals(epicTaskForTest.getStatus(), TaskStatus.NEW);
    }

    @Test
    public void shouldCorrectSetEpicStatusWithTwoDoneSubStatus() {
        Subtask testTask1 = new Subtask("tt1", "tt1", TaskStatus.DONE,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(10000));
        Subtask testTask2 = new Subtask("tt2", "tt2", TaskStatus.DONE,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(15000));

        EpicTask epicTaskForTest = new EpicTask("e", "e", TaskStatus.NEW,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(10000), LocalDateTime.now().plusSeconds(5000));

        manager.addEpicTask(epicTaskForTest);

        manager.addSubtask(testTask1, epicTaskForTest.getId());
        manager.addSubtask(testTask2, epicTaskForTest.getId());

        Assertions.assertEquals(epicTaskForTest.getStatus(), TaskStatus.DONE);
    }

    @Test
    public void shouldCorrectSetEpicStatusWithTwoInProgressSubStatus() {
        Subtask testTask1 = new Subtask("tt1", "tt1", TaskStatus.IN_PROGRESS,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(10000));
        Subtask testTask2 = new Subtask("tt2", "tt2", TaskStatus.IN_PROGRESS,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(15000));

        EpicTask epicTaskForTest = new EpicTask("e", "e", TaskStatus.DONE,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(10000), LocalDateTime.now().plusSeconds(5000));

        manager.addEpicTask(epicTaskForTest);

        manager.addSubtask(testTask1, epicTaskForTest.getId());
        manager.addSubtask(testTask2, epicTaskForTest.getId());

        Assertions.assertEquals(epicTaskForTest.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    public void shouldCorrectSetEpicStatusWithNewAndDoneSubStatus() {
        Subtask testTask1 = new Subtask("tt1", "tt1", TaskStatus.NEW,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(10000));
        Subtask testTask2 = new Subtask("tt2", "tt2", TaskStatus.DONE,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(15000));

        EpicTask epicTaskForTest = new EpicTask("e", "e", TaskStatus.NEW,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(10000), LocalDateTime.now().plusSeconds(5000));

        manager.addEpicTask(epicTaskForTest);

        manager.addSubtask(testTask1, epicTaskForTest.getId());
        manager.addSubtask(testTask2, epicTaskForTest.getId());

        Assertions.assertEquals(epicTaskForTest.getStatus(), TaskStatus.IN_PROGRESS);
    }
}
