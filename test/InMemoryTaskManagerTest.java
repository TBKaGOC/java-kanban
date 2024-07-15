package test;

import main.manager.*;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @BeforeEach
    public void addAll() throws Exception {
        manager = Managers.getDefault();
        super.addAll();
    }
}
