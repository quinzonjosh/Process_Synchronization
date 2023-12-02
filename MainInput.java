import java.util.concurrent.Semaphore;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class FittingRoom {
    private final Semaphore semaphore;
    private final ReentrantLock lock = new ReentrantLock(true); // Fair lock to maintain order
    private final Queue<ThreadInfo> queue = new LinkedList<>();
    private int threadsInside = 0;
    private String currentColor = null;

    public FittingRoom(int slots) {
        this.semaphore = new Semaphore(slots, true);
    }

    private static class ThreadInfo {
        final Thread thread;
        final String color;

        ThreadInfo(Thread thread, String color) {
            this.thread = thread;
            this.color = color;
        }
    }

    private void tryEnter(String color) throws InterruptedException {
        lock.lock();
        try {
            ThreadInfo currentThreadInfo = new ThreadInfo(Thread.currentThread(), color);
            queue.add(currentThreadInfo);

            while (!queue.isEmpty() &&
                   (currentColor != null && !currentColor.equals(color) || // Different color is in the room
                    queue.peek() != currentThreadInfo || // Not this thread's turn
                    threadsInside >= semaphore.availablePermits())) { // No room available
                lock.unlock(); // Release the lock while waiting
                Thread.sleep(10); // Sleep to prevent tight looping
                lock.lock(); // Re-acquire the lock before checking conditions
            }

            queue.remove();
            threadsInside++;
            if (currentColor == null) {
                currentColor = color; // Set the current color if the room was empty
            }
            semaphore.acquire();
        } finally {
            lock.unlock();
        }
    }

    public void enter(String color) {
        try {
            tryEnter(color);
            System.out.println("Thread " + Thread.currentThread().getId() + " " + color);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void exit(String color) {
        lock.lock();
        try {
            System.out.println("Thread " + Thread.currentThread().getId() + " " + color + " is leaving.");
            threadsInside--;
            if (threadsInside == 0) {
                currentColor = null; // Reset the current color when the room becomes empty
                System.out.println("Empty fitting room.");
            }
            semaphore.release();
        } finally { 
            lock.unlock();
        }
    }
}

class ColoredThread extends Thread {
    private final FittingRoom fittingRoom;
    private final String color;

    public ColoredThread(FittingRoom fittingRoom, String color) {
        this.fittingRoom = fittingRoom;
        this.color = color;
    }

    @Override
    public void run() {
        fittingRoom.enter(color, this.getId());

        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        fittingRoom.exit(color, this.getId());
    }
}

public class MainInput {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of slots in the fitting room: ");
        int n = scanner.nextInt();
        System.out.print("Enter the number of blue threads: ");
        int b = scanner.nextInt();
        System.out.print("Enter the number of green threads: ");
        int g = scanner.nextInt();
        scanner.close();

        FittingRoom fittingRoom = new FittingRoom(n);


        for (int i = 0; i < b; i++) {
            new ColoredThread(fittingRoom, "Blue").start();
        }

        for (int i = 0; i < g; i++) {
            new ColoredThread(fittingRoom, "Green").start();
        }
    }
}
