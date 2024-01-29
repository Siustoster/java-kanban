package Managers;

import Tasks.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.*;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> taskList = new HashMap<>();
    protected Map<Integer, Epic> epicList = new HashMap<>();
    protected Map<Integer, Subtask> subTaskList = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer generatedTaskId = 1;
    protected final Set<Task> priorityTasksByStartTime = new TreeSet<>(comparing(Task::getStartTime,
            nullsLast(naturalOrder())).thenComparing(Task::getTaskId));
    private int generateId() {
        return generatedTaskId++;
    }

    @Override
    public int createTask(Task task) {
        Optional<Task> crossTask = getCrossWithTask(task);
        if (crossTask.isPresent()) {
            // Пока всё будет падать а-ля - Всё пропало!
            throw new TimeCrossException("Задача пересекается во времени с задачей номер " + crossTask.get().getTaskId());
        }
        if (task.getTaskId() == null) {
            task.setTaskId(generateId());
        }

        while (epicList.containsKey(task.getTaskId()) || subTaskList.containsKey(task.getTaskId()) ||
                taskList.containsKey(task.getTaskId())) {
            task.setTaskId(generateId());
        }
        taskList.put(task.getTaskId(), task);
        priorityTasksByStartTime.add(task);
        return task.getTaskId();
    }

    @Override
    public int createSubTask(Subtask task) {
        int epicId;
        Optional<Task> crossTask = getCrossWithTask(task);
        if (crossTask.isPresent()) {
            throw new TimeCrossException("Задача пересекается во времени с задачей номер " + crossTask.get().getTaskId());
        }

        if (!epicList.containsKey(task.getEpicId())) {
            throw new InvalidEpicTaskIdException("Указанный id эпика отсутствует");
        }

        if (task.getTaskId() == null) {
            task.setTaskId(generateId());
        }

        while (epicList.containsKey(task.getTaskId()) || subTaskList.containsKey(task.getTaskId()) ||
                taskList.containsKey(task.getTaskId())) {
            task.setTaskId(generateId());
        }
        epicId = task.getEpicId();

            subTaskList.put(task.getTaskId(), task);
            epicList.get(epicId).linkSubtask(task);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
            priorityTasksByStartTime.add(task);

        return task.getTaskId();
    }

    @Override
    public int createEpic(Epic task) {
        if (task == null) {
            return 0;
        }
        if (task.getTaskId() == null) {
            task.setTaskId(generateId());
        }


        while (epicList.containsKey(task.getTaskId()) || subTaskList.containsKey(task.getTaskId()) ||
                taskList.containsKey(task.getTaskId())) {
            task.setTaskId(generateId());
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

        for(Integer key : taskList.keySet()) {
            historyManager.remove(key);
            priorityTasksByStartTime.remove(taskList.get(key));
        }

        taskList.clear();
    }

    @Override
    public void deleteAllEpics() {
        for(Integer key : epicList.keySet()) {
            historyManager.remove(key);

            List<Integer> subTaskIds = epicList.get(key).getSubTasksList();

            for (Integer id : subTaskIds) {
                historyManager.remove(id);
                priorityTasksByStartTime.remove(subTaskList.get(id));
            }
        }

        subTaskList.clear();
        epicList.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        if (subTaskList.size() > 0) {
            List<Integer> keys = new ArrayList<>(subTaskList.keySet());
            for (int key : keys) {
                deleteTaskById(key);
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
            priorityTasksByStartTime.remove(taskList.get(task.getTaskId()));

            Optional<Task> crossTask = getCrossWithTask(task);

            if (crossTask.isPresent()) {
                throw new TimeCrossException("Задача пересекается во времени с задачей номер " + crossTask.get().getTaskId());
            }
            taskList.put(task.getTaskId(), task);
            priorityTasksByStartTime.add(task);
        }
    }

    @Override
    public void updateEpic(Epic task) {
        if (task == null)
            return;
        if (epicList.containsKey(task.getTaskId())) {
            epicList.put(task.getTaskId(), task);
            updateEpicStatus(task.getTaskId());
            updateEpicTime(task.getTaskId());
        }
    }

    @Override
    public void updateSubTask(Subtask task) {
        if (task == null)
            return;

        if (subTaskList.containsKey(task.getTaskId())) {
            priorityTasksByStartTime.remove(subTaskList.get(task.getTaskId()));
            Optional<Task> crossTask = getCrossWithTask(task);

            if (crossTask.isPresent()) {
                throw new TimeCrossException("Задача пересекается во времени с задачей номер " + crossTask.get().getTaskId());
            }
            subTaskList.put(task.getTaskId(), task);
            epicList.get(task.getEpicId()).linkSubtask(task);
            updateEpicStatus(task.getEpicId());
            updateEpicTime(task.getEpicId());
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
    private void updateEpicTime(int epicTaskId) {
        Epic epicTask = epicList.get(epicTaskId);
        ArrayList<Integer> subTaskIds = epicTask.getSubTasksList();

        if (subTaskIds.size() != 0) {
            List<Subtask> epicSubTasks = new ArrayList<>();
            int duration = 0;

            for (Integer id : subTaskIds) {
                epicSubTasks.add(subTaskList.get(id));
            }

            LinkedList<Subtask> subTasksFilteredSorted = epicSubTasks.stream()
                    .filter(st -> st.getStartTime() != null)
                    .sorted(comparing(Subtask::getStartTime))
                    .collect(Collectors.toCollection(LinkedList::new));

            if (subTasksFilteredSorted.size() > 0) {
                for (Subtask st : subTasksFilteredSorted) {
                    duration += st.getDuration();
                }

                epicTask.setStartTime(subTasksFilteredSorted.getFirst().getStartTime());
                epicTask.setEndTime(subTasksFilteredSorted.getLast().getEndTime());
                epicTask.setDuration(duration);
            }
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (taskList.containsKey(taskId)) {
            priorityTasksByStartTime.remove(taskList.get(taskId));
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
            priorityTasksByStartTime.remove(subTaskList.get(taskId));
            subTaskList.remove(taskId);
            historyManager.remove(taskId);
            epicList.get(epicId).removeSubTask(taskId);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);

        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        if (priorityTasksByStartTime.isEmpty()) {
            return new ArrayList<Task>();
        }

        return List.copyOf(priorityTasksByStartTime);
    }

    protected Optional<Task> getCrossWithTask(Task task) {
        if (task.getStartTime() == null) {
            return Optional.empty();
        }
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        for (Task t : priorityTasksByStartTime) {
            if (t.getStartTime() == null) {
                continue;
            }
            LocalDateTime checkStartTime = t.getStartTime();
            LocalDateTime checkEndTime = t.getEndTime();
            if (!(endTime.isBefore(checkStartTime) || startTime.isAfter(checkEndTime))) {
                return Optional.of(t);
            }
        }

        return Optional.empty();
    }
}

