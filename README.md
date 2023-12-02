# Process_Synchronization
MCO2 for CSOPESY

**Synchronization  technique**
    This project uses semaphores and reentrant locks as synchronization techniques. The number of available slots in the fitting room (n) corresponds to the number of permits. A thread enters and exits the room with the help  of the sempahore's acquire() and release() methods. Meanwhile reentrant locks are used to establish mutual exclusivity on the critical sections of the code. The lock is acquired before entering the critical section and released afterward. To avoid starvation, the reentrant lock is combined with a fair policy ReentrantLock(True) to maintain the order acquiring threads. 


   