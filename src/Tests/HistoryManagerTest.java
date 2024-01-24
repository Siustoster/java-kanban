package Tests;

import Managers.HistoryManager;
import Managers.Managers;
import Tasks.Epic;
import Tasks.Statuses;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void createHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Epic epic = new Epic("Эпик", "Эпик", 1);
        Task task = new Task("Таск", "Таск", Statuses.IN_PROGRESS, 2);
        Subtask subtask = new Subtask("Саб", "Саб", Statuses.IN_PROGRESS, 1, 3);
        historyManager.add(epic);
        historyManager.add(task);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(subtask, task, epic), history);
    }

    @Test
    void addNull() {
        //historyManager.add(null);
        assertThrows(NullPointerException.class, () -> historyManager.add(null));
    }

    @Test
    void addRepeat() {
        Epic epic = new Epic("Эпик", "эпик", 1);
        Task task = new Task("Таск", "Таск", Statuses.NEW, 2);
        Subtask sub = new Subtask("Саб", "Саб", Statuses.NEW, 1, 3);
        historyManager.add(epic);
        historyManager.add(epic);
        historyManager.add(sub);
        historyManager.add(sub);
        historyManager.add(task);

        assertEquals(List.of(task, sub, epic), historyManager.getHistory());

    }

    @Test
    void addRepeatableTaskToEnd() {
        Task task = new Task("Таск", "Таск", Statuses.NEW, 1);
        Epic epic = new Epic("Эпик", "Эпик", 2);
        Subtask sub = new Subtask("Саб", "Саб", Statuses.NEW, 2, 3);

        historyManager.add(task); // первый в списке просмотренных задач
        historyManager.add(epic); // второй в списке просмотренных задач
        historyManager.add(sub); // третий в списке просмотренных задач
        historyManager.add(task); // task удален как дубли и помещен в конец списка просмотренных задач

        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task, sub, epic), history);
    }

    @Test
    void getHistory() {
        Task task = new Task("name", "description", Statuses.NEW, 1);
        Epic epic = new Epic("epic", "description", 2);
        Subtask sub = new Subtask("sub", "description", Statuses.NEW,2,3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(sub);

        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(sub, epic, task), history);
    }
    @Test
    void getHistoryIsZeroWhenEmpty() {
        List<Task> history = historyManager.getHistory();
        assertEquals(0,history.size());
    }

    @Test
    void removeInvalid() {
        Task task = new Task("name", "description", Statuses.NEW, 1);
        Epic epic = new Epic("epic", "description", 2);
        Subtask sub = new Subtask("sub", "description", Statuses.NEW,2,3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(sub);
        historyManager.remove(-5);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(sub, epic, task), history);

    }
    @Test
    void removeFirst() {
        Task task = new Task("name", "description", Statuses.NEW, 1);
        Epic epic = new Epic("epic", "description", 2);
        Subtask sub = new Subtask("sub", "description", Statuses.NEW,2,3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(sub);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(sub, epic), history);

    }

    @Test
    void removeMiddle() {
        Task task = new Task("name", "description", Statuses.NEW, 1);
        Epic epic = new Epic("epic", "description", 2);
        Subtask sub = new Subtask("sub", "description", Statuses.NEW,2,3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(sub);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(sub, task), history);

    }
    @Test
    void removeLast() {
        Task task = new Task("name", "description", Statuses.NEW, 1);
        Epic epic = new Epic("epic", "description", 2);
        Subtask sub = new Subtask("sub", "description", Statuses.NEW,2,3);

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(sub);
        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(epic, task), history);

    }


}