package managers;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task task;
        Node prev;
        Node next;

        private Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Task = " + task.toString()
                    + "; Prev = " + prev.task.toString()
                    + "; Next = " + next.task.toString();
        }

        @Override

        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass())
                return false;

            Node node = (Node) obj;

            if (this.task == node.task && this.prev == node.prev && this.next == node.next)
                return true;
            return false;
        }
    }

    private Map<Integer, Node> nodeMap = new HashMap<>();
    Node first;
    Node last;

    InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        putLast(task);
        nodeMap.put(task.getId(), last);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (!nodeMap.containsKey(id))
            return;
        Node node = nodeMap.get(id);
        if (first.equals(last)) {
            first = null;
            last = null;
            return;
        }
        if (node.equals(first)) {
            first = node.next;
            first.prev = null;
        } else if (node.equals(last)) {
            last = node.prev;
            last.next = null;
        } else {
            Node prevNode = node.prev;
            Node nextNode = node.next;

            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    private List<Task> getTasks() {
        Node node = first;
        List<Task> tasks = new ArrayList<>();
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }

        return tasks;
    }

    private void putLast(Task task) {
        if (first == null) {
            last = new Node(task, null, null);
            first = last;
        } else {
            Node preLast = last;
            last = new Node(task, preLast, null);
            preLast.next = last;
        }
    }
}
