### Simple library for handling repetitive tasks

`TaskLoop` is a simple abstraction over a thread that executes the same action in a loop, sleeping for configuration amount of time between invocations. 

Key improvement over using `ScheduledExecutorService` is ability to wake up `TaskLoop` in any moment from a different thread, for example in reaction to an incoming event - `wakeup()` operation forces immediate `Task` execution without waiting for the next scheduled one.

By returning `Task.Result` from the executed `Task` one can have better control over the next execution:
 - `Task.Result.ok()` schedules the next execution using the task interval configured for the current `TaskLoop` instance
 - `Task.Result.repeat()` forces the next execution to happen immediately
 - `Task.Result.sleep(Duration)` sets the delay before the next execution to a given duration

`TaskLoopGroup` can be used to create and group multiple `TaskLoop` instances together.

#### Examples

[Can be found here](src/test/java/dev/dmcode/taskloop).
