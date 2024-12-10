package controllers;

import java.io.File;

public abstract class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFile()  {
        return new FileBackedTaskManager(new File("/Users/sergeyskvortsov/test_sprint7.csv")); }
}