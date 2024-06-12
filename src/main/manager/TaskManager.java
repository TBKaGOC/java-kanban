package main.manager;

import main.model.*;

import java.util.Map;

public interface TaskManager {
    //Получение коллекций с задачами
    Map<Integer, Task> getTasks();

    Map<Integer, EpicTask> getEpicTasks();

    Map<Integer, Subtask> getSubtasks();

    Map<Integer, Subtask> getSubtasksOfEpic(int epicId);

    //Удаление всех задач
    void deleteAllTasks();

    void deleteAllEpicTasks();

    void deleteAllSubtasks();

    //Получение определённых задач
    Task getTask(int id);

    EpicTask getEpicTask(int id);

    Subtask getSubtask(int id);

    //Создание задач
    void addTask(Task task);

    void addEpicTask(EpicTask epic);

    void addSubtask(Subtask sub, int epicTaskId);

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
