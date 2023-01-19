package Manager;

// Патер singleton
// Сайт refactoring.guru
public class Managers {
    private static InMemoryHistoryManager inMemoryHistoryManager;
    private static InMemoryTaskManager inMemoryTaskManager;

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
}
