package main.manager;

import main.exception.IntersectionOfTasksException;
import main.exception.NotFoundException;
import main.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, EpicTask> epicTasks;
    private Map<Integer, Subtask> subtasks;
    private Set<Task> sortedTasks;
    private final HistoryManager history;
    private static int id = 0;

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
    public Map<Integer, Subtask> getSubtasksOfEpic(int epicId) throws NotFoundException {
        if (epicTasks.containsKey(epicId)) {
            return epicTasks.get(epicId).getSubtasks();
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public Set<Task> getSortedTask() {
        return sortedTasks;
    }

    @Override
    public HistoryManager getHistory() {
        return history;
    }

    //Удаление всех задач
    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.keySet().forEach(history::remove);
            tasks = new HashMap<>();
        }

        Set<Task> newSet = sortedTasks.stream()
                .filter(e -> e.getType() != TaskType.TASK)
                .collect(Collectors.toSet());

        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        sortedTasks.addAll(newSet);
    }

    @Override
    public void deleteAllEpicTasks() {
        Set<Task> newSet = sortedTasks.stream()
                .filter(e -> e.getType() != TaskType.EPIC_TASK)
                .collect(Collectors.toSet());

        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        sortedTasks.addAll(newSet);

        if (!epicTasks.isEmpty()) {
            epicTasks.values().forEach(EpicTask::deleteAllSubtask);
            epicTasks.keySet().forEach(history::remove);
            epicTasks = new HashMap<>();
        }
        if (!subtasks.isEmpty()) {
            subtasks.keySet().forEach(history::remove);
            subtasks = new HashMap<>();
        }
    }

    @Override
    public void deleteAllSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.keySet().forEach(history::remove);
            subtasks = new HashMap<>();
            epicTasks.values().forEach(EpicTask::deleteAllSubtask);

            Set<Task> newSet = sortedTasks.stream()
                    .filter(e -> e.getType() != TaskType.EPIC_TASK)
                    .collect(Collectors.toSet());

            sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
            sortedTasks.addAll(newSet);
        }

    }

    //Получение определённых задач
    @Override
    public Optional<Task> getTask(int id) {
        history.add(tasks.get(id));
        if (tasks.get(id) == null) {
            return Optional.empty();
        }

        return Optional.of(tasks.get(id));
    }

    @Override
    public Optional<EpicTask> getEpicTask(int id) {
        history.add(epicTasks.get(id));
        if (epicTasks.get(id) == null) {
            return Optional.empty();
        }

        return Optional.of(epicTasks.get(id));
    }

    @Override
    public Optional<Subtask> getSubtask(int id) {
        history.add(subtasks.get(id));
        if (subtasks.get(id) == null) {
            return Optional.empty();
        }

        return Optional.of(subtasks.get(id));
    }

    @Override
    //Создание задач
    public void addTask(Task task) throws IntersectionOfTasksException {
        int id = task.getId();
        tasks.put(id, task);

        if (!task.getStartTime().equals(Task.getNullLocalDateTime())) {
            for (Task t : sortedTasks) {
                if (isTaskIntersect(task, t)) {
                    throw new IntersectionOfTasksException();
                }
            }

            sortedTasks.add(task);
        }
    }

    @Override
    public void addEpicTask(EpicTask epic) {
        int id = epic.getId();
        epicTasks.put(id, epic);
        setEpicTaskStatus(epic);
    }

    @Override
    public void addSubtask(Subtask sub, int epicTaskId) throws IntersectionOfTasksException {
        int subId = sub.getId();
        if (epicTasks.containsKey(epicTaskId)) {
            EpicTask task = epicTasks.get(epicTaskId);
            task.addSubtask(subId, sub);

            if (!task.getStartTime().equals(EpicTask.getNullLocalDateTime())) {
                sortedTasks.remove(task);

                for (Task t : sortedTasks) {
                    if (isTaskIntersect(task, t)) {
                        throw new IntersectionOfTasksException();
                    }
                }

                sortedTasks.add(task);
            }


            setEpicTaskStatus(task);
            subtasks.put(subId, sub);
        }
    }

    //Обновление задачи
    @Override
    public void updatingTask(Task task) throws IntersectionOfTasksException {
        if (tasks.containsKey(id)) {
            int id = task.getId();
            tasks.replace(id, task);

            if (!task.getStartTime().equals(Task.getNullLocalDateTime())) {
                sortedTasks.remove(task);
                for (Task t : sortedTasks) {
                    if (isTaskIntersect(task, t)) {
                        throw new IntersectionOfTasksException();
                    }
                }

                sortedTasks.add(task);
            }
        }
    }

    @Override
    public void updatingSubtask(Subtask sub) throws IntersectionOfTasksException {
        int id = sub.getId();
        if (subtasks.containsKey(id)) {
            subtasks.replace(id, sub);

            for (EpicTask task: epicTasks.values()) {
                task.updatingSubtask(id, sub);
                setEpicTaskStatus(task);

                if (task.containsSubtask(sub.getId()) && !task.getStartTime().equals(EpicTask.getNullLocalDateTime())) {
                    sortedTasks.remove(task);

                    for (Task t : sortedTasks) {
                        if (isTaskIntersect(task, t)) {
                            throw new IntersectionOfTasksException();
                        }
                    }

                    sortedTasks.add(task);
                }
            }
        }
    }

    //Удаление определённых задач
    @Override
    public void deleteTask(int id) {
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteEpicTask(int id) {
        epicTasks.get(id).getSubtasks().values().forEach(sub -> {
            subtasks.remove(sub.getId());
            history.remove(sub.getId());
        });

        sortedTasks.remove(epicTasks.get(id));
        epicTasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);

        epicTasks.values().forEach(epic -> {
            epic.deleteSubtask(id);
            setEpicTaskStatus(epic);
        });

        history.remove(id);
    }

    //Определение статуса эпика
    public void setEpicTaskStatus(EpicTask task) {
        List<Subtask> subtasksOfEpic = new ArrayList<>(task.getSubtasks().values());
        if (!subtasksOfEpic.isEmpty()) {
            TaskStatus firstStatus = subtasksOfEpic.getFirst().getStatus();

            TaskStatus resultStatus = subtasksOfEpic.stream()
                            .map(Task::getStatus)
                            .map(status -> {
                                if (status == firstStatus) {
                                    return status;
                                } else {
                                    return TaskStatus.IN_PROGRESS;
                                }
                            })
                            .filter(e -> e == TaskStatus.IN_PROGRESS)
                            .findFirst()
                            .orElse(firstStatus);


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

    public static boolean isTaskIntersect(Task newTask, Task savedTask) {
        if (newTask.equals(savedTask)) {
            return false;
        }

        if (newTask.getStartTime().isBefore(savedTask.getEndTime()) &&
                newTask.getEndTime().isAfter(savedTask.getStartTime())) {
            return true;
        }

        return savedTask.getStartTime().isBefore(newTask.getEndTime()) &&
                savedTask.getEndTime().isAfter(newTask.getStartTime());
    }
}
