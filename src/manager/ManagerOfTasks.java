package manager;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ManagerOfTasks {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, Subtask> subtasks;
    private static int id = 0;

    public ManagerOfTasks() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Получение коллекций с задачами
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Subtask> getSubtasksOfEpic(int epicId) {
        if (epicTasks.containsKey(epicId)) {
            return epicTasks.get(epicId).getSubtasks();
        } else {
            return null;
        }
    }

    //Удаление всех задач
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks = new HashMap<>();
        }
    }

    public void deleteAllEpicTasks() {
        if (!epicTasks.isEmpty()) {
            epicTasks = new HashMap<>();
        }
        if (!subtasks.isEmpty()) {
            subtasks = new HashMap<>();
        }
    }

    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks = new HashMap<>();
            for (EpicTask epic: epicTasks.values()) {
                epic.deleteAllSubtask();
            }
        }

    }

    //Получение определённых задач
    public Task getTask(int id) {
        return tasks.getOrDefault(id, null);
    }

    public EpicTask getEpicTask(int id) {
        return epicTasks.getOrDefault(id, null);
    }

    public Subtask getSubtask(int id) {
        return subtasks.getOrDefault(id, null);
    }

    //Создание задач
    public void addTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    public void addEpicTask(EpicTask epic) {
        int id = epic.getId();
        epicTasks.put(id, epic);
        setEpicTaskStatus(epic);
    }

    public void addSubtask(Subtask sub, int epicTaskId) {
        int subId = sub.getId();
        if (epicTasks.containsKey(epicTaskId)) {
            EpicTask task = epicTasks.get(epicTaskId);
            task.addSubtask(subId, sub);
            setEpicTaskStatus(task);
            subtasks.put(subId, sub);
        }
    }

    //Обновление задачи
    public void updatingTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.replace(id, task);
        }
    }

    public void updatingEpicTask(EpicTask epic) {
        int id = epic.getId();
        if (epicTasks.containsKey(id)) {
            epicTasks.replace(id, epic);
        }
    }

    public void updatingSubtask(Subtask sub) {
        int id = sub.getId();
        if (subtasks.containsKey(id)) {
            subtasks.replace(id, sub);

            for (EpicTask epic: epicTasks.values()) {
                epic.updatingSubtask(id, sub);
            }
        }
    }

    //Удаление определённых задач
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpicTask(int id) {
        epicTasks.remove(id);
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);

        for (EpicTask epic: epicTasks.values()) {
            epic.deleteSubtask(id);
            setEpicTaskStatus(epic);
        }
    }

    //Определение статуса эпика
    public void setEpicTaskStatus(EpicTask task) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>(task.getSubtasks().values());
        if (!subtasksOfEpic.isEmpty()) {
            TaskStatus resultStatus = subtasksOfEpic.get(0).getStatus();

            for (Subtask sub : subtasksOfEpic) {
                if (sub.getStatus() != resultStatus) {
                    resultStatus = TaskStatus.IN_PROGRESS;
                    break;
                }
            }

            task.setStatus(resultStatus);
        }
    }

    public static int getNewId() {
        id++;
        return id;
    }
}
