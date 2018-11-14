package com.scienceminer.nerd.client;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Patrice
 */
public class NerdProcess {

    private static final Logger logger = LoggerFactory.getLogger(NerdProcess.class);

    private String host;
    private int sleepTime;
    public NerdProcess(String host, int sleepTime) {
        this.host = host;
        this.sleepTime = sleepTime;
    }

    public void process(String input, String output, int nbThreads) {

        ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
        File dirInputPath = new File(input);

        final File[] refFiles = dirInputPath.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".pdf");
            }
        });

        if (refFiles == null) {
            throw new IllegalStateException("Folder " + dirInputPath.getAbsolutePath()
                    + " does not seem to contain any PDF file");
        }

        System.out.println(refFiles.length + " PDF files");
        int n = 0;
        for (; n < refFiles.length; n++) {
            final File pdfFile = refFiles[n];
            Runnable worker = new NerdWorker(this.host, pdfFile, output + "/" + FilenameUtils.getBaseName(pdfFile.getPath()) + ".json");
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

    }
}
