package dev.dmcode.taskloop;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

public interface TaskLoopStopFuture {

    boolean await() throws InterruptedException;

    boolean await(long timeoutMs) throws InterruptedException;

    @RequiredArgsConstructor
    class ThreadJoin implements TaskLoopStopFuture {

        private final Thread thread;

        @Override
        public boolean await() throws InterruptedException {
            thread.join();
            return !thread.isAlive();
        }

        @Override
        public boolean await(long timeoutMs) throws InterruptedException {
            if (timeoutMs < 0) {
                throw new IllegalArgumentException("Timeout value must not be negative");
            }
            thread.join(timeoutMs);
            return !thread.isAlive();
        }
    }

    @RequiredArgsConstructor
    class Group implements TaskLoopStopFuture {

        private final Collection<TaskLoopStopFuture> futures;

        @Override
        public boolean await() throws InterruptedException {
            return await(Long.MAX_VALUE);
        }

        @Override
        public boolean await(long timeoutMs) throws InterruptedException {
            if (timeoutMs < 0) {
                throw new IllegalArgumentException("Timeout value must not be negative");
            }
            long startTimestamp = System.currentTimeMillis();
            for (var future : futures) {
                long elapsedTime = System.currentTimeMillis() - startTimestamp;
                long remainingTimeout = Math.max(0, timeoutMs - elapsedTime);
                if (!future.await(remainingTimeout)) {
                    return false;
                }
            }
            return true;
        }
    }
}
