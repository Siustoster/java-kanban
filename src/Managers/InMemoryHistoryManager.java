package Managers;

import Tasks.*;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    public void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
            tail = newNode;
        } else
            oldTail.next = newNode;
        historyMap.put(task.getTaskId(), newNode);
    }

    List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        if (tail != null) {
            Node currentNode = tail;
            while (true) {
                tasksList.add(currentNode.data);
                if (currentNode.prev != null) {
                    currentNode = currentNode.prev;
                } else {
                    break;
                }
            }
        }
        return tasksList;
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        if (prevNode == null && nextNode == null) {
            this.head = null;
            this.tail = null;
        } else if (nextNode == null) {
            prevNode.next = null;
            this.tail = prevNode;
        } else if (prevNode == null) {
            nextNode.prev = null;
            this.head = nextNode;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    @Override
    public void add(Task task) {
        Node taskNode = historyMap.get(task.getTaskId());
        if (taskNode != null) {
            removeNode(taskNode);
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void remove(int id) {
        Node nodeToDelete = historyMap.get(id);
        if (nodeToDelete != null) {
            removeNode(nodeToDelete);
        }
        historyMap.remove(id);
    }
}
