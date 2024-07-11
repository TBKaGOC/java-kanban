package main;

import main.exception.IntersectionOfTasksException;
import main.manager.InMemoryTaskManager;
import main.manager.Managers;
import main.manager.TaskManager;
import main.model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IntersectionOfTasksException {
        TaskManager manager = Managers.getDefault();

        Task t1 = new Task("t1", "newT1", TaskStatus.NEW, InMemoryTaskManager.getNewId(), Duration.ofSeconds(234322), LocalDateTime.now());
        Task t2 = new Task("t2", "newT2", TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId(), Duration.ofSeconds(234322), LocalDateTime.now());

        EpicTask e1 = new EpicTask("e1", "newE1", TaskStatus.NEW, InMemoryTaskManager.getNewId(), Duration.ofSeconds(234322), LocalDateTime.now());
        EpicTask e2 = new EpicTask("e2", "newE2", TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId(), Duration.ofSeconds(234322), LocalDateTime.now());

        Subtask s1 = new Subtask("s1", "newS1", TaskStatus.NEW, InMemoryTaskManager.getNewId(), Duration.ofSeconds(234322), LocalDateTime.now());
        Subtask s2 = new Subtask("s2", "newS2", TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId(), Duration.ofSeconds(234322), LocalDateTime.now());
        Subtask s3 = new Subtask("s3", "newS3", TaskStatus.DONE, InMemoryTaskManager.getNewId(), Duration.ofSeconds(234322), LocalDateTime.now());

        manager.addTask(t1);
        manager.addTask(t2);
        manager.addEpicTask(e1);
        manager.addEpicTask(e2);
        manager.addSubtask(s1, e1.getId());
        manager.addSubtask(s2, e1.getId());
        manager.addSubtask(s3, e1.getId());

        manager.getTask(t1.getId());
        manager.getTask(t2.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());
        manager.getTask(t2.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());
        manager.getEpicTask(e1.getId());
        manager.getTask(t1.getId());
        manager.getEpicTask(e1.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());
        manager.getSubtask(s3.getId());
        manager.getSubtask(s3.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());
        System.out.println(Managers.getDefaultHistory().getHistory());

        manager.deleteTask(t2.getId());
        System.out.println(Managers.getDefaultHistory().getHistory());

        Managers.getDefaultHistory().remove();

        manager.getEpicTask(e1.getId());
        manager.getSubtask(s1.getId());
        manager.getSubtask(s2.getId());
        manager.getSubtask(s3.getId());

        System.out.println(Managers.getDefaultHistory().getHistory());

        manager.deleteEpicTask(e1.getId());

        System.out.println(Managers.getDefaultHistory().getHistory());
    }
}
