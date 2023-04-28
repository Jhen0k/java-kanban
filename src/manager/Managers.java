package manager;

import manager.hisory.HistoryManager;
import manager.hisory.InMemoryHistoryManager;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;

// Патер singleton
// Сайт refactoring.guru
public class Managers {
    private static InMemoryHistoryManager inMemoryHistoryManager;
    private static InMemoryTaskManager inMemoryTaskManager;
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
        if (inMemoryTaskManager == null) {
            inMemoryTaskManager = new InMemoryTaskManager((InMemoryHistoryManager) getDefaultHistory());
        }
        return inMemoryTaskManager;
    }

    public static TaskManager backedTaskManager(Path path) {
        if (fileBackedTasksManager == null) {
            fileBackedTasksManager = new FileBackedTasksManager((InMemoryHistoryManager)getDefaultHistory(), path);
        }
        return fileBackedTasksManager;
    }
}
