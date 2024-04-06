package birthdaypresentsparty;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Node {
    public int id;
    public Node next;
    public Node prev;
    public final Lock lock = new ReentrantLock();

    public Node(int id, Node next, Node prev) { 
        this.id = id; 
        this.next = next; 
        this.prev = prev;
    }

    int deleteSelf() {
        lockRelevantLocks();
        try {
            if (this.prev != null)
                this.prev.next = this.next;
            if (this.next != null)
                this.next.prev = this.prev;
            this.next = this.prev = null;
        } finally { 
            unlockRelevantLocks();
        }
        return this.id;
    }

    void insertNodeInFront(Node node) {
        lockRelevantLocks();
        try {

            node.next = this.next;
            node.prev = this;
            if (this.next != null)
            this.next.prev = node;
            this.next = node;
        } finally { 
            unlockRelevantLocks();
        }
    }
    
    void insertNodeBehind(Node node) {
        lockRelevantLocks();
        try { 
            node.next = this; 
            node.prev = this.prev;
            if (this.prev != null) 
            this.prev.next = node; 
            this.prev = node; 
        } finally { 
            unlockRelevantLocks();
        }
    }

    // Make sure to lock in order to prevent deadlock
    private void lockRelevantLocks() { 
        if (this.prev != null)
            this.prev.lock.lock();
        lock.lock();
        if (this.next != null)
            this.next.lock.lock();
    }

    private void unlockRelevantLocks() { 
        if (this.prev != null)
            this.prev.lock.unlock();
        lock.unlock();
        if (this.next != null)
            this.next.lock.unlock();
    }
}