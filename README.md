# Process_Synchronization
MCO2 for CSOPESY

### Synchronization  technique

This project uses semaphores and reentrant locks as synchronization techniques. The number of available slots in the fitting room (n) corresponds to the number of permits. A thread enters and exits the room with the help  of the sempahore's acquire() and release() methods. Meanwhile reentrant locks are used to establish mutual exclusivity on the critical sections of the code. The lock is acquired before entering the critical section and released afterward. To avoid starvation, the reentrant lock is combined with a fair policy ReentrantLock(True) to maintain the order acquiring threads. <br><br>

### List of variables for synchronization and their corresponding use

1. **Semaphore**
    - manages access to the fitting room by controlling the number of threads allowed to enter concurrently

2. **lock**
    - Ensures exclusive access to critical sections of code where shared data is accessed or modified

3. **queue**
    - Keeps track of the order in which threads arrived at the fitting room

4. **threadsInside**
    - Tracks the number of threads currently inside the fitting room

5. **currentColor**
    - Records the color of the clothing currently inside the fitting room

6. **ThreadInfo**
    - contains info about a thread, including the thread itself and the color of its clothing



   