package utility;

import model.Task;

import java.util.List;

public class TaskUtilities {
    private static final int numberOfHistoryMember = 10;

    public static void addToEndList(List<Task> list, Task element) {
        if (list.size() < numberOfHistoryMember) {
            list.add(element);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (i != numberOfHistoryMember - 1) {
                    list.add(i, list.get(i + 1));
                } else {
                    list.add(i, element);
                }
            }
        }
    }
}
