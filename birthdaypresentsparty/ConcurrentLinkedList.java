package birthdaypresentsparty;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



class ConcurrentLinkedList { 
    public Node head; 
    public Node tail; 
    private final Lock headLock = new ReentrantLock();
    private final Lock tailLock = new ReentrantLock();
    
    public Node isIDPresent(int id) { 
        Node runner = head; 
        while (runner != null) { 
            if (runner.id == id) { 
                return runner;
            }
            runner = runner.next;
        }
        return null; 
    }

    public Node insertNode(int id) { 
        Node newNode = new Node(id, null, null); 
        newNode.lock.lock(); 
        if (head == null) {
            setHeadIfListEmpty(newNode);
            return newNode;
        }

        if (head.id > id) { 
            head.insertNodeBehind(newNode);
            this.headLock.lock();
            try {
                this.head = newNode;
            } finally { 
                this.headLock.unlock();
            }
            return newNode;
        }

        Node runner = head;
        while (runner != null) {
            runner.lock.lock();
            if (runner.next != null) { 
                runner.next.lock.lock(); 
            }
            if (runner.id < id && (runner.next == null || runner.next.id > id)) {
                runner.insertNodeInFront(newNode);
                return newNode;
            }
            runner = runner.next;
        }
        return null;
    }

    private void setHeadIfListEmpty(Node head) { 
        this.headLock.lock();
        this.tailLock.lock();
        try {
            this.head = head;
            this.tail = head;
        } finally {
            this.headLock.unlock();
            this.tailLock.unlock();
        }
    }
}