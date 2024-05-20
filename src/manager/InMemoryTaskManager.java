package manager;

import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    private Map<Integer, Task> tasks;
    private Map<Integer, EpicTask> epicTasks;
    private Map<Integer, Subtask> subtasks;
    private static int id = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Получение коллекций с задачами
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public Map<Integer, Subtask> getSubtasksOfEpic(int epicId) {
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
        Managers.getDefaultHistory().add(tasks.get(id));
        return tasks.getOrDefault(id, null);
    }

    @Override
    public EpicTask getEpicTask(int id) {
        Managers.getDefaultHistory().add(epicTasks.get(id));
        return epicTasks.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtask(int id) {
        Managers.getDefaultHistory().add(subtasks.get(id));
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

        for (EpicTask epic : epicTasks.values()) {
            epic.deleteSubtask(id);
            setEpicTaskStatus(epic);
        }
    }

    //Определение статуса эпика
    public void setEpicTaskStatus(EpicTask task) {
        List<Subtask> subtasksOfEpic = new ArrayList<>(task.getSubtasks().values());
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

    public static int getNewId() {
        id++;
        return id;
    }

    @Override
    public boolean containsTask(Task task) {
        if (task instanceof EpicTask) {
            return epicTasks.containsValue(task);
        } else if (task instanceof Subtask) {
            return subtasks.containsValue(task);
        } else {
            return tasks.containsValue(task);
        }
    }
}
