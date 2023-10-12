package Tasks;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer,Subtask> subTasksList = new HashMap<>();

    public Epic(String taskName, String taskDescription, String taskStatus){
        super(taskName,taskDescription,taskStatus);

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
}
