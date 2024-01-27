package Managers;

import Tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CsvUtils {
    static String historyToString(HistoryManager manager) {
        String tasks = "";
        List<Task> taskList = manager.getHistory();
        Collections.reverse(taskList);
        for (Task task : taskList) {
            if (tasks.equals("")) {
                tasks = "" + task.getTaskId();
            } else {
                tasks = tasks + "," + task.getTaskId();
            }
        }
        return tasks;
    }

    static List<Integer> historyFromString(String value) {
        String[] array = value.split((","));
        Integer[] intArray = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            intArray[i] = Integer.parseInt(array[i]);
        }
        return new ArrayList<>(Arrays.asList(intArray));
    }

    public static Task fromString(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String[] taskArray = value.split(",");
        if (TaskTypes.valueOf(taskArray[1]).equals(TaskTypes.Task)) {
            return new Task(taskArray[2], taskArray[3], Statuses.valueOf(taskArray[4]), Integer.parseInt(taskArray[0])
                    , taskArray[5] != null ? LocalDateTime.parse(taskArray[5], formatter): null
                    , taskArray[6] != null ? Integer.parseInt(taskArray[6]): null);
        } else {
            if (TaskTypes.valueOf(taskArray[1]).equals(TaskTypes.Epic)) {
                return new Epic(taskArray[2], taskArray[3], Integer.parseInt(taskArray[0]));
            } else
                return new Subtask(taskArray[2], taskArray[3], Statuses.valueOf(taskArray[4]), Integer.parseInt(taskArray[5])
                        , Integer.parseInt(taskArray[0]), taskArray[6] != null ? LocalDateTime.parse(taskArray[6], formatter): null
                        , taskArray[7] != null ? Integer.parseInt(taskArray[7]): null);
        }
    }
}
