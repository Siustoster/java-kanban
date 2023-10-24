package Managers;

import Tasks.*;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewingHistoryList = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (viewingHistoryList.size() < 10) {
            viewingHistoryList.add(task);
        } else {
            viewingHistoryList.remove(0);
            viewingHistoryList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return viewingHistoryList;
    }
}
