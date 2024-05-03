import java.util.ArrayList;
import java.util.HashMap;

public class ManagerOfTasks {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, EpicTask> epicTasks;
    private HashMap<Integer, Subtask> subtasks;
    private static int code = 0;

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

    public HashMap<Integer, Subtask> getSubtasksOfEpic(int epicCode) {
        if (epicTasks.containsKey(epicCode)) {
            return epicTasks.get(code).getSubtasks();
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
    public Task getTask(int code) {
        return tasks.getOrDefault(code, null);
    }

    public EpicTask getEpicTask(int code) {
        return epicTasks.getOrDefault(code, null);
    }

    public Subtask getSubtask(int code) {
        return subtasks.getOrDefault(code, null);
    }

    //Создание задач
    public void addTask(Task task) {
        int code = task.getCode();
        tasks.put(code, task);
    }

    public void addEpicTask(EpicTask epic) {
        int code = epic.getCode();
        epicTasks.put(code, epic);
        setEpicTaskStatus(epic);
    }

    public void addSubtask(Subtask sub, int epicTaskCode) {
        int subCode = sub.getCode();
        if (epicTasks.containsKey(epicTaskCode)) {
            EpicTask task = epicTasks.get(epicTaskCode);
            task.addSubtask(subCode, sub);
            setEpicTaskStatus(task);
            subtasks.put(subCode, sub);
        }
    }

    //Обновление задачи
    public void updatingTask(Task task) {
        int code = task.getCode();
        if (tasks.containsKey(code)) {
            tasks.replace(code, task);
        }
    }

    public void updatingEpicTask(EpicTask epic) {
        int code = epic.getCode();
        if (epicTasks.containsKey(code)) {
            epicTasks.replace(code, epic);
        }
    }

    public void updatingSubtask(Subtask sub) {
        int code = sub.getCode();
        if (subtasks.containsKey(code)) {
            subtasks.replace(code, sub);

            for (EpicTask epic: epicTasks.values()) {
                epic.updatingSubtask(code, sub);
            }
        }
    }

    //Удаление определённых задач
    public void deleteTask(int code) {
        tasks.remove(code);
    }

    public void deleteEpicTask(int code) {
        HashMap<Integer, Subtask> subs = epicTasks.get(code).getSubtasks();
        for (Subtask s: subs.values()){
            subtasks.remove(s.getCode());
        }
        epicTasks.remove(code);
    }

    public void deleteSubtask(int code) {
        subtasks.remove(code);

        for (EpicTask epic: epicTasks.values()) {
            epic.deleteSubtask(code);
            setEpicTaskStatus(epic);
        }
    }

    //Определение статуса эпика
    public void setEpicTaskStatus(EpicTask task) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>(task.getSubtasks().values());
        boolean isAllSubNew = true;
        boolean isAllSubDone = true;

        for (Subtask sub: subtasksOfEpic) {
            if (sub.getStatus() != TaskStatus.NEW && !subtasksOfEpic.isEmpty()) {
                isAllSubNew = false;
            }
            if (sub.getStatus() != TaskStatus.DONE) {
                isAllSubDone = false;
            }
        }

        if (isAllSubNew) {
            task.setStatus(TaskStatus.NEW);
        } else if (isAllSubDone) {
            task.setStatus(TaskStatus.DONE);
        } else {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public static int getNewCode() {
        code++;
        return code;
    }
}
