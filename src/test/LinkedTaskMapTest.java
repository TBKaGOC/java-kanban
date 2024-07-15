package test;

import main.model.Task;
import main.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import main.utility.LinkedTaskMap;

import java.time.Duration;
import java.time.LocalDateTime;

public class LinkedTaskMapTest {
    @Test
    public void shouldAddElementsForLinkedTaskMap() {
        LinkedTaskMap<Integer, Task> map = new LinkedTaskMap<>();
        Task t = new Task("t1", "newT", TaskStatus.NEW, 1, Duration.ofSeconds(234322), LocalDateTime.now());

        map.put(1, t);

        Assertions.assertTrue(map.containsKey(1));
    }

    @Test
    public void shouldRemoveElementsFromLinkedTaskMap() {
        LinkedTaskMap<Integer, Task> map = new LinkedTaskMap<>();
        Task t = new Task("t1", "newT", TaskStatus.NEW, 1, Duration.ofSeconds(234322), LocalDateTime.now());

        map.put(1, t);
        map.remove(1);

        Assertions.assertFalse(map.containsKey(1));
    }
}
