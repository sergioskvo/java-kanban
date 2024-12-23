package controllers;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getTasks();

    void remove(int id);
}