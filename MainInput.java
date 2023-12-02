import java.util.concurrent.Semaphore;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class FittingRoom {
    // Semaphore to control the access to the fitting room
    private final Semaphore semaphore;
    private final AtomicBoolean turnIsBlue; // Indicator for whose turn it is
    private int threadsInside = 0;
    private final AtomicInteger blueWaiting; // Number of blue threads waiting
    private final AtomicInteger greenWaiting; // Number of green threads waiting


    public FittingRoom(int slots) {
        this.semaphore = new Semaphore(slots, true);
        this.turnIsBlue = new AtomicBoolean(true);
        this.blueWaiting = new AtomicInteger(0);
        this.greenWaiting = new AtomicInteger(0);
    }
    public void tryEnter(String color) throws InterruptedException {
        if (color.equals("Blue")) {
            blueWaiting.incrementAndGet(); // A blue thread is waiting
        } else {
            greenWaiting.incrementAndGet(); // A green thread is waiting
        }

        while (this.turnIsBlue.get() != color.equals("Blue") || 
              (this.turnIsBlue.get() && greenWaiting.get() > 0) || // Give priority to green if any are waiting
              (!this.turnIsBlue.get() && blueWaiting.get() > 0)) {
            Thread.sleep(10); // Sleep to prevent tight looping
        }

        if (color.equals("Blue")) {
            blueWaiting.decrementAndGet(); // Blue thread is no longer waiting
        } else {
            greenWaiting.decrementAndGet(); // Green thread is no longer waiting
        }

        semaphore.acquire(); // Acquire the semaphore if it's this color's turn
    }
    public void enter(String color, long threadId) {
        try {
            tryEnter(color); // Try to enter the room, waiting if it's not the turn of this color
            synchronized (this) {
                if (threadsInside == 0) {
                    System.out.println(color + " only.");
                }
                threadsInside++;
                System.out.println("Thread " + threadId + " " + color);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized void exit(String color, long threadId) {
        System.out.println("Thread " + threadId + " " + color + " is leaving.");
        threadsInside--;
        if (threadsInside == 0) {
            turnIsBlue.set(!turnIsBlue.get());
            System.out.println("Empty fitting room.");
        }
        semaphore.release();
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
