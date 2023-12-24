package Managers;

import Tasks.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        super.createTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int createSubTask(Subtask task) {
        super.createSubTask(task);
        save();
        return task.getTaskId();
    }

    @Override
    public int createEpic(Epic task) {
        super.createEpic(task);
        save();
        return task.getTaskId();
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
}
