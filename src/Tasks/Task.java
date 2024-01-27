package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String taskName;
    private String taskDescription;
    private Integer taskId;
    private Statuses taskStatus;
    protected LocalDateTime startTime;
    protected int duration;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String taskName, String taskDescription, Statuses taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public Task(String taskName, String taskDescription, Statuses taskStatus, LocalDateTime startTime, int duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName, String taskDescription, Statuses taskStatus, int taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskId = taskId;
    }

    public Task(String taskName, String taskDescription, Statuses taskStatus, int taskId, LocalDateTime startTime, int duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskId = taskId;
        this.startTime = startTime;
        this.duration = duration;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(taskName, task.taskName)
                && Objects.equals(taskDescription, task.taskDescription) && Objects.equals(taskStatus, task.taskStatus)
                && Objects.equals(startTime, task.startTime) && duration == task.duration;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, taskId, taskStatus, startTime, duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {

        return getTaskId() + "," + TaskTypes.Task + "," + getTaskName() + "," + getTaskDescription() + ","
                + getTaskStatus() + "," + (startTime != null ? startTime.format(formatter) : "не установлена")
                // + "," + (getEndTime() != null ? getEndTime().format(formatter) : "не установлена")
                + "," + (duration != 0 ? duration  : "не установлена");

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

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(Duration.ofMinutes(duration));
    }
}
