package manager;

import manager.file.FileBackedTasksManager;
import manager.hisory.HistoryManager;
import manager.hisory.InMemoryHistoryManager;
import manager.http.HttpTaskManager;

import java.nio.file.Path;

// Патер singleton
// Сайт refactoring.guru
public final class Managers {
    private static InMemoryHistoryManager inMemoryHistoryManager;
    private static HttpTaskManager httpTaskManager;
    private static FileBackedTasksManager fileBackedTasksManager;

    private Managers() {
        throw new IllegalStateException("Utility class");
    }

    public static HistoryManager getDefaultHistory() {
        if (inMemoryHistoryManager == null) {
            inMemoryHistoryManager = new InMemoryHistoryManager();
        }
        return inMemoryHistoryManager;
    }

    public static TaskManager getDefault() {
        if (httpTaskManager == null) {
            httpTaskManager = new HttpTaskManager();
        }
        return httpTaskManager;
    }

    public static TaskManager backedTaskManager(Path path) {
        if (fileBackedTasksManager == null) {
            fileBackedTasksManager = new FileBackedTasksManager((InMemoryHistoryManager)getDefaultHistory(), path);
        }
        return fileBackedTasksManager;
    }
}
