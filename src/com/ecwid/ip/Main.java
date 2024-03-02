package com.ecwid.ip;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class Main {
    private static final String FILE = "e:/ip_addresses";

    private static final long LOG_STEP = 10_000_000;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static long counter = 1;
    private static long maxUsedMemory = 0;

    public static void main(String[] args) {
        IPCounter ipCounter = new IPCounter();

        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get(FILE))) {
            stream.forEach(ip -> {
                log();
                ipCounter.addIP(ip);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        long finish = System.currentTimeMillis();

        long totalTime = (finish - start) / 1000;

        System.out.println("Total time:         " + totalTime + " sec");
        System.out.println("Memory max/cur:     " + getUsedMemory());
        System.out.println("Added IP`s amount:  " + ipCounter.getAddedIpCount());
        System.out.println("Unique IP`s amount: " + ipCounter.getIPCount());
    }

    private static void log() {
        if (counter++ % LOG_STEP == 0) {
            String time = LocalTime.now().format(TIME_FORMATTER);
            System.out.println(time + " ; " + counter / 1_000_000 + " M ip`s added ; Memory max/cur " + getUsedMemory());
        }
    }

    private static String getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = (total - free) / 1048576;
        if (used > maxUsedMemory) maxUsedMemory = used;
        return maxUsedMemory + "/" + used + " MB";
    }
}