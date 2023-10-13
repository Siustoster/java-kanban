package Tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTasksList = new ArrayList<>();

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

    public ArrayList<Integer> getSubTasksList() {
        return subTasksList;
    }

    public void linkSubtask(Subtask task) {
        if (task == null)
            return;
        if (task.getEpicId() == this.getTaskId())
            if (!subTasksList.contains(task.getTaskId())) {
                subTasksList.add(task.getTaskId());
            }

    }

    public void clearSubTasks() {
        subTasksList.clear();
    }

    public void removeSubTask(Integer subTaskId) {
        int IndexToDelete = -1;
        if (!(subTasksList.isEmpty())) {
            for (Integer taskNum:subTasksList) {
                if (taskNum.equals(subTaskId)) {
                    IndexToDelete = subTasksList.indexOf(taskNum);
                }
            }
        }
        if(IndexToDelete != -1) {
            subTasksList.remove(IndexToDelete);
        }
    }
}
