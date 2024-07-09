package test;

import main.manager.*;
import main.model.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @BeforeEach
    public void addAll() {
        manager = Managers.getDefault();

        super.addAll();
    }

}
