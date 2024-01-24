package Managers;

import Tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBackedTasksManager manager = (FileBackedTasksManager) o;
        return taskList.equals(manager.taskList) && subTaskList.equals(manager.subTaskList)
                && epicList.equals(manager.epicList);
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("id,type,name,description,status,epic");
            writer.newLine();

            for (Task task : taskList.values()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic task : epicList.values()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Subtask task : subTaskList.values()) {
                writer.write(task.toString());
                writer.newLine();
            }
            writer.newLine();
            writer.write(CsvUtils.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении данных в файл");
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int createTask(Task task) {
        int createdTaskId = super.createTask(task);
        save();
        return createdTaskId;
    }

    @Override
    public int createSubTask(Subtask task) {
        int createdTaskId = super.createSubTask(task);
        save();
        return createdTaskId;
    }

    @Override
    public int createEpic(Epic task) {
        int createdTaskId = super.createEpic(task);
        save();
        return createdTaskId;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic task) {
        super.updateEpic(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask task) {
        super.updateSubTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public Task getTaskById(int Id) {
        Task taskToReturn = super.getTaskById(Id);
        save();
        return taskToReturn;
    }

    @Override
    public Epic getEpicById(int Id) {
        Epic taskToReturn = super.getEpicById(Id);
        save();
        return taskToReturn;
    }

    @Override
    public Subtask getSubTaskById(int Id) {
        Subtask taskToReturn = super.getSubTaskById(Id);
        save();
        return taskToReturn;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        try {
            FileBackedTasksManager manager = new FileBackedTasksManager(file);

            String fileString = Files.readString(Path.of(file.getPath()));
            fileString = fileString.replaceAll("\\r", "");
            String[] lines = fileString.split("\\n");

            if (lines[0].isBlank()) {
                System.out.println("Ошибка загрузки данных из файла - файл пустой");
                return manager;
            }

            for (int i = 0; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    List<Integer> historyIds = CsvUtils.historyFromString(lines[i + 1]);
                    for (int id : historyIds) {
                        if (manager.taskList.containsKey(id)) {
                            // manager.historyManager.add(manager.taskList.get(id));
                            manager.getTaskById(id);
                        } else if (manager.epicList.containsKey(id)) {
                            //manager.historyManager.add(manager.epicList.get(id));
                            manager.getEpicById(id);
                        } else {
                            // manager.historyManager.add(manager.subTaskList.get(id));
                            manager.getSubTaskById(id);
                        }
                    }
                    return manager;
                } else {
                    if (!lines[i].contains("id,type,name,description,status,epic")) {
                        String[] line = lines[i].split(",");
                        if (TaskTypes.valueOf(line[1]).equals(TaskTypes.Task)) {
                            manager.createTask(CsvUtils.fromString(lines[i]));
                            if(i==(lines.length)-1)
                                return manager;
                        } else if (TaskTypes.valueOf(line[1]).equals(TaskTypes.Epic)) {
                            manager.createEpic((Epic) CsvUtils.fromString(lines[i]));
                            if(i==(lines.length)-1)
                                return manager;
                        } else {
                            manager.createSubTask((Subtask) CsvUtils.fromString(lines[i]));
                            if(i==(lines.length)-1)
                                return manager;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return new FileBackedTasksManager(file);
    }
}
