package com.ruchira.murex;


import com.ruchira.murex.model.AggregatedDataResponse;
import com.ruchira.murex.parser.DynamicFieldParser;
import com.ruchira.murex.util.CloneUtils;
import com.ruchira.murex.util.VarHandleMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {

    @Test
    void loadTestUsingVarHandler() throws InterruptedException {
        log.info("Load Test started");

        int users = 1000;
        int requestsPerUser = 2000;
        AtomicInteger counter = new AtomicInteger();

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(users * requestsPerUser);

        // Prepare fields for set/get/copy operations
        List<String> allFields = new ArrayList<>(VarHandleMapper.getVarHandles(AggregatedDataResponse.class).keySet());
        Collections.shuffle(allFields);
        Set<String> setFields = new HashSet<>(allFields.subList(0, 30));
        Set<String> getFields = new HashSet<>(allFields.subList(30, 50));
        Set<String> copyFields = new HashSet<>(allFields.subList(50, 80));

        long start = System.nanoTime();
        VarHandleMapper<AggregatedDataResponse, AggregatedDataResponse> mapper =
                new VarHandleMapper<>(AggregatedDataResponse.class, AggregatedDataResponse.class);

        AggregatedDataResponse source = new AggregatedDataResponse();
        for (int u = 0; u < users; u++) {
            for (int r = 0; r < requestsPerUser; r++) {
                executor.submit(() -> {
                    try {


                        // --- 30 set operations ---
                        for (String field : setFields) {
                            Class<?> type = VarHandleMapper.getVarHandles(source.getClass()).get(field).varType();
                            if (type == String.class) VarHandleMapper.setField(source, field, "test");
                            else if (type == BigDecimal.class) VarHandleMapper.setField(source, field, BigDecimal.TEN);
                            else if (type == LocalDate.class) VarHandleMapper.setField(source, field, LocalDate.now());
                            else if (type == LocalDateTime.class)
                                VarHandleMapper.setField(source, field, LocalDateTime.now());
                        }

                        // --- 20 get operations ---
                        for (String field : getFields) {
                            VarHandleMapper.getField(source, field);
                        }

                        // --- 30 copy operations ---
                         mapper.copy(source, AggregatedDataResponse.class, copyFields);

                        counter.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();

        long end = System.nanoTime();
        executor.close();
        System.out.println("Total requests processed: " + counter.get());
        System.out.println("Total time (ms): " + ((end - start) / 1_000_000));
        System.out.println("Average time per request (ns): " + ((end - start) / (double) counter.get()));
    }

    @Test
    void loadTestUsingBeanWrapper() throws InterruptedException {
        int users = 1000;
        int requestsPerUser = 2000;
        AtomicInteger counter = new AtomicInteger();

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(users * requestsPerUser);

        DynamicFieldParser parser = new DynamicFieldParser();

        // Prepare fields
        List<String> allFields = new ArrayList<>();
        for (java.lang.reflect.Field f : AggregatedDataResponse.class.getDeclaredFields()) {
                allFields.add(f.getName());
        }
        Collections.shuffle(allFields);

        Set<String> setFields = new HashSet<>(allFields.subList(0, 30));
        Set<String> getFields = new HashSet<>(allFields.subList(30, 50));
        Set<String> cloneFields = new HashSet<>(allFields.subList(50, 80));

        long start = System.nanoTime();
        AggregatedDataResponse source = new AggregatedDataResponse();
        for (int u = 0; u < users; u++) {
            for (int r = 0; r < requestsPerUser; r++) {
                executor.submit(() -> {
                    try {
                        //AggregatedDataResponse source = new AggregatedDataResponse();
                        AggregatedDataResponse target;

                        // --- 30 set operations ---
                        for (String field : setFields) {
                            try {
                                java.lang.reflect.Field f = AggregatedDataResponse.class.getDeclaredField(field);
                                Class<?> type = f.getType();
                                if (type == String.class) parser.setFieldValue(source, field, "test");
                                else if (type == BigDecimal.class) parser.setFieldValue(source, field, BigDecimal.TEN);
                                else if (type == LocalDate.class) parser.setFieldValue(source, field, LocalDate.now());
                                else if (type == LocalDateTime.class) parser.setFieldValue(source, field, LocalDateTime.now());
                            } catch (NoSuchFieldException ignored) {}
                        }

                        // --- 20 get operations ---
                        for (String field : getFields) {
                            parser.getFieldValue(source, field);
                        }

                        // --- 30 clone operations ---
                        for (String field : cloneFields) {
                            target = CloneUtils.cloneWithFields(source, AggregatedDataResponse.class, Collections.singleton(field));
                        }

                        counter.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }
        }

        latch.await();
        long end = System.nanoTime();
        executor.close();

        System.out.println("Total requests processed: " + counter.get());
        System.out.println("Total time (ms): " + ((end - start) / 1_000_000));
        System.out.println("Average time per request (ns): " + ((end - start) / (double) counter.get()));
    }
}
