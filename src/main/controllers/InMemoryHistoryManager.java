package controllers;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeHistoryMap = new HashMap<>();

    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
            newNode.setPrev(tail);
        }
        tail = newNode;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentTask = head;
        while (currentTask != null) {
            tasks.add(currentTask.getTask());
            currentTask = currentTask.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node == head) {
            head = node.getNext();
            if (head != null) {
                head.setPrev(null);
            } else {
                tail = null;
            }
        } else if (node == tail) {
            tail = node.getPrev();
            if (tail != null) {
                tail.setNext(null);
            }
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        int taskId = task.getIdNumber();
        if (nodeHistoryMap.containsKey(taskId)) {
            removeNode(nodeHistoryMap.get(taskId));
        }
        linkLast(task);
        nodeHistoryMap.put(taskId, tail);
    }

    @Override
    public void remove(int id) {
        if (nodeHistoryMap.containsKey(id)) {
            removeNode(nodeHistoryMap.get(id));
        }
    }

}
