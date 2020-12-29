package com.gmail.leonard.spring;

import com.gmail.leonard.spring.backend.CustomThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Console {

    private final static Logger LOGGER = LoggerFactory.getLogger(Console.class);

    private static final Console instance = new Console();

    public static Console getInstance() {
        return instance;
    }

    private Console() {
        registerTasks();
    }

    private boolean started = false;
    private final HashMap<String, ConsoleTask> tasks = new HashMap<>();

    public void start() {
        if (started) return;
        started = true;

        new CustomThread(this::manageConsole, "console", 1).start();
    }

    private void registerTasks() {
        tasks.put("help", this::onHelp);

        tasks.put("quit", this::onQuit);
        tasks.put("threads", this::onThreads);
        tasks.put("threads_stop", this::onThreadStop);
    }

    private void onThreadStop(String[] args) {
        int stopped = 0;

        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (args.length < 2 || t.getName().matches(args[1])) {
                t.interrupt();
                stopped++;
            }
        }

        LOGGER.info("{} thread/s interrupted", stopped);
    }

    private void onThreads(String[] args) {
        StringBuilder sb = new StringBuilder();

        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (args.length < 2 || t.getName().matches(args[1])) {
                sb.append(t.getName()).append(", ");
            }
        }

        String str = sb.toString();
        if (str.length() >= 2) str = str.substring(0, str.length() - 2);

        LOGGER.info("\n--- THREADS ({}) ---\n{}\n", Thread.getAllStackTraces().size(), str);
    }

    private void onQuit(String[] args) {
        LOGGER.info("EXIT - User commanded exit");
        System.exit(0);
    }

    private void onHelp(String[] args) {
        tasks.keySet().stream()
                .filter(key -> !key.equals("help"))
                .sorted()
                .forEach(key -> System.out.println("- " + key));
    }

    private void manageConsole() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                if (br.ready()) {
                    String[] args = br.readLine().split(" ");
                    ConsoleTask task = tasks.get(args[0]);
                    if (task != null) {
                        new CustomThread(() -> {
                            try {
                                task.process(args);
                            } catch (Throwable throwable) {
                                LOGGER.error("Console task {} endet with exception", args[0], throwable);
                            }
                        }, "console_task_" + args[0], 1).start();
                    } else {
                        System.err.printf("No result for \"%s\"\n", args[0]);
                    }
                }
                Thread.sleep(100);
            } catch (IOException | InterruptedException e) {
                LOGGER.error("Unexpected console exception", e);
            }
        }
    }


    public interface ConsoleTask {

        void process(String[] args) throws Throwable;

    }

}