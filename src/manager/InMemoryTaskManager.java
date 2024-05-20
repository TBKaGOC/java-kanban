package manager;

import model.*;
import utility.TaskUtilities;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager{
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, Subtask> subtasks;
    private ArrayList<Task> history = new ArrayList<>(10);
    private static int id = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Получение коллекций с задачами
    public HashMap<Integer, Task> getTasks() {
        return (HashMap<Integer, Task>) tasks.clone();
    }

    public HashMap<Integer, EpicTask> getEpicTasks() {
        return (HashMap<Integer, EpicTask>) epicTasks.clone();
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return (HashMap<Integer, Subtask>) subtasks.clone();
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
    @Override
    public Task getTask(int id) {
        TaskUtilities.addToEndList(history, tasks.get(id));
        return tasks.getOrDefault(id, null);
    }

    @Override
    public EpicTask getEpicTask(int id) {
        TaskUtilities.addToEndList(history, epicTasks.get(id));
        return epicTasks.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtask(int id) {
        TaskUtilities.addToEndList(history, subtasks.get(id));
        return subtasks.getOrDefault(id, null);
    }

    @Override
    //Создание задач
    public void addTask(Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    @Override
    public void addEpicTask(EpicTask epic) {
        int id = epic.getId();
        epicTasks.put(id, epic);
        setEpicTaskStatus(epic);
    }

    @Override
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
    @Override
    public void updatingTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.replace(id, task);
        }
    }

    @Override
    public void updatingEpicTask(EpicTask epic) {
        int id = epic.getId();
        if (epicTasks.containsKey(id)) {
            epicTasks.replace(id, epic);
        }
    }

    @Override
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
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) {
        epicTasks.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);

        for (EpicTask epic: epicTasks.values()) {
            epic.deleteSubtask(id);
            setEpicTaskStatus(epic);
        }
    }

    //Получение и отчищение истории
    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) history.clone();
    }

    //Определение статуса эпика
    public void setEpicTaskStatus(EpicTask task) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>(task.getSubtasks().values());
        if (!subtasksOfEpic.isEmpty()) {
            TaskStatus resultStatus = subtasksOfEpic.getFirst().getStatus();

            for (Subtask sub : subtasksOfEpic) {
                if (sub.getStatus() != resultStatus) {
                    resultStatus = TaskStatus.IN_PROGRESS;
                    break;
                }
            }

            task.setStatus(resultStatus);
        }
    }

    public void removeHistory() {
        history = new ArrayList<>();
    }

    public static int getNewId() {
        id++;
        return id;
    }

    //Проверка содержания
    public boolean containsTask(Task task) {
        return tasks.containsValue(task);
    }

    public boolean containsEpicTask(EpicTask task) {
        return epicTasks.containsValue(task);
    }

    public boolean containsSubtask(Subtask task) {
        return subtasks.containsValue(task);
    }
}
