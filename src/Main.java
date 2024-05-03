import manager.ManagerOfTasks;
import model.*;

public class Main {

    public static void main(String[] args) {
        //Тестирование функционала
        Task task1 = new Task("1task", "1thForExamination",
                TaskStatus.NEW, ManagerOfTasks.getNewId());
        Task task2 = new Task("2task", "2thForExamination",
                TaskStatus.IN_PROGRESS, ManagerOfTasks.getNewId());
        Task task3 = new Task("3task", "3thForExamination",
                TaskStatus.DONE, ManagerOfTasks.getNewId());

        EpicTask eTask1 = new EpicTask("1task", "1thForExamination",
                TaskStatus.NEW, ManagerOfTasks.getNewId());
        EpicTask eTask2 = new EpicTask("2task", "2thForExamination",
                TaskStatus.IN_PROGRESS, ManagerOfTasks.getNewId());
        EpicTask eTask3 = new EpicTask("3task", "3thForExamination",
                TaskStatus.DONE, ManagerOfTasks.getNewId());

        Subtask sTask1 = new Subtask("1task", "1thForExamination",
                TaskStatus.NEW, ManagerOfTasks.getNewId());
        Subtask sTask2 = new Subtask("2task", "2thForExamination",
                TaskStatus.DONE, ManagerOfTasks.getNewId());
        Subtask sTask3 = new Subtask("3task", "3thForExamination",
                TaskStatus.DONE, ManagerOfTasks.getNewId());

        ManagerOfTasks manager = new ManagerOfTasks();

        //Проверка добавления
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);

        manager.addEpicTask(eTask1);
        manager.addEpicTask(eTask2);
        manager.addEpicTask(eTask3);

        manager.addSubtask(sTask1, eTask1.getId());
        manager.addSubtask(sTask2, eTask1.getId());
        manager.addSubtask(sTask3, eTask1.getId());

        manager.addSubtask(sTask1, eTask2.getId());
        manager.addSubtask(sTask2, eTask2.getId());
        manager.addSubtask(sTask3, eTask2.getId());

        manager.addSubtask(sTask1, eTask3.getId());
        manager.addSubtask(sTask2, eTask3.getId());
        manager.addSubtask(sTask3, eTask3.getId());

        //Проверка вывода
        System.out.println(manager.getTasks() + "\n" + manager.getEpicTasks() + "\n" + manager.getSubtasks());
        System.out.println(manager.getSubtasksOfEpic(eTask1.getId()));

        System.out.println();

        //Проверка обновления
        Task new1 = new Task("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, 1);
        System.out.println(manager.getTask(1));
        manager.updatingTask(new1);
        System.out.println(manager.getTask(1));

        EpicTask new1E = new EpicTask("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, 4);
        System.out.println(manager.getEpicTask(4));
        manager.updatingEpicTask(new1E);
        System.out.println(manager.getEpicTask(4));

        Subtask new1S = new Subtask("1taskNew", "1thForExaminationNew",
                TaskStatus.IN_PROGRESS, 7);
        System.out.println(manager.getSubtask(7));
        manager.updatingSubtask(new1S);
        System.out.println(manager.getSubtask(7));

        System.out.println();

        //Проверка удаления
        manager.deleteTask(1);
        manager.deleteEpicTask(4);
        manager.deleteSubtask(7);
        System.out.println(manager.getTasks() + "\n" + manager.getEpicTasks() + "\n" + manager.getSubtasks());
        System.out.println(manager.getSubtasksOfEpic(eTask2.getId()));

        System.out.println();

        //Проверка полного удаления
        manager.deleteAllTasks();
        manager.deleteAllEpicTasks();
        manager.deleteAllSubtasks();
        System.out.println(manager.getTasks() + "\n" + manager.getEpicTasks() + "\n" + manager.getSubtasks());
    }
}
