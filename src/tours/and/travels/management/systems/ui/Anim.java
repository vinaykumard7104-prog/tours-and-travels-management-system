package tours.and.travels.management.systems.ui;

import java.util.function.DoubleConsumer;
import javax.swing.Timer;

/** Tiny time-based animation helper (ease-out cubic, runs on the Swing thread). */
public final class Anim {

    private Anim() { }

    public static double easeOut(double t) {
        double u = 1 - t;
        return 1 - u * u * u;
    }

    public static double easeInOut(double t) {
        return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
    }

    /** Calls {@code step} with eased progress 0..1 for {@code ms} milliseconds, then {@code done}. */
    public static Timer run(int ms, DoubleConsumer step, Runnable done) {
        final long start = System.nanoTime();
        final Timer timer = new Timer(14, null);
        timer.addActionListener(e -> {
            double p = Math.min(1.0, (System.nanoTime() - start) / (ms * 1_000_000.0));
            step.accept(easeOut(p));
            if (p >= 1.0) {
                timer.stop();
                if (done != null) done.run();
            }
        });
        timer.setInitialDelay(0);
        step.accept(0);
        timer.start();
        return timer;
    }
}
