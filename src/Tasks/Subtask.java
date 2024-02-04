package Tasks;

import java.time.LocalDateTime;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
public class Subtask extends Task {
    private int epicId;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return getTaskId() + "," + TaskTypes.Subtask + "," + getTaskName() + "," + getTaskDescription() + ","
                + getTaskStatus() + "," + epicId + ","
                + (startTime != null ? startTime.format(formatter) : "не установлена")
                // + "," + (getEndTime() != null ? getEndTime().format(formatter) : "не установлена")
                + "," + (duration != 0 ? duration : "не установлена");
    }

    public Subtask(String taskName, String taskDescription, Statuses taskStatus, int epicId) {
        super(taskName, taskDescription, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, Statuses taskStatus, int epicId, int taskId) {
        super(taskName, taskDescription, taskStatus);
        this.epicId = epicId;
        this.setTaskId(taskId);
    }

    public Subtask(String taskName, String taskDescription, Statuses taskStatus, int epicId, LocalDateTime startTime, int duration) {
        super(taskName, taskDescription, taskStatus, startTime, duration);
        this.epicId = epicId;
    }
    public Subtask(String taskName, String taskDescription,  int epicId, LocalDateTime startTime, int duration) {
        super(taskName, taskDescription, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, Statuses taskStatus, int epicId, int taskId
            , LocalDateTime startTime, int duration) {
        super(taskName, taskDescription, taskStatus, taskId, startTime, duration);
        this.epicId = epicId;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskTypes getType() {
        return TaskTypes.Subtask;
    }
}
