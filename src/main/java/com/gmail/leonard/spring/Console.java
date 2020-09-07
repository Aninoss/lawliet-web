package com.gmail.leonard.spring;

import com.gmail.leonard.spring.Backend.CustomThread;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class Console {

    private static final Console instance = new Console();
    private double maxMemory = 0;
    private final static Logger LOGGER = LoggerFactory.getLogger(Console.class);

    private Console() {}

    public static Console getInstance() {
        return instance;
    }

    public void start() {
        new CustomThread(this::manageConsole, "Console", 1).start();
        new CustomThread(this::trackMemory, "Console MemoryTracker", 1).start();
    }

    private void manageConsole() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                if (br.ready()) {
                    String s = br.readLine();
                    String command = s;
                    String arg = "";
                    if (s != null) {
                        if (command.contains(" ")) {
                            command = command.split(" ")[0];
                            arg = s.substring(command.length() + 1);
                        }
                        switch (command) {
                            case "quit":
                                System.exit(0);
                                break;

                            case "stats":
                                System.out.println(getStats());
                                break;

                            case "threads":
                                try {
                                    StringBuilder sb = new StringBuilder();

                                    for (Thread t : Thread.getAllStackTraces().keySet()) {
                                        if (arg.isEmpty() || t.getName().matches(arg)) {
                                            sb.append(t.getName()).append(", ");
                                        }
                                    }

                                    String str = sb.toString();
                                    if (str.length() >= 2) str = str.substring(0, str.length() - 2);

                                    System.out.println("\n--- THREADS (" + Thread.getAllStackTraces().size() + ") ---");
                                    System.out.println(str + "\n");
                                } catch (Throwable e) {
                                    LOGGER.error("Could not list threads", e);
                                }

                                break;

                            default:
                                System.err.println("No result");
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Error in console");
            }
        }
    }

    private void trackMemory() {
        try {
            while (true) {
                double memoryTotal = Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0);
                double memoryUsed = memoryTotal - (Runtime.getRuntime().freeMemory() / (1024.0 * 1024.0));
                maxMemory = Math.max(maxMemory, memoryUsed);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted", e);
        }
    }

    private String getStats() {
        StringBuilder sb = new StringBuilder("\n--- STATS ---\n");

       //Memory
        double memoryTotal = Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0);
        double memoryUsed = memoryTotal - (Runtime.getRuntime().freeMemory() / (1024.0 * 1024.0));
        sb.append("Memory: ").append(String.format("%1$.2f", memoryUsed) + " / " + String.format("%1$.2f", memoryTotal) + " MB").append("\n");

        //Max Memory
        maxMemory = Math.max(maxMemory, memoryUsed);
        sb.append("Max Memory: ").append(String.format("%1$.2f", maxMemory) + " / " + String.format("%1$.2f", memoryTotal) + " MB").append("\n");

        //Threads
        sb.append("Threads: ").append(Thread.getAllStackTraces().keySet().size()).append("\n");

        //CPU Usage
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        double cpuJvm = osBean.getProcessCpuLoad();
        double cpuTotal = osBean.getSystemCpuLoad();

        sb.append("CPU JVM: ").append(Math.floor(cpuJvm * 1000) / 10 + "%").append("\n");
        sb.append("CPU Total: ").append(Math.floor(cpuTotal * 1000) / 10 + "%").append("\n");

        return sb.toString();
    }

}