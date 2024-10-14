package controllers;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyList = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyList);
    }

    @Override
    public void historyCashAddAndCheck(Task task) {
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        historyList.add(task);
    }
}
