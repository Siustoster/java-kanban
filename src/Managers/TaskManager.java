package Managers;

import Tasks.*;

import java.util.*;

public interface TaskManager {

    int createTask(Task task);

    int createSubTask(Subtask task);

    int createEpic(Epic task);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Task getTaskById(int Id);

    Epic getEpicById(int Id);

    Subtask getSubTaskById(int Id);

    void updateTask(Task task);

    void updateEpic(Epic task);

    void updateSubTask(Subtask task);

    ArrayList<Subtask> getAllEpicSubTasks(int epicId);

    void deleteTaskById(int taskId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}

