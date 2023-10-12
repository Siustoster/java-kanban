import Tasks.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TaskManager {
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();
    private HashMap<Integer, Subtask> subTaskList = new HashMap<>();

    private Integer generatedTaskId = 0;

    private int generateId() {
        generatedTaskId++;
        return generatedTaskId;
    }

    public void createTask(Task task) {
        if (task ==null)
            return;
        if (task.getTaskId()==0)
            task.setTaskId(generateId());
        if(taskList.containsKey(task.getTaskId())) {
            return; //Будет возвращать ошибку, что задача с таким айди уже есть
        }
        taskList.put(task.getTaskId(),task);
    }

    public void createSubTask(Subtask task) {
        if (task ==null)
            return;
        if (task.getTaskId()==0)
            task.setTaskId(generateId());
        if(subTaskList.containsKey(task.getTaskId())) {
            return; //будет возвращать ошибку, что подзадача с таким айди уже есть
        }
        if(epicList.containsKey(task.getEpicId())){
            subTaskList.put(task.getTaskId(),task);
        } else {
            return; //Будет возвращать ошибку, что нет эпика с нужным айди
        }
    }

    public void createEpic(Epic task) {
        if (task ==null)
            return;
        if (task.getTaskId()==0)
            task.setTaskId(generateId());
        if(epicList.containsKey(task.getTaskId())) {
            return; //Будет возвращать ошибку, что эпик с таким айди уже есть
        }
        epicList.put(task.getTaskId(),task);
    }

    public HashMap<Integer, Object>  getAllTasks() {
        HashMap<Integer, Object> allTasksList = new HashMap<>();
        if(!taskList.isEmpty()) {
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                allTasksList.put(entry.getKey(),entry.getValue());
            }
        }
        if(!epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> entry : epicList.entrySet()) {
                allTasksList.put(entry.getKey(),entry.getValue());
            }
        }
        if(!subTaskList.isEmpty()) {
            for (Map.Entry<Integer, Subtask> entry : subTaskList.entrySet()) {
                allTasksList.put(entry.getKey(),entry.getValue());
            }
        }
        return allTasksList;
    }

    public void deleteAllTasks() {
        subTaskList.clear();
        epicList.clear();
        taskList.clear();
    }

    public Object getTaskById(int Id) {
        Object object = new Object();
        if (taskList.containsKey(Id)) {
            return taskList.get(Id);
        } else if (subTaskList.containsKey(Id)) {
            return subTaskList.get(Id);
        } else if (epicList.containsKey(Id)) {
            return epicList.get(Id);
        } else return object;
    }

    public void updateTask(Task task) {
        if (task ==null)
            return;
        if (task.getTaskId()==0)
            return; //Таск необходимо сначала создать - разделяем методы создания и обновления
        if(taskList.containsKey(task.getTaskId())) {
            taskList.put(task.getTaskId(),task);
        }
    }

    public void updateEpic(Epic task) {
        if (task ==null)
            return;
        if (task.getTaskId()==0)
            return;
        if(epicList.containsKey(task.getTaskId())) {
            epicList.put(task.getTaskId(),task);
        }
    }

    public void updateSubTask(Subtask task) {
        if (task ==null)
            return;
        if (task.getTaskId()==0)
            return;
        if(subTaskList.containsKey(task.getTaskId())) {
            subTaskList.put(task.getTaskId(),task);
        }
    }

    public void getAllSubTasks() {

    }
}
