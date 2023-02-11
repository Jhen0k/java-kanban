package manager.hisory;

import tasks.Task;

import java.util.LinkedList;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyTask = new CustomLinkedList<>();
    private final HashMap<Integer, Node<Task>> map = new HashMap<>();

    public InMemoryHistoryManager() {  // Конструктор
    }

    @Override
    public void add(Task task) {
        if (!map.containsKey(task.getId())) {
            Node<Task> node = historyTask.linkLast(task);
            map.put(task.getId(), node);
        } else {
            historyTask.removeNode(map.get(task.getId()));
            map.remove(task.getId());
            Node<Task> node = historyTask.linkLast(task);
            map.put(task.getId(), node);
        }
    }

    @Override
    public void remove(int id) {
        historyTask.removeNode(map.get(id));
        map.remove(id);
    }

    @Override
    public List<Task> getHistory() {

        return historyTask.getTasks();
    }

    final static class CustomLinkedList<E> {
        private Node<E> head;
        private Node<E> tail;
        public int size = 0;

        private CustomLinkedList() {
            this.head = null;
            this.tail = null;
        }

        private Node<E> linkLast(E element) {
            Node<E> t = tail;
            Node<E>newNode = new Node<>(t, element, null);
            tail = newNode;
            if (t == null) {
                head = newNode;
            } else {
                t.next = newNode;
            }
            size++;

            return newNode;
        }

        private List<E> getTasks() {
            Node<E> currentElement = head;

            List<E> resulted = new LinkedList<>();
            resulted.add( currentElement.element);
            while (currentElement != tail) {
                currentElement = currentElement.next;
                resulted.add( currentElement.element);
            }
            return resulted;
        }

        private void removeNode(Node<E> node) {
            final Node<E> next = node.next;
            final Node<E> prev = node.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.element = null;
        }
    }

    private static class Node<E> {
        Node<E> prev;
        Node<E> next;
        E element;

        Node(Node<E> prev ,E element, Node<E> next) {
            this.prev = prev;
            this.next = next;
            this.element = element;
        }
    }
}
