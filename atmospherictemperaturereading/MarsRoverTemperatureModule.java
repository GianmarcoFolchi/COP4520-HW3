package atmospherictemperaturereading;

import java.util.Arrays;
import java.util.Random;

public class MarsRoverTemperatureModule {

    public static void main(String[] args) {
        int NUM_REPORT_THREADS = 8;
        int NUM_ITERATIONS = 5;
        int NUM_TIME_READINGS = NUM_ITERATIONS * 60;
        int[][] readings = new int[NUM_REPORT_THREADS][NUM_TIME_READINGS];
        Random rand = new Random();
        Thread[] threads = new Thread[NUM_REPORT_THREADS + 1];

        for (int i = 0; i < threads.length - 1; i++) {
            threads[i] = new Thread(() -> {
                int threadID = Thread.currentThread().getName().charAt(7) - '0';
                for (int j = 0; j < NUM_TIME_READINGS; j++) {
                    int randomTemp = rand.nextInt(171) - 100;
                    readings[threadID][j] = randomTemp;
                }
            });
            threads[i].start();
        }
        threads[NUM_REPORT_THREADS] = new Thread(() -> {
            // Create the report for every iteration here
            for (int t = 0; t < NUM_ITERATIONS; t++) {
                int maxDifference = 0;
                int startIndexOfMaxDiff = 0;
                int sensorWithMaxDiff = 0;

                for (int i = 0; i < NUM_REPORT_THREADS; i++) {
                    for (int j = 60 * t; j < (60 * (t + 1)) - 20; j++) {
                        j = Math.min(j, NUM_TIME_READINGS - 1);
                        int tempDiff = Arrays.stream(readings[i], j, Math.min(j + 10, NUM_TIME_READINGS - 1)).max().getAsInt()
                                - Arrays.stream(readings[i], j, Math.min(j + 10, NUM_TIME_READINGS)).min().getAsInt();
                        if (tempDiff > maxDifference) {
                            maxDifference = tempDiff;
                            startIndexOfMaxDiff = j;
                            sensorWithMaxDiff = i;
                        }
                    }
                }

                System.out.println("Largest temperature difference: " + maxDifference);
                System.out.println("Starting index of largest difference (minute): " + startIndexOfMaxDiff);
                System.out.println("Sensor with largest difference: " + sensorWithMaxDiff);
            }
        });
        threads[NUM_REPORT_THREADS].start();
        try {
            threads[NUM_REPORT_THREADS].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            // Handle the exception, e.g., by logging it or cleaning up resources
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }
}
