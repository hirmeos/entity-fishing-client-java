package com.scienceminer.nerd.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scienceminer.nerd.exception.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Patrice
 */
public class NerdWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(NerdWorker.class);

    private File pdfFile;
    private NerdClient nerdClient;
    private String output;

    public NerdWorker(String host, File pdfFile, String output) {
        this.pdfFile = pdfFile;
        nerdClient = new NerdClient(host);
        this.output = output;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        logger.info(Thread.currentThread().getName() + " Start. Processing = " + pdfFile.getPath());
        try {
            final ObjectNode jsonNodes = nerdClient.disambiguatePDF(pdfFile, null);
            new FileWriter(this.output).append(jsonNodes.toString());
            logger.info("\t\t " + pdfFile.getPath() + " processed.");
        } catch (ClientException e) {
            logger.warn("Processing of " + pdfFile.getPath() + " ended up in error. ", e);
        } catch (RuntimeException e) {
            logger.error("\t\t error occurred while processing " + pdfFile.getPath(), e);
            logger.error(e.getMessage(), e.getCause());
        } catch (IOException e) {
            logger.error("\t\t error when writing output file " + output, e);
        }
        long endTime = System.nanoTime();
        logger.info(Thread.currentThread().getName() + " End. :" + (endTime - startTime) / 1000000 + " ms");
    }

    public String getFilename() {
        return pdfFile.getAbsolutePath();
    }
}
