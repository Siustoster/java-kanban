package Managers;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    Path filePath;

    public FileBackedTasksManager(Path filePath) {
        this.filePath = filePath;

    }

    private void save() {

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
