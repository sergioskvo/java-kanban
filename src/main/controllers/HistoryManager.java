package controllers;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();
    void historyCashAddAndCheck(Task task);
}
