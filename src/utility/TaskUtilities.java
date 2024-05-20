package utility;

import model.Task;

import java.util.List;

public class TaskUtilities {
    public static void addToEndList(List<Task> list, Task element) {
        if (list.size() < 10) {
            list.add(element);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (i != 9) {
                    list.add(i, list.get(i + 1));
                } else {
                    list.add(i, element);
                }
            }
        }
    }
}
