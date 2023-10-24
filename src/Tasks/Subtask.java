package Tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    @Override
    public String toString() {
        return "Subtask{" +
                "taskName='" + getTaskName() + '\'' +
                ", taskDescription='" + getTaskDescription() + '\'' +
                ", taskId=" + getTaskId() +
                ", taskStatus='" + getTaskStatus() + '\'' +
                "epicId=" + epicId +
                '}';
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

}
