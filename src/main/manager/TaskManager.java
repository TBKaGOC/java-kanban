package main.manager;

import main.exception.IntersectionOfTasksException;
import main.model.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {
    //Получение коллекций с задачами
    Map<Integer, Task> getTasks();

    Map<Integer, EpicTask> getEpicTasks();

    Map<Integer, Subtask> getSubtasks();

    Map<Integer, Subtask> getSubtasksOfEpic(int epicId);

    Set<Task> getSortedTask();

    //Удаление всех задач
    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubtasks();

    //Получение определённых задач
    Optional<Task> getTask(int id);

    Optional<EpicTask> getEpicTask(int id);

    Optional<Subtask> getSubtask(int id);

    //Создание задач
    void addTask(Task task) throws IntersectionOfTasksException;

    void addEpicTask(EpicTask epic);

    void addSubtask(Subtask sub, int epicTaskId) throws IntersectionOfTasksException;

    //Обновление задачи
    void updatingTask(Task task);

    void updatingEpicTask(EpicTask epic);

    void updatingSubtask(Subtask s);

    //Удаление определённых задач
    void deleteTask(int id);

    void deleteEpicTask(int id);

    void deleteSubtask(int id);

    boolean containsTask(Task task);
}
