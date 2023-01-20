package manager;

import manager.hisory.HistoryManager;
import manager.hisory.InMemoryHistoryManager;

// Патер singleton
// Сайт refactoring.guru
public class Managers {
    private static InMemoryHistoryManager inMemoryHistoryManager;
    private static InMemoryTaskManager inMemoryTaskManager;

    private static HistoryManager getDefaultHistory() {
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
}
