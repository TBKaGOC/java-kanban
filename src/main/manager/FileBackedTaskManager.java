package main.manager;

import main.exception.ManagerSaveException;
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

    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("save", ".txt");
        TaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        Task t1 = new Task("t1", "newT1", TaskStatus.NEW, InMemoryTaskManager.getNewId());
        Task t2 = new Task("t2", "newT2", TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId());

        EpicTask e1 = new EpicTask("e1", "newE1", TaskStatus.NEW, InMemoryTaskManager.getNewId());
        EpicTask e2 = new EpicTask("e2", "newE2", TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId());

        Subtask s1 = new Subtask("s1", "newS1", TaskStatus.NEW, InMemoryTaskManager.getNewId());
        Subtask s2 = new Subtask("s2", "newS2", TaskStatus.IN_PROGRESS, InMemoryTaskManager.getNewId());
        Subtask s3 = new Subtask("s3", "newS3", TaskStatus.DONE, InMemoryTaskManager.getNewId());

        manager.addTask(t1);
        manager.addTask(t2);
        manager.addEpicTask(e1);
        manager.addEpicTask(e2);
        manager.addSubtask(s1, e1.getId());
        manager.addSubtask(s2, e1.getId());
        manager.addSubtask(s3, e1.getId());

        manager.getTask(t1.getId());
        manager.getTask(t2.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());
        manager.getTask(t2.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());
        manager.getEpicTask(e1.getId());
        manager.getTask(t1.getId());
        manager.getEpicTask(e1.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());
        manager.getSubtask(s3.getId());
        manager.getSubtask(s3.getId());
        manager.getTask(t1.getId());
        manager.getTask(t1.getId());

        TaskManager newManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println(newManager.equals(manager));
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

    public static TaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager res = new FileBackedTaskManager(Managers.getDefaultHistory(), null);

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

                        res.addEpicTask(epicTask);
                        
                        if (elements.length == 2) {
                            String[] subtasks = elements[1].split(";");
                            for (String subtask : subtasks) {
                                String[] elementsOfSubtask = subtask.split(",");
                                Subtask sub = new Subtask(elementsOfSubtask[2], elementsOfSubtask[4],
                                        getStatusFromString(elementsOfSubtask[3]), Integer.parseInt(elementsOfSubtask[0]));
                                res.addSubtask(sub, idOfEpic);
                            }
                        }
                    } else {
                        String[] elements = task.split(",");
                        res.addTask(new Task(elements[2], elements[4], getStatusFromString(elements[3]),
                                Integer.parseInt(elements[0])));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        res.setFileToSave(file);

        return res;
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
