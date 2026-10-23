package tours.and.travels.management.systems.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Runs database work off the Swing event thread and delivers the result back on it. */
public final class Async {

    private Async() { }

    public static <T> void run(Callable<T> work, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception {
                return work.call();
            }
            @Override protected void done() {
                try {
                    T result = get();
                    if (onSuccess != null) onSuccess.accept(result);
                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    if (onError != null) onError.accept(cause);
                    else cause.printStackTrace();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
