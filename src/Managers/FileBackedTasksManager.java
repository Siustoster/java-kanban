package Managers;

import Exceptions.ManagerSaveException;
import Tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    public final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    protected FileBackedTasksManager() {
        file = new File("default");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof FileBackedTasksManager)) {
            return false;
        }

        return super.equals(obj);
    }

    public String toString(final Task task) {
        if (task == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        String taskType = task.getType().name();
        String epicId = TaskTypes.Subtask.equals(task.getType()) ? String.valueOf(((Subtask) task).getEpicId()) : "";

        sb.append(task.getTaskId()).append(",");
        sb.append(taskType).append(",");
        sb.append(task.getTaskName()).append(",");
        sb.append(task.getTaskStatus()).append(",");
        sb.append(task.getTaskDescription()).append(",");
        sb.append(epicId).append(",");
        sb.append(task.getStartTime() != null ? task.getStartTime().format(formatter) : "").append(",");
        sb.append(task.getEndTime() != null ? task.getEndTime().format(formatter) : "").append(",");
        sb.append(task.getDuration() != 0 ? task.getDuration() : "");

        return sb.toString();
    }
    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            writer.write("id,type,name,description,status,epic,startTime,duration");
            writer.newLine();

            for (Task task : taskList.values()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic task : epicList.values()) {
                writer.write(task.toString2());
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
    public int deleteTaskById(int taskId) {
        int result = super.deleteTaskById(taskId);
        save();
        return result;
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
                    if (!lines[i].contains("id,type,name,description,status,epic,startTime,duration")) {
                        String[] line = lines[i].split(",");
                        if (TaskTypes.valueOf(line[1]).equals(TaskTypes.Task)) {
                            manager.createTask(CsvUtils.fromString(lines[i]));
                            if (i == (lines.length) - 1)
                                return manager;
                        } else if (TaskTypes.valueOf(line[1]).equals(TaskTypes.Epic)) {
                            manager.createEpic((Epic) CsvUtils.fromString(lines[i]));
                            if (i == (lines.length) - 1)
                                return manager;
                        } else {
                            manager.createSubTask((Subtask) CsvUtils.fromString(lines[i]));
                            if (i == (lines.length) - 1)
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

    public void historyFromString(String value) {
        if (!(value == null || value.length() == 0)) {
            List<Integer> idList = new ArrayList<>();

            for (String s : value.split(",")) {
                idList.add(Integer.parseInt(s));
            }

            for (Integer id : idList) {
                if (taskList.containsKey(id)) {
                    super.getTaskById(id);
                } else if (epicList.containsKey(id)) {
                    super.getEpicById(id);
                } else if (subTaskList.containsKey(id)) {
                    super.getSubTaskById(id);
                }
            }
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> tasksInHistory = manager.getHistory();
        List<String> ids = new ArrayList<>();

        if (tasksInHistory == null || tasksInHistory.size() == 0) {
            return "";
        }

        for (Task task : tasksInHistory) {
            ids.add(String.valueOf(task.getTaskId()));
        }

        return String.join(",", ids);
    }

    public void fromString(String value) {
        if (value == null || value.length() == 0) {
            return;
        }

        String[] items = value.split(",", 9);

        if (items.length < 9) {
            return;
        }

        int id = Integer.parseInt(items[0]);
        TaskTypes type = TaskTypes.valueOf(items[1]);
        String name = items[2];
        String status = items[3];
        String description = items[4];
        String epicId = items[5];
        LocalDateTime startTime = (!"".equals(items[6]) ? LocalDateTime.parse(items[6], formatter) : null);
        String duration = items[8];

        switch (type) {
            case Task:
                Task task;
                if (startTime != null) {
                    task = new Task(name, description, Statuses.valueOf(status), id, startTime,
                            Integer.parseInt(duration));
                } else {
                    task = new Task(id, Statuses.valueOf(status), name, description);
                }
                taskList.put(id, task);
                priorityTasksByStartTime.add(task);
                break;
            case Epic:
                epicList.put(id, new Epic(name, description, id));
                break;
            case Subtask:
                Subtask subTask;
                if (startTime != null) {
                    subTask = new Subtask(name, description, Statuses.valueOf(status), id,
                            Integer.parseInt(epicId), startTime, Integer.parseInt(duration));
                } else {
                    subTask = new Subtask(name, description, Statuses.valueOf(status), id,
                            Integer.parseInt(epicId));
                }
                subTaskList.put(id, subTask);
                priorityTasksByStartTime.add(subTask);
                super.addSubTackToEpic(id, Integer.parseInt(items[5]));
        }

        if (id >= generatedTaskId) {
            generatedTaskId = id + 1;
        }
    }

}
