package main.manager;

import main.exception.ManagerSaveException;
import main.exception.ManagerLoadException;
import main.model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static main.model.TaskStatus.getStatusFromString;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File fileToSave;
    private static final String exampleToSave = "id,type,name,status,description," +
            "durationInSeconds,localDateTimePattern:::subtasksOfEpic";
    private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public FileBackedTaskManager(HistoryManager history, File fileToSave) {
        super(history);
        this.fileToSave = fileToSave;
    }

    public void save() {
        if (fileToSave != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(exampleToSave + "\n");

                getTasks().values().forEach(e -> {
                    try {
                        writer.write(e.createStringToSave() + "\n");
                    } catch (IOException ex) {
                        throw new ManagerSaveException(ex);
                    }
                });

                getEpicTasks().values().forEach(e -> {
                    try {
                        writer.write(e.createStringToSave() + "\n");
                    } catch (IOException ex) {
                        throw new ManagerSaveException(ex);
                    }
                });
            } catch (IOException e) {
                throw new ManagerSaveException(e);
            }
        }
    }

    public static TaskManager loadFromFile(File file) {
        FileBackedTaskManager resultForLoad = new FileBackedTaskManager(Managers.getDefaultHistory(), null);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String example = reader.readLine();
            if (example != null && example.equals(exampleToSave)) {
                while (reader.ready()) {
                    String task = reader.readLine();
                    if (task.contains(":::")) {
                        String[] elements = task.split(":::");
                        String[] elementsOfEpic = elements[0].split(",");

                        Duration duration = Duration.ofSeconds(Long.parseLong(elementsOfEpic[5]));
                        LocalDateTime startTime = LocalDateTime.parse(elementsOfEpic[6], pattern);
                        int idOfEpic = Integer.parseInt(elementsOfEpic[0]);
                        EpicTask epicTask = new EpicTask(elementsOfEpic[2], elementsOfEpic[4],
                                getStatusFromString(elementsOfEpic[3]), idOfEpic, duration, startTime);
                        getNewId();

                        resultForLoad.addEpicTask(epicTask);
                        if (elements.length == 2) {
                            List<String> subtasks = List.of(elements[1].split(";"));
                            subtasks.forEach(subtask -> {
                                Subtask sub = getSubtaskFromString(subtask);
                                getNewId();
                                resultForLoad.addSubtask(sub, idOfEpic);
                            });
                        }
                    } else {
                        String[] elements = task.split(",");
                        Duration duration = Duration.ofSeconds(Long.parseLong(elements[5]));
                        LocalDateTime start = LocalDateTime.parse(elements[6], pattern);
                        resultForLoad.addTask(new Task(elements[2], elements[4], getStatusFromString(elements[3]),
                                Integer.parseInt(elements[0]), duration, start));
                        getNewId();
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e);
        }

        if (resultForLoad.getTasks().isEmpty() && resultForLoad.getEpicTasks().isEmpty()) {
            throw new ManagerLoadException();
        }

        resultForLoad.setFileToSave(file);

        return resultForLoad;
    }

    private static Subtask getSubtaskFromString(String subtask) {
        String[] elementsOfSubtask = subtask.split(",");
        Duration durationOfSub = Duration.ofSeconds(Long.parseLong(elementsOfSubtask[5]));
        LocalDateTime startTimeOfSub = LocalDateTime.parse(elementsOfSubtask[6], pattern);
        return new Subtask(elementsOfSubtask[2], elementsOfSubtask[4],
                getStatusFromString(elementsOfSubtask[3]),
                Integer.parseInt(elementsOfSubtask[0]), durationOfSub, startTimeOfSub);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpicTasks() {
        super.deleteAllEpicTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epic) {
        super.addEpicTask(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask sub, int epicTaskId) {
        super.addSubtask(sub, epicTaskId);
        save();
    }

    @Override
    public void updatingTask(Task task) {
        super.updatingTask(task);
        save();
    }

    @Override
    public void updatingEpicTask(EpicTask epic) {
        super.updatingEpicTask(epic);
        save();
    }

    @Override
    public void updatingSubtask(Subtask sub) {
        super.updatingSubtask(sub);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpicTask(int id) {
        super.deleteEpicTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    public void setFileToSave(File fileToSave) {
        this.fileToSave = fileToSave;
    }

    public File getFileToSave() {
        return fileToSave;
    }
}
