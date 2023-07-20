package manager.hisory;

import tasks.AbstractTasks;

import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<AbstractTasks> historyTask = new CustomLinkedList<>();

    private final HashMap<Integer, CustomLinkedList.Node<AbstractTasks>> map = new HashMap<>();


    public InMemoryHistoryManager() {  // Конструктор

    }


    @Override

    public void add(AbstractTasks tasks) {

        if (!map.containsKey(tasks.getId())) {

            CustomLinkedList.Node<AbstractTasks> node = historyTask.linkLast(tasks);

            map.put(tasks.getId(), node);

        } else {

            historyTask.removeNode(map.get(tasks.getId()));

            map.remove(tasks.getId());

            CustomLinkedList.Node<AbstractTasks> node = historyTask.linkLast(tasks);

            map.put(tasks.getId(), node);

        }

    }


    @Override

    public void remove(int id) {

        historyTask.removeNode(map.get(id));

        map.remove(id);

    }


    @Override

    public List<AbstractTasks> getHistory() {

        if (historyTask.getSize() == 0) {

            return null;

        }

        return historyTask.getTasks();

    }


    public boolean isEmpty() {

        return historyTask.getSize() == 0;

    }
}

