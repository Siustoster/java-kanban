import Tasks.*;

import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> taskList;
    HashMap<Integer, Epic> epicList;
    HashMap<Integer, Subtask> subTaskList;

    Integer generatedTaskId = 0;

    private int generateId() {
        generatedTaskId++;
        return generatedTaskId;
    }

    public void createTask() {
    }

    public void createSubTask() {

    }

    public void createEpic() {

    }

    public void getAllTasks() {

    }
    public void deleteAllTasks() {

    }

    public void getTaskById() {

    }

    public void updateTask() {

    }

    public void updateEpic() {

    }

    public void updateSubTask() {

    }

    public void getAllSubTasks() {

    }
}
