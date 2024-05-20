package manager.test;

import manager.*;
import model.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class InMemoryTaskManagerTest {

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
        Assertions.assertEquals(manager.getTask(task2.getId()), task2);
    }

    @Test
    public void shouldGetEpicTaskFromMemory() {
        Assertions.assertEquals(manager.getEpicTask(eTask2.getId()), eTask2);
    }

    @Test
    public void shouldGetSubtaskFromMemory() {
        Assertions.assertEquals(manager.getSubtask(sTask2.getId()), sTask2);
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
                TaskStatus.IN_PROGRESS, task1.getId());
        manager.updatingTask(new1);
        Assertions.assertEquals(manager.getTask(new1.getId()), new1);
    }

    @Test
    public void shouldUpdateEpicTaskInMemory() {
        EpicTask new1 = new EpicTask("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, eTask1.getId());
        manager.updatingEpicTask(new1);
        Assertions.assertEquals(manager.getEpicTask(new1.getId()), new1);
    }

    @Test
    public void shouldUpdateSubtaskInMemory() {
        Subtask new1 = new Subtask("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, sTask1.getId());
        manager.updatingSubtask(new1);
        Assertions.assertEquals(manager.getSubtask(new1.getId()), new1);
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
}
