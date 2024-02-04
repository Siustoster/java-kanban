package Managers;

import Managers.Server.KVTaskClient;
import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager implements  TaskManager{
    private final String key = "TaskManager_01";
    private final KVTaskClient kvTaskClient;
    public final String FIRST_STRING = "id,type,name,status,description,epic,startTime,endTime,duration";

    public HttpTaskManager(String serverUrl) {
        kvTaskClient = new KVTaskClient(serverUrl);
    }

    protected void loadFromServer() {
        String context = kvTaskClient.load(key);

        if (context.length() > 0) {
            String[] fileParts = context.split("\r?\n\r?\n");
            String partOfTask = fileParts[0];
            String partOfHistory = fileParts.length > 1 ? fileParts[1] : "";

            String[] taskData = partOfTask.split("\r?\n");

            if (taskData.length > 1) {
                for (String str : taskData) {
                    if (str.equals("id,type,name,status,description,epic,startTime,endTime,duration")) {
                        continue;
                    }

                   fromString(str);
                }
            }

            if (partOfHistory.length() > 0) {
                historyFromString(partOfHistory);
            }
        }
    }

    @Override
    protected void save() {
        StringBuilder context = new StringBuilder();
        context.append(FIRST_STRING);
        context.append("\r\n");

        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getAllTasks());
        allTasks.addAll(getAllEpics());
        allTasks.addAll(getAllSubTasks());

        for (Task item : allTasks) {
            context.append(toString(item));
            context.append("\r\n");
        }
        context.append("\r\n");
        context.append(historyToString(historyManager));

        kvTaskClient.put(key, context.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof HttpTaskManager)) {
            return false;
        }

        return super.equals(obj);
    }
}
