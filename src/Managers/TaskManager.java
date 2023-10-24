package Managers;

import Tasks.*;

import java.util.*;

public interface TaskManager {

    int generateId();

    void createTask(Task task);

    void createSubTask(Subtask task);

    void createEpic(Epic task);

    HashMap<Integer, Object> getAllTasksAllTypes();

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Object getAnyTaskById(int Id);

    Task getTaskById(int Id);

    Epic getEpicById(int Id);

    Subtask getSubTaskById(int Id);

    void updateTask(Task task);

    void updateEpic(Epic task);

    void updateSubTask(Subtask task);

    ArrayList<Subtask> getAllEpicSubTasks(int epicId);

    void updateEpicStatus(int Id);

    void deleteTaskById(int taskId);

    List<Task> getHistory();

}

