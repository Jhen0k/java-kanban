package manager.hisory;

import tasks.AbstractTasks;

import java.util.List;

public interface HistoryManager {
    List<AbstractTasks> getHistory();

    void add(AbstractTasks abstractTasks);

    void remove(int id);

    boolean isEmpty();
}