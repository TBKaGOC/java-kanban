package main.manager;

import main.model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, EpicTask> epicTasks;
    private Map<Integer, Subtask> subtasks;
    private HistoryManager history;
    private static int id = 0;

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    //Получение коллекций с задачами
    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, EpicTask> getEpicTasks() {
        return epicTasks;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public Map<Integer, Subtask> getSubtasksOfEpic(int epicId) {
        if (epicTasks.containsKey(epicId)) {
            return epicTasks.get(epicId).getSubtasks();
        } else {
            return null;
        }
    }

    //Удаление всех задач
    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            for (Integer id: tasks.keySet()) {
                history.remove(id);
            }
            tasks = new HashMap<>();
        }
    }

    @Override
    public void deleteAllEpicTasks() {
        if (!epicTasks.isEmpty()) {
            for (Integer id: epicTasks.keySet()) {
                history.remove(id);
            }
            epicTasks = new HashMap<>();
        }
        if (!subtasks.isEmpty()) {
            for (Integer id: subtasks.keySet()) {
                history.remove(id);
            }
            subtasks = new HashMap<>();
        }
    }

    @Override
    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            for (Integer id: subtasks.keySet()) {
                history.remove(id);
            }
            subtasks = new HashMap<>();
            for (EpicTask epic: epicTasks.values()) {
                epic.deleteAllSubtask();
            }
        }

    }

    //Получение определённых задач
    @Override
    public Task getTask(int id) {
        history.add(tasks.get(id));
        return tasks.getOrDefault(id, null);
    }

    @Override
    public EpicTask getEpicTask(int id) {
        history.add(epicTasks.get(id));
        return epicTasks.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtask(int id) {
        history.add(subtasks.get(id));
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
                setEpicTaskStatus(epic);
            }
        }
    }

    //Удаление определённых задач
    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) {
        for (Subtask s: epicTasks.get(id).getSubtasks().values()) {
            subtasks.remove(s);
            history.remove(s.getId());
        }

        epicTasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);

        for (EpicTask epic : epicTasks.values()) {
            epic.deleteSubtask(id);
            setEpicTaskStatus(epic);
        }

        history.remove(id);
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
