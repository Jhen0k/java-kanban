package Manager;
import Tasks.Task;

import Manager.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTask = new ArrayList<>();

    public InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        if (historyTask.size() != 10) { // Сделать проверку на количество объектов в списке.
            this.historyTask.add(task);
        } else {
            historyTask.remove(0);
            this.historyTask.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {

        return historyTask;
    }
}
