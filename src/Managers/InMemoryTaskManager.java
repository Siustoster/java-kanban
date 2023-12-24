package Managers;

import Tasks.*;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> taskList = new HashMap<>();
    protected Map<Integer, Epic> epicList = new HashMap<>();
    protected Map<Integer, Subtask> subTaskList = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer generatedTaskId = 1;

    private int generateId() {
        return generatedTaskId++;
    }

    @Override
    public int createTask(Task task) {
        if (task == null) {
            return 0;
        }
        if (task.getTaskId() != null) {
            return 0;
        }
        task.setTaskId(generateId());
        if (taskList.containsKey(task.getTaskId())) {
            return 0; //Будет возвращать ошибку, что задача с таким айди уже есть
        }
        taskList.put(task.getTaskId(), task);
        return task.getTaskId();
    }

    @Override
    public int createSubTask(Subtask task) {
        int epicId;
        if (task == null) {
            return 0;
        }
        if (task.getTaskId() != null) {
            return 0;
        }
        task.setTaskId(generateId());
        if (subTaskList.containsKey(task.getTaskId())) {
            return 0; //будет возвращать ошибку, что подзадача с таким айди уже есть
        }
        epicId = task.getEpicId();
        if (epicList.containsKey(epicId)) {
            subTaskList.put(task.getTaskId(), task);
            epicList.get(epicId).linkSubtask(task);
            updateEpicStatus(epicId);
        } else {
            return 0; //Будет возвращать ошибку, что нет эпика с нужным айди
        }
        return task.getTaskId();
    }

    @Override
    public int createEpic(Epic task) {
        if (task == null) {
            return 0;
        }
        if (task.getTaskId() != null) {
            return 0;
        }
        task.setTaskId(generateId());
        if (epicList.containsKey(task.getTaskId())) {
            return 0; //Будет возвращать ошибку, что эпик с таким айди уже есть
        }
        epicList.put(task.getTaskId(), task);
        return task.getTaskId();
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasksList = new ArrayList<>();
        if (!taskList.isEmpty()) {
            for (Map.Entry<Integer, Task> entry : taskList.entrySet()) {
                tasksList.add(entry.getValue());
            }
        }
        return tasksList;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> tasksList = new ArrayList<>();
        if (!epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> entry : epicList.entrySet()) {
                tasksList.add(entry.getValue());
            }
        }
        return tasksList;
    }

    @Override
    public List<Subtask> getAllSubTasks() {
        List<Subtask> tasksList = new ArrayList<>();
        if (!subTaskList.isEmpty()) {
            for (Map.Entry<Integer, Subtask> entry : subTaskList.entrySet()) {
                tasksList.add(entry.getValue());
            }
        }
        return tasksList;
    }

    @Override
    public void deleteAllTasks() {
        taskList.clear();
    }

    @Override
    public void deleteAllEpics() {
        epicList.clear();
        subTaskList.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTaskList.clear();
        if (!epicList.isEmpty()) {
            for (Map.Entry<Integer, Epic> entry : epicList.entrySet()) {
                entry.getValue().clearSubTasks();
                updateEpicStatus(entry.getValue().getTaskId());
            }
        }
    }

    @Override
    public Task getTaskById(int Id) {
        if (taskList.containsKey(Id)) {
            historyManager.add(taskList.get(Id));
            return taskList.get(Id);
        } else return null;
    }

    @Override
    public Epic getEpicById(int Id) {
        if (epicList.containsKey(Id)) {
            historyManager.add(epicList.get(Id));
            return epicList.get(Id);
        } else return null;
    }

    @Override
    public Subtask getSubTaskById(int Id) {
        if (subTaskList.containsKey(Id)) {
            historyManager.add(subTaskList.get(Id));
            return subTaskList.get(Id);
        } else return null;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null)
            return;
        if (taskList.containsKey(task.getTaskId())) {
            taskList.put(task.getTaskId(), task);
        }
    }

    @Override
    public void updateEpic(Epic task) {
        if (task == null)
            return;
        if (epicList.containsKey(task.getTaskId())) {
            epicList.put(task.getTaskId(), task);
            updateEpicStatus(task.getTaskId());
        }
    }

    @Override
    public void updateSubTask(Subtask task) {
        if (task == null)
            return;
        if (subTaskList.containsKey(task.getTaskId())) {
            subTaskList.put(task.getTaskId(), task);
            epicList.get(task.getEpicId()).linkSubtask(task);
            updateEpicStatus(task.getEpicId());
        }
    }

    @Override
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

    private void updateEpicStatus(int Id) {
        if (epicList.containsKey(Id)) {
            Epic currentEpic = epicList.get(Id);
            ArrayList<Integer> subTasksList = currentEpic.getSubTasksList();
            int newTask = 0;
            int inProgress = 0;
            int doneTask = 0;
            if (!subTaskList.isEmpty()) {
                for (Integer taskNum : subTasksList) {
                    if (subTaskList.get(taskNum).getTaskStatus().equals(Statuses.DONE))
                        doneTask++;
                    if (subTaskList.get(taskNum).getTaskStatus().equals(Statuses.NEW))
                        newTask++;
                    if (subTaskList.get(taskNum).getTaskStatus().equals(Statuses.IN_PROGRESS))
                        inProgress++;
                }
            } else {
                currentEpic.setTaskStatus(Statuses.NEW);
            }
            if (newTask > 0 && inProgress == 0 && doneTask == 0) {
                currentEpic.setTaskStatus(Statuses.NEW);
                return;
            }
            if (inProgress > 0) {
                currentEpic.setTaskStatus(Statuses.IN_PROGRESS);
                return;
            }
            if (doneTask > 0 && inProgress == 0 && newTask == 0) {
                currentEpic.setTaskStatus(Statuses.DONE);
                return;
            }
            if (doneTask > 0 && inProgress == 0 && newTask > 0) {
                currentEpic.setTaskStatus(Statuses.IN_PROGRESS);
                return;
            }
            currentEpic.setTaskStatus(Statuses.NEW);
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (taskList.containsKey(taskId)) {
            taskList.remove(taskId);
            historyManager.remove(taskId);
        } else if (epicList.containsKey(taskId)) {
            ArrayList<Integer> subsList = epicList.get(taskId).getSubTasksList();
            if (!subsList.isEmpty()) {
                for (Integer entry : subsList) {
                    subTaskList.remove(entry);
                    historyManager.remove((entry));
                }
            }
            epicList.remove(taskId);
            historyManager.remove(taskId);
        } else if (subTaskList.containsKey(taskId)) {
            int epicId = subTaskList.get(taskId).getEpicId();
            subTaskList.remove(taskId);
            historyManager.remove(taskId);
            epicList.get(epicId).removeSubTask(taskId);
            updateEpicStatus(epicId);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}

