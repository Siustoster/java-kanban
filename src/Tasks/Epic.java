package Tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
public class Epic extends Task {

    private ArrayList<Integer> subTasksList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, Statuses.NEW);
    }

    public Epic(String taskName, String taskDescription, int taskId) {
        super(taskName, taskDescription, Statuses.NEW);
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
        return getTaskId() + "," + TaskTypes.Epic + "," + getTaskName() + "," + getTaskDescription() + "," + getTaskStatus();

    }
    public String toString2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return getTaskId() + "," + TaskTypes.Epic + "," + getTaskName() + "," + getTaskDescription() + ","
                + getTaskStatus() + "," +  (startTime != null ? startTime.format(formatter) : null)  ;

    }
    @Override
    public TaskTypes getType() {
        return TaskTypes.Epic;
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
            for (Integer taskNum : subTasksList) {
                if (taskNum.equals(subTaskId)) {
                    IndexToDelete = subTasksList.indexOf(taskNum);
                }
            }
        }
        if (IndexToDelete != -1) {
            subTasksList.remove(IndexToDelete);
        }
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
