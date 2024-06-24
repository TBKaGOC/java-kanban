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

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {
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
    private static FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), null);

    @BeforeEach
    public void addAll() throws IOException{
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
        manager.deleteAllSubtasks();
        if (manager.getFileToSave() != null) {
            manager.getFileToSave().delete();
        }

        File file = File.createTempFile("save", ".txt");

        manager.setFileToSave(file);

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
    public void shouldSaveAndLoadEqualsObject() {
        TaskManager manager1 = FileBackedTaskManager.loadFromFile(manager.getFileToSave());

        boolean equals = manager1.getTasks().equals(manager.getTasks());
        equals = equals && manager1.getSubtasks().equals(manager.getSubtasks());
        equals = equals && manager1.getEpicTasks().equals(manager.getEpicTasks());

        Assertions.assertTrue(equals);
    }
}
