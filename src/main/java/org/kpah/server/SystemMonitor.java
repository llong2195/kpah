package org.kpah.server;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.List;

import org.kpah.utils.Printer;

public class SystemMonitor {
    private final static ThreadMXBean threadMXBean;
    private final static MemoryMXBean memoryMXBean;
    private final static List<GarbageCollectorMXBean> gcBeans;
    private final static ClassLoadingMXBean classLoadingMXBean;
    private final static OperatingSystemMXBean osMXBean;
    private final static RuntimeMXBean runtimeMXBean;

    static {
        threadMXBean = ManagementFactory.getThreadMXBean();
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        osMXBean = ManagementFactory.getOperatingSystemMXBean();
        runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    }

    public static final SystemMonitor instance = new SystemMonitor();

    public static void printStats() {
        printHeader("THREAD");
        Printer.printGreen("Current thread count: " + threadMXBean.getThreadCount());
        Printer.printGreen("Peak thread count: " + threadMXBean.getPeakThreadCount());
        Printer.printGreen("Total started threads: " + threadMXBean.getTotalStartedThreadCount());

        printHeader("MEMORY");
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memoryMXBean.getNonHeapMemoryUsage();
        Printer.printGreen("Heap: used=" + toMB(heap.getUsed()) + "MB, committed="
                + toMB(heap.getCommitted()) + "MB, max=" + toMB(heap.getMax()) + "MB");
        Printer.printGreen("Non-Heap: used=" + toMB(nonHeap.getUsed()) + "MB, committed="
                + toMB(nonHeap.getCommitted()) + "MB, max=" + toMB(nonHeap.getMax()) + "MB");

        printHeader("GARBAGE COLLECTORS");
        for (GarbageCollectorMXBean gc : gcBeans) {
            Printer.printGreen(gc.getName() + ": count=" + gc.getCollectionCount()
                    + ", time=" + gc.getCollectionTime() + "ms");
        }

        printHeader("CLASS LOADING");
        Printer.printGreen("Currently loaded: " + classLoadingMXBean.getLoadedClassCount());
        Printer.printGreen("Total loaded: " + classLoadingMXBean.getTotalLoadedClassCount());
        Printer.printGreen("Total unloaded: " + classLoadingMXBean.getUnloadedClassCount());

        printHeader("CPU & SYSTEM");
        Printer.printGreen("Available processors: " + osMXBean.getAvailableProcessors());
        Printer.printGreen("System load average: " + osMXBean.getSystemLoadAverage());

        printHeader("RUNTIME");
        Printer.printGreen("Uptime: " + runtimeMXBean.getUptime() + "ms");
        Printer.printGreen("JVM arguments: " + runtimeMXBean.getInputArguments());
        Printer.printGreen("JVM name: " + runtimeMXBean.getName());
    }

    private static String toMB(long bytes) {
        return String.format("%.2f", bytes / (1024.0 * 1024.0));
    }

    private static void printHeader(String title) {
        Printer.printGreen("\n=== " + title + " ===");
    }

}
