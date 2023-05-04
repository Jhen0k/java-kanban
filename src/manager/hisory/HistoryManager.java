package manager.hisory;

import tasks.Tasks;

import java.util.List;

public interface HistoryManager {
    List<Tasks> getHistory();

    void add(Tasks tasks);

    void remove(int id);

    boolean isEmpty();
}