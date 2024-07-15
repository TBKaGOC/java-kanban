package main.utility;

import java.util.*;

public class LinkedTaskMap<K, V> {
    private final Map<K, Node<V>> map;
    private Node<V> head;
    private Node<V> tail;

    private static class Node<V> {
        V value;
        Node<V> next;
        Node<V> prev;

        Node(V value) {
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
            Node<?> node = (Node<?>) o;
            return Objects.equals(value, node.value);
        }
    }

    public LinkedTaskMap() {
        map = new HashMap<>();
    }

    public void put(K key, V value) {
        Node<V> newNode = new Node<>(value);

        if (head == null) {
            head = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
        }

        tail = newNode;
        map.put(key, newNode);
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
        }
    }

    public List<V> values() {
        List<V> res = new ArrayList<>(map.size());
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
}
