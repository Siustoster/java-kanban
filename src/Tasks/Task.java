package Tasks;

import java.util.Objects;

public class Task {
    private String taskName;
    private String taskDescription;
    private Integer taskId;
    private Statuses taskStatus;

    public Task(String taskName, String taskDescription, Statuses taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public Task(String taskName, String taskDescription, Statuses taskStatus, int taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskId = taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(taskName, task.taskName)
                && Objects.equals(taskDescription, task.taskDescription) && Objects.equals(taskStatus, task.taskStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, taskId, taskStatus);
    }

    @Override
    public String toString() {
        return getTaskId() + "," + TaskTypes.Task + "," + getTaskName() + "," + getTaskDescription() + "," + getTaskStatus();
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public Statuses getTaskStatus() {
        return taskStatus;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTaskStatus(Statuses taskStatus) {
        this.taskStatus = taskStatus;
    }
}
