import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThroughputMonitor {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startMonitoring() {
        scheduler.scheduleAtFixedRate(() -> {
            long count = PrinterI.getRequestCount();
            List<Long> jitterList = PrinterI.getJitterList();
            
            if (!jitterList.isEmpty()) {
                long totalJitter = jitterList.stream().mapToLong(Long::longValue).sum();
                long averageJitter = totalJitter / jitterList.size();
                System.out.println("Throughput: " + count + " requests in the last minute");
                System.out.println("Average Jitter: " + averageJitter + " ns");
            } else {
                System.out.println("Throughput: " + count + " requests in the last minute");
                System.out.println("No jitter data available.");
            }
            
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
