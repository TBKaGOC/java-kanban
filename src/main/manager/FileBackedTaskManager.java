package main.manager;

import main.exception.ManagerSaveException;
import main.exception.MangerLoadException;
import main.model.*;

import java.io.*;

import static main.model.TaskStatus.getStatusFromString;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File fileToSave;
    private static final String exampleToSave = "id,type,name,status,description:::subtasksOfEpic";

    public FileBackedTaskManager(HistoryManager history, File fileToSave) {
        super(history);
        this.fileToSave = fileToSave;
    }

    private void save() {
        if (fileToSave != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(exampleToSave + "\n");

                for (Task task : getTasks().values()) {
                    writer.write(task.createStringToSave() + "\n");
                }

                for (EpicTask task : getEpicTasks().values()) {
                    writer.write(task.createStringToSave() + "\n");
                }
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

                        int idOfEpic = Integer.parseInt(elementsOfEpic[0]);
                        EpicTask epicTask = new EpicTask(elementsOfEpic[2], elementsOfEpic[4],
                                getStatusFromString(elementsOfEpic[3]), idOfEpic);

                        resultForLoad.addEpicTask(epicTask);
                        if (elements.length == 2) {
                            String[] subtasks = elements[1].split(";");
                            for (String subtask : subtasks) {
                                String[] elementsOfSubtask = subtask.split(",");
                                Subtask sub = new Subtask(elementsOfSubtask[2], elementsOfSubtask[4],
                                        getStatusFromString(elementsOfSubtask[3]), Integer.parseInt(elementsOfSubtask[0]));
                                resultForLoad.addSubtask(sub, idOfEpic);
                            }
                        }
                    } else {
                        String[] elements = task.split(",");
                        resultForLoad.addTask(new Task(elements[2], elements[4], getStatusFromString(elements[3]),
                                Integer.parseInt(elements[0])));
                    }
                }
            }
        } catch (IOException e) {
            throw new MangerLoadException(e);
        }

        resultForLoad.setFileToSave(file);

        return resultForLoad;
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
