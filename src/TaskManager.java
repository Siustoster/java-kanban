import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> taskList;
    HashMap<Integer, Epic> epicList;
    HashMap<Integer, Subtask> subTaskList;

    Integer generatedTaskId = 0;
}
