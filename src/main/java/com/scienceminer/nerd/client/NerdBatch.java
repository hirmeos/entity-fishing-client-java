package com.scienceminer.nerd.client;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.*;

/**
 * @author Patrice
 */
public class NerdBatch {

    private static final Logger logger = LoggerFactory.getLogger(NerdBatch.class);

    private String host;
    private int sleepTime;
    public NerdBatch(String host, int sleepTime) {
        this.host = host;
        this.sleepTime = sleepTime;
    }

    public void process(String input, String output, int nbThreads) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(nbThreads*10);
        
        File dirInputPath = new File(input);

        final File[] refFiles = dirInputPath.listFiles((dir, name) -> name.endsWith(".pdf"));

        if (refFiles == null) {
            throw new IllegalStateException("Folder " + dirInputPath.getAbsolutePath()
                    + " does not seem to contain any PDF file");
        }

        System.out.println(refFiles.length + " PDF files");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(nbThreads, nbThreads, 60000,
                TimeUnit.MILLISECONDS, blockingQueue);

        // this is for handling rejected tasks (e.g. queue is full)
        executor.setRejectedExecutionHandler((r, executor1) -> {
            logger.warn("Task Rejected  " + ((NerdWorker) r).getFilename());
            logger.info("Waiting for 60 second!!");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Lets add it to the queue another time: " + ((NerdWorker) r).getFilename());
            executor1.execute(r);
        });
        executor.prestartAllCoreThreads();
        Runnable worker = null;
        int n = 0;
        for (; n < refFiles.length; n++) {
            final File pdfFile = refFiles[n];

            worker = new NerdWorker(this.host, pdfFile, output + "/" + FilenameUtils.getBaseName(pdfFile.getPath()) + ".json");
            executor.execute(worker);
        }

        try {
            System.out.println("wait for thread completion");
            executor.shutdown();
            //executor.awaitTermination(48, TimeUnit.HOURS);
            while (!executor.isTerminated()) {
            }
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel all non-finished workers");
            }
            executor.shutdownNow();
        }

        logger.info("Finished all threads");
        logger.info("Total: " + n + " documents annotated.");
    }
}
