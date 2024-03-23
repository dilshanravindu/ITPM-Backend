package com.itmp;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;

@RestController
@CrossOrigin
class ConcurrentThreadsDetector {

    @PostMapping("/detect-concurrent-threads")
    public int detectConcurrentThreads(@RequestBody String payload) {
        String code = extractCodeFromPayload(payload);

        if (code == null) {
            // Handle invalid payload
            return -1; // Or throw an exception
        }

        String[] lines = code.split("\n");
        Set<String> runningThreads = new HashSet<>();
        int concurrentThreadCount = 0;

        for (String line : lines) {
            // Detect thread creation
            Matcher creationMatcher = Pattern.compile("\\b(\\w+)\\s*=\\s*new\\s+Thread\\(.*\\)").matcher(line);
            while (creationMatcher.find()) {
                String threadName = creationMatcher.group(1);
                runningThreads.add(threadName);
            }

            // Detect thread start
            Matcher startMatcher = Pattern.compile("\\.start\\(\\)").matcher(line);
            while (startMatcher.find()) {
                for (String threadName : runningThreads) {
                    if (line.contains(threadName + ".start()")) {
                        concurrentThreadCount++;
                        break; // Break once a start is found in this line
                    }
                }
            }

            // Detect thread join
            Matcher joinMatcher = Pattern.compile("\\.join\\(\\)").matcher(line);
            while (joinMatcher.find()) {
                for (String threadName : runningThreads) {
                    if (line.contains(threadName + ".join()")) {
                        // A thread is considered finished if it's joined
                        runningThreads.remove(threadName);
                        break; // Break once a join is found in this line
                    }
                }
            }
        }

        return concurrentThreadCount;
    }

    private String extractCodeFromPayload(String payload) {
        try {
            // Replace illegal characters before parsing
            payload = payload.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payload);
            return node.get("code").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

//sample code
//
//public class TestConcurrentThreadsDetector {
//
//    public static void main(String[] args) {
//        String code1 = """
//        String thread1 = new Thread(() -> {
//            System.out.println("Thread 1 running");
//        });
//        thread1.start();
//
//        String thread2 = new Thread(() -> {
//            System.out.println("Thread 2 running");
//        });
//        thread2.start();
//
//
//        """;
//
//        String code2 = """
//        String thread1 = new Thread(() -> {
//            System.out.println("Thread 1 running");
//        });
//        thread1.start();
//
//        String thread2 = new Thread(() -> {
//            System.out.println("Thread 2 running");
//        });
//        thread2.start();
//
//        try {
//            thread1.join();
//            thread2.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        """;
//
//        int concurrentThreads1 = ConcurrentThreadsDetector.countConcurrentThreads(code1);
//        System.out.println("Code 1: Maximum Concurrent Threads = " + concurrentThreads1);
//
//        int concurrentThreads2 = ConcurrentThreadsDetector.countConcurrentThreads(code2);
//        System.out.println("Code 2: Maximum Concurrent Threads = " + concurrentThreads2);
//    }



//
//import java.util.concurrent.ArrayBlockingQueue;
//
//public class ConcurrentThreadsExample {
//
//    static class Producer implements Runnable {
//        private final ArrayBlockingQueue<Integer> queue;
//
//        Producer(ArrayBlockingQueue<Integer> queue) {
//            this.queue = queue;
//        }
//
//        public void run() {
//            try {
//                for (int i = 0; i < 10; i++) {
//                    System.out.println("Producing: " + i);
//                    queue.put(i);
//                    Thread.sleep(100);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    static class Consumer implements Runnable {
//        private final ArrayBlockingQueue<Integer> queue;
//
//        Consumer(ArrayBlockingQueue<Integer> queue) {
//            this.queue = queue;
//        }
//
//        public void run() {
//            try {
//                while (true) {
//                    Integer value = queue.take();
//                    System.out.println("Consumed: " + value);
//                    Thread.sleep(200);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
//
//        Producer producer = new Producer(queue);
//        Consumer consumer = new Consumer(queue);
//
//        Thread producerThread = new Thread(producer);
//        Thread consumerThread = new Thread(consumer);
//
//        producerThread.start();
//        consumerThread.start();
//    }
//}