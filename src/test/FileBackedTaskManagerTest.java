package test;

import main.exception.ManagerSaveException;
import main.exception.ManagerLoadException;
import main.manager.FileBackedTaskManager;
import main.manager.Managers;
import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void addAll() {
            manager = new FileBackedTaskManager(Managers.getDefaultHistory(), null);
        if (manager.getFileToSave() != null) {
            manager.getFileToSave().delete();
        }

        File file = null;
        try {
            file = File.createTempFile("save", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager.setFileToSave(file);

        super.addAll();
    }

    @Test
    public void shouldSaveAndLoadEqualsObject() {
        Task testTask = new Task("tt", "tt", TaskStatus.NEW,
                FileBackedTaskManager.getNewId(), Duration.ofSeconds(1), LocalDateTime.now().plusSeconds(10000));
        manager.addTask(testTask);
        manager = (FileBackedTaskManager) FileBackedTaskManager.loadFromFile(manager.getFileToSave());

        Assertions.assertTrue(manager.containsTask(testTask));
    }

    @Test
    public void shouldThrowManagerSaveExceptionIfSetIncorrectFile() {
        manager.setFileToSave(new File(""));

        Assertions.assertThrows(ManagerSaveException.class, () -> manager.save());
    }

    @Test
    public void shouldNotThrowManagerSaveExceptionIfSetCorrectFile() {
        Assertions.assertDoesNotThrow(() -> manager.save());
    }

    @Test
    public void shouldThrowManagerLoadExceptionIfSetEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("empty", ".txt");


            Assertions.assertThrows(ManagerLoadException.class,
                    () -> FileBackedTaskManager.loadFromFile(emptyFile));
    }

    @Test
    public void shouldNotThrowManagerLoadExceptionIfSetCorrectFile() {
        Assertions.assertDoesNotThrow(() -> {
            FileBackedTaskManager.loadFromFile(manager.getFileToSave());
        });
    }
}
