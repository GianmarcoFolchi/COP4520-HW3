package birthdaypresentsparty;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BirthdayPresentsParty {
    static final int NUM_PRESENTS = 500;
    static AtomicInteger numPresentsAdded = new AtomicInteger(0); 
    static AtomicInteger numCardsWritten = new AtomicInteger(0);
    static Random rand = new Random(); 
    static Lock presentsBagLock = new ReentrantLock();
    static Boolean[] presentsBag = new Boolean[NUM_PRESENTS];
    static ConcurrentLinkedList linkedList = new ConcurrentLinkedList(); 

    public static void main(String[] args) {
        Arrays.fill(presentsBag, Boolean.FALSE);
        Thread[] threads = new Thread[1];
        for(int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                while (numPresentsAdded.get() < NUM_PRESENTS || numCardsWritten.get() < NUM_PRESENTS) { 
                    int randInt = rand.nextInt(2);
                    if (randInt == 0) { 
                        if (numPresentsAdded.get() < NUM_PRESENTS)
                            addPresent();
                    } else { 
                        if (numCardsWritten.get() < NUM_PRESENTS)
                            writeThankYouNote();
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Finished");
    }

    public static void addPresent() { 
        boolean giftAdded = false; 
        presentsBagLock.lock();
        try { 
            while (giftAdded == false) { 
                int id = Arrays.asList(presentsBag).indexOf(false);
                if (id == -1) { 
                    presentsBagLock.unlock();
                    return;
                }
                presentsBag[id] = true; 
                linkedList.insertNode(id);
                giftAdded = true; 
                numPresentsAdded.incrementAndGet();
            }
        } finally {
            presentsBagLock.unlock(); 
        }
    }

    public static void writeThankYouNote() { 
        if (linkedList.tail == null)
            return;
        linkedList.tail.deleteSelf(); 
        numCardsWritten.incrementAndGet(); 
    }

}
