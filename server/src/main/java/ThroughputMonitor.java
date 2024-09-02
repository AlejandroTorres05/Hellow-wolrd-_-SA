import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThroughputMonitor {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startMonitoring() {
        scheduler.scheduleAtFixedRate(() -> {
            long count = PrinterI.getRequestCount();
            System.out.println("Throughput: " + count + " requests in the last minute");
            PrinterI.resetRequestCount();
        }, 0, 1, TimeUnit.MINUTES);
    }

    public static void stopMonitoring() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
