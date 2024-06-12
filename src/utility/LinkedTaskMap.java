package utility;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LinkedTaskMap<K, V> {
    private final Map<K, Node<V>> map = new HashMap<>();
    private Node<V> head;
    private Node<V> tail;
    private int size = 0;

    private static class Node<V> {
        V value;
        Node<V> next;
        Node<V> prev;

        Node (V value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<V> node = (Node<V>) o;
            return value.equals(node.value);
        }
    }

    public void put(K key, V value) {
        Node<V> newNode = new Node<>(value);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }

        map.put(key, newNode);
        size++;
    }

    public void remove(K key) {
        if (map.containsKey(key)) {

            Node<V> deletedNode = map.get(key);
            Node<V> prev = deletedNode.prev;
            Node<V> next = deletedNode.next;

            if (deletedNode.equals(head) && deletedNode.equals(tail)) {
                head = null;
                tail = null;
                map.remove(key);
            } else if (deletedNode.equals(head)) {
                next.prev = null;
                head = next;
            } else if (deletedNode.equals(tail)) {
                prev.next = null;
                tail = prev;
            } else {
                prev.next = next;
                next.prev = prev;
            }

            map.remove(key);
            size--;
        }
    }

    public ArrayList<V> values() {
        ArrayList<V> res = new ArrayList<>(map.size());
        Node<V> next = head;

        while (next != null) {
            res.add(next.value);
            next = next.next;
        }

        return res;
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean containsValue(V value) {
        return map.containsValue(value);
    }
}
