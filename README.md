# Process_Synchronization

MCO2 for CSOPESY

### Synchronization technique

This project uses semaphores and reentrant locks as synchronization techniques. The number of available slots in the fitting room (n) corresponds to the number of permits. A thread enters and exits the room with the help of the sempahore's acquire() and release() methods. Meanwhile reentrant locks are used to establish mutual exclusivity on the critical sections of the code. The lock is acquired before entering the critical section and released afterward. To avoid starvation, the reentrant lock is combined with a fair policy ReentrantLock(True) to maintain the order acquiring threads. <br><br>

### List of variables for synchronization and their corresponding use

1. **Semaphore** -manages access to the fitting room by controlling the number of threads allowed to enter concurrently

2. **lock** - Ensures exclusive access to critical sections of code where shared data is accessed or modified

3. **queue** - Keeps track of the order in which threads arrived at the fitting room

4. **threadsInside** - Tracks the number of threads currently inside the fitting room

5. **currentColor** - Records the color of the clothing currently inside the fitting room

6. **ThreadInfo** - contains info about a thread, including the thread itself and the color of its clothing

### Part of the code that satisfies the constraints

1. There are only n slots inside the fitting room of a department store. Thus, there can only be at most n persons inside the fitting room at a time.

The fitting room class is initialized as follows:

```
public FittingRoom(int slots) {
    this.semaphore = new Semaphore(slots, true);
}
```

Here, the `semaphore` is a variable that controls access to the fitting room. The parameter `slots` determine the max number of threads allowed inside the fitting room simultaneously.

The `tryEnter` method checks the fitting room before a thread enters. The code below checks if the fitting room is full. If it is full the thread will wait.

```
while (!queue.isEmpty() &&
        (currentColor != null && !currentColor.equals(color) ||
        queue.peek() != currentThreadInfo ||
        threadsInside >= semaphore.availablePermits())) { // No room available
    lock.unlock(); // Release the lock while waiting
    Thread.sleep(10); // Sleep to prevent tight looping
    lock.lock(); // Re-acquire the lock before checking conditions
}
```

2. There cannot be a mix of blue and green in the fitting room at the same time. Thus, there can only be at most n blue threads or at most n green threads inside the fitting room at a time.

   
This is satisfied by the following part of the code:
```
while (!queue.isEmpty() &&
       (currentColor != null && !currentColor.equals(color) || // Different color is in the room
        queue.peek() != currentThreadInfo || // Not this thread's turn
        threadsInside >= semaphore.availablePermits())) { // No room available
    lock.unlock(); // Release the lock while waiting
    Thread.sleep(10); // Sleep to prevent tight looping
    lock.lock(); // Re-acquire the lock before checking conditions
}
```
In this segment, currentColor != null && !currentColor.equals(color) ensures that a thread (either blue or green) will wait if the current color inside the fitting room is different from its own color. This effectively prevents the mixing of blue and green threads inside the fitting room at the same time.

3. The solution should not result in deadlock.

4. The solution should not result in starvation. For example, blue threads cannot forever be blocked from entering the fitting room if green threads are lining up to enter as well.

This is addressed in the code with the use of a fair ReentrantLock (initialized with true) and a queue to manage threads:
```
private final ReentrantLock lock = new ReentrantLock(true); // Fair lock to maintain order
private final Queue<ThreadInfo> queue = new LinkedList<>();
```
The fair lock ensures that threads acquire the lock in the order they requested it, preventing indefinite postponement (or starvation) of any thread. The queue is used to maintain the order of threads waiting to enter the fitting room, further supporting the fairness in processing thread requests.

These mechanisms combined ensure that threads are served in a fair manner, respecting the order of arrival and preventing the possibility of starvation.
