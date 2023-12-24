package Managers;

import Tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public static FileBackedTasksManager  loadFromFile(File file) {
        try{
            String FileString = Files.readString(Path.of(file.getPath()));
            System.out.println(FileString);
        } catch(IOException e) {

        }
        return new FileBackedTasksManager(file);
    }
}
