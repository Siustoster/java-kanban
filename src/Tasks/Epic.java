package Tasks;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    private HashMap<Integer, Subtask> subTasksList = new HashMap<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, "NEW");
    }

    public Epic(String taskName, String taskDescription, int taskId) {
        super(taskName, taskDescription, "NEW");
        this.setTaskId(taskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasksList, epic.subTasksList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subTasksList);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + getTaskName() + '\'' +
                ", taskDescription='" + getTaskDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", taskStatus='" + getTaskStatus() + '\'' +
                "subTasksList=" + subTasksList +
                '}';
    }

    public HashMap<Integer, Subtask> getSubTasksList() {
        return subTasksList;
    }

    public void linkSubtask(Subtask task) {
        if (task == null)
            return;
        if (task.getEpicId() == this.getTaskId())
            subTasksList.put(task.getTaskId(), task);
    }

    public void clearSubTasks() {
        subTasksList.clear();
    }

    public void removeSubTask(int subTaskId) {
        if (subTasksList.containsKey(subTaskId)) {
            subTasksList.remove(subTaskId);
        }
    }
}
