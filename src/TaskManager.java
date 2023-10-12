import Tasks.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
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
        if (task == null)
            return;
        if (task.getTaskId() == 0)
            task.setTaskId(generateId());
        if (taskList.containsKey(task.getTaskId())) {
            return; //Будет возвращать ошибку, что задача с таким айди уже есть
        }
        taskList.put(task.getTaskId(), task);
    }

    public void createSubTask(Subtask task) {
        int epicId = 0;
        if (task == null)
            return;
        if (task.getTaskId() == 0)
            task.setTaskId(generateId());
        if (subTaskList.containsKey(task.getTaskId())) {
            return; //будет возвращать ошибку, что подзадача с таким айди уже есть
        }
        epicId = task.getEpicId();
        if (epicList.containsKey(epicId)) {
            subTaskList.put(task.getTaskId(), task);
            epicList.get(epicId).linkSubtask(task);
            correctEpicStatus(epicId);
        } else {
            return; //Будет возвращать ошибку, что нет эпика с нужным айди
        }
    }

    public void createEpic(Epic task) {
        if (task == null)
            return;
        if (task.getTaskId() == 0)
            task.setTaskId(generateId());
        if (epicList.containsKey(task.getTaskId())) {
            return; //Будет возвращать ошибку, что эпик с таким айди уже есть
        }
        epicList.put(task.getTaskId(), task);
    }

    public HashMap<Integer, Object> getAllTasksAllTypes() {
        HashMap<Integer, Object> allTasksList = new HashMap<>();
        if (!taskList.isEmpty()) {
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                allTasksList.put(entry.getKey(), entry.getValue());
            }
        }
        if (!epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> entry : epicList.entrySet()) {
                allTasksList.put(entry.getKey(), entry.getValue());
            }
        }
        if (!subTaskList.isEmpty()) {
            for (Map.Entry<Integer, Subtask> entry : subTaskList.entrySet()) {
                allTasksList.put(entry.getKey(), entry.getValue());
            }
        }
        return allTasksList;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return taskList;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epicList;
    }

    public HashMap<Integer, Subtask> getAllSubTasks() {
        return subTaskList;
    }

    public void deleteAllTasks() {
        taskList.clear();
    }

    public void deleteAllEpics() {
        epicList.clear();
        subTaskList.clear();
    }

    public void deleteAllSubTasks() {
        subTaskList.clear();
        if (!epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> entry : epicList.entrySet()) {
                entry.getValue().clearSubTasks();
                correctEpicStatus(entry.getValue().getTaskId());
            }
        }
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
        if (task == null)
            return;
        if (task.getTaskId() == 0)
            return; //Таск необходимо сначала создать - разделяем методы создания и обновления
        if (taskList.containsKey(task.getTaskId())) {
            taskList.put(task.getTaskId(), task);
        }
    }

    public void updateEpic(Epic task) {
        if (task == null)
            return;
        if (task.getTaskId() == 0)
            return;
        if (epicList.containsKey(task.getTaskId())) {
            epicList.put(task.getTaskId(), task);
            correctEpicStatus(task.getTaskId());
        }
    }

    public void updateSubTask(Subtask task) {
        if (task == null)
            return;
        if (task.getTaskId() == 0)
            return;
        if (subTaskList.containsKey(task.getTaskId())) {
            subTaskList.put(task.getTaskId(), task);
            epicList.get(task.getEpicId()).linkSubtask(task);
            correctEpicStatus(task.getEpicId());
        }
    }

    public ArrayList<Subtask> getAllEpicSubTasks(int epicId) {
        ArrayList<Subtask> listOfSubtasks = new ArrayList<>();
        if (epicId > 0) {
            if (!subTaskList.isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry : subTaskList.entrySet()) {
                    if (entry.getValue().getEpicId() == epicId)
                        listOfSubtasks.add(entry.getValue());
                }
            }
        }
        return listOfSubtasks;
    }

    private void correctEpicStatus(int Id) {
        if (epicList.containsKey(Id)) {
            Epic currentEpic = epicList.get(Id);
            HashMap<Integer, Subtask> subTasksList = currentEpic.getSubTasksList();
            int newTask = 0;
            int inProgress = 0;
            int doneTask = 0;
            if (!subTasksList.isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry : subTasksList.entrySet()) {
                    if (entry.getValue().getTaskStatus().equals("DONE"))
                        doneTask++;
                    if (entry.getValue().getTaskStatus().equals("NEW"))
                        newTask++;
                    if (entry.getValue().getTaskStatus().equals("IN_PROGRESS"))
                        inProgress++;
                }
            } else {
                currentEpic.setTaskStatus("NEW");
            }
            if (newTask > 0 && inProgress == 0 && doneTask == 0)
                currentEpic.setTaskStatus("NEW");
            if (inProgress > 0)
                currentEpic.setTaskStatus("IN_PROGRESS");
            if (doneTask > 0 && inProgress == 0 && newTask == 0)
                currentEpic.setTaskStatus("DONE");
        }
    }

    public void deleteTaskById(int taskId) {
        if (taskList.containsKey(taskId)) {
            taskList.remove(taskId);
        } else if (epicList.containsKey(taskId)) {
            HashMap<Integer, Subtask> subsList = epicList.get(taskId).getSubTasksList();
            if (!subsList.isEmpty()) {
                for (Map.Entry<Integer, Subtask> entry : subsList.entrySet()) {
                    subTaskList.remove(entry.getKey());
                }
            }
            epicList.remove(taskId);
        } else if (subTaskList.containsKey(taskId)) {
            int epicId = subTaskList.get(taskId).getEpicId();
            subTaskList.remove(taskId);
            epicList.get(epicId).removeSubTask(taskId);
            correctEpicStatus(epicId);
        }
    }

}

