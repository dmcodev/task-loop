package dev.dmcode.taskloop;

import lombok.RequiredArgsConstructor;

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
}
