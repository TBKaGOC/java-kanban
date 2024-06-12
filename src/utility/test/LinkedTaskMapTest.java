package utility.test;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.LinkedTaskMap;

public class LinkedTaskMapTest {
    @Test
    public void shouldAddElementsForLinkedTaskMap() {
        LinkedTaskMap<Integer, Task> map = new LinkedTaskMap<>();
        Task t = new Task("t1", "newT", TaskStatus.NEW, 1);

        map.put(1, t);

        Assertions.assertTrue(map.containsKey(1));
    }

    @Test
    public void shouldRemoveElementsFromLinkedTaskMap() {
        LinkedTaskMap<Integer, Task> map = new LinkedTaskMap<>();
        Task t = new Task("t1", "newT", TaskStatus.NEW, 1);

        map.put(1, t);
        map.remove(1);

        Assertions.assertFalse(map.containsKey(1));
    }
}
