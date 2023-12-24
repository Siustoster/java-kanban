package Managers;

import Tasks.*;

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

    public static FileBackedTasksManager loadFromFile(File file) {
        try {
            FileBackedTasksManager manager = new FileBackedTasksManager(file);

            String fileString = Files.readString(Path.of(file.getPath()));
            String[] lines = fileString.split("\\r\\n");

            if (lines[0].isBlank()) {
                System.out.println("Ошибка загрузки данных из файла - файл пустой");
                return manager;
            }

            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    List<Integer> historyIds = CsvUtils.historyFromString(lines[i + 1]);
                    for (int id : historyIds) {
                        if (manager.taskList.containsKey(id)) {
                            manager.historyManager.add(manager.taskList.get(id));
                        } else if (manager.epicList.containsKey(id)) {
                            manager.historyManager.add(manager.epicList.get(id));
                        } else {
                            manager.historyManager.add(manager.subTaskList.get(id));
                        }
                    }
                    return manager;
                } else {
                    if (!lines[i].equals("id,type,name,description,status,epic")) {
                        String[] line = lines[i].split(",");
                        if (TaskTypes.valueOf(line[1]).equals(TaskTypes.Task)) {
                            manager.createTask(new Task(line[2], line[3], Statuses.valueOf(line[4]),
                                    Integer.parseInt(line[0])));
                        } else if (TaskTypes.valueOf(line[1]).equals(TaskTypes.Epic)) {
                            manager.createEpic(new Epic(line[2], line[3], Integer.parseInt(line[0])));
                        } else manager.createSubTask(new Subtask(line[2], line[3], Statuses.valueOf(line[4]),
                                Integer.parseInt(line[5]), Integer.parseInt(line[0])));
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return new FileBackedTasksManager(file);
    }
}
