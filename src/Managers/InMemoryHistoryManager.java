package Managers;

import Tasks.*;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node<Task>> historyMap = new HashMap<>();

    class Node<E> {
        public E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node<Task> head;
    private Node<Task> tail;

    public void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
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
            Node<Task> currentNode = tail;
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

    private void removeNode(Node<Task> node) {
        Node<Task> prevNode;
        Node<Task> nextNode;
        if (node != null) {
            if (node.next != null) {
                if (node.prev != null) {
                    prevNode = node.prev;
                    nextNode = node.next;
                    prevNode.next = nextNode;
                    nextNode.prev = prevNode;
                } else {
                    node.next.prev = null;
                    head = node.next;
                }
            } else {
                node.prev.next = null;
                tail = node.prev;
            }
        }
    }

    @Override
    public void add(Task task) {
        Node<Task> taskNode = historyMap.get(task.getTaskId());
        if (taskNode!=null) {
            removeNode(taskNode);
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void remove(int id) {
        Node<Task> nodeToDelete = historyMap.get(id);
        if (nodeToDelete!=null) {
            removeNode(nodeToDelete);
        }
        historyMap.remove(id);
    }
}
