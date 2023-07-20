package manager.hisory;

import java.util.LinkedList;
import java.util.List;

public final class CustomLinkedList<E> {

    private Node<E> head;

    private Node<E> tail;

    private int size;


    public CustomLinkedList() {

        this.head = null;

        this.tail = null;

    }


    public Node<E> linkLast(E element) {

        Node<E> t = tail;

        Node<E> newNode = new Node<>(t, element, null);

        tail = newNode;

        if (t == null) {

            head = newNode;

        } else {

            t.next = newNode;

        }

        size++;


        return newNode;

    }


    public List<E> getTasks() {

        Node<E> currentElement = head;


        List<E> resulted = new LinkedList<>();

        resulted.add(currentElement.element);

        while (currentElement != tail) {

            currentElement = currentElement.next;

            resulted.add(currentElement.element);

        }

        return resulted;

    }

    public int getSize() {
        return this.size;
    }


    public void removeNode(Node<E> node) {

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


protected static class Node<E> {

    Node<E> prev;

    Node<E> next;

    E element;


    Node(Node<E> prev, E element, Node<E> next) {

        this.prev = prev;

        this.next = next;

        this.element = element;

    }

}
}