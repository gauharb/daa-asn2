public class LinkedList<T> {

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(T x) {
        Node<T> node = new Node<>(x);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    public void add(int index, T x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
        if (index == size) {
            add(x);
            return;
        }
        if (index == 0) {
            Node<T> node = new Node<>(x);
            node.next = head;
            if (head != null) {
                head.prev = node;
            }
            head = node;
            if (tail == null) {
                tail = node;
            }
            size++;
            return;
        }

        Node<T> current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        Node<T> newNode = new Node<>(x);
        Node<T> before = current.prev;
        newNode.prev = before;
        newNode.next = current;
        before.next = newNode;
        current.prev = newNode;
        size++;
    }

    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
        Node<T> target = nodeAt(index);
        return unlink(target);
    }

    private T unlink(Node<T> node) {
        Node<T> before = node.prev;
        Node<T> after = node.next;

        if (before == null) {
            head = after;
        } else {
            before.next = after;
        }

        if (after == null) {
            tail = before;
        } else {
            after.prev = before;
        }

        node.prev = null;
        node.next = null;
        size--;
        return node.value;
    }

    public T get(int index) {
        return nodeAt(index).value;
    }

    private Node<T> nodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
        Node<T> current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    public void set(int index, T x) {
        nodeAt(index).value = x;
    }


    public boolean contains(T x) {
        return indexOf(x) >= 0;
    }

    public int indexOf(T x) {
        Node<T> current = head;
        int i = 0;

        while (current != null) {
            if (current.value == null ? x == null : current.value.equals(x)) {
                return i;
            }
            current = current.next;
            i++;
        }
        return -1;
    }


    public boolean containsWithCount(T x, long[] comparisonCounter) {
        Node<T> current = head;
        while (current != null) {
            comparisonCounter[0]++;
            if (current.value == null ? x == null : current.value.equals(x)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
}