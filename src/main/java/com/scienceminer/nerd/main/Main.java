package com.scienceminer.nerd.main;

import com.scienceminer.nerd.client.NerdBatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * The entrance point for starting the client from command line
 *
 * @author Patrice Lopez
 */
public class Main {
    private static MainArgs gbdArgs;

    protected static String getHelp() {
        final StringBuilder help = new StringBuilder();
        help.append("HELP Java entity-fishing client\n");
        help.append("-h: displays this help\n");
        help.append("-in: directory path containing the PDF files to process.\n");
        help.append("-out: directory path to write the result files\n");
        help.append("-n: number of threads\n");
        return help.toString();
    }

    protected static boolean processArgs(final String[] pArgs) {
        boolean result = true;

        // read the properties and put the values in the args object
        Properties props = new Properties();
        String nerdHost = null;
        int sleepTime = 5000;
        try (InputStream resourceStream = new FileInputStream("nerd-client.properties")) {
            props.load(resourceStream);
            nerdHost = props.getProperty("nerdHost", "localhost:8090");
            String sleepTimeStr = props.getProperty("sleepTime", "5000");
            try {
                sleepTime = Integer.parseInt(sleepTimeStr);
            } catch (Exception e) {
                System.err.println("sleep time value should be an integer, default value will be used");
            }
        } catch (Exception e) {
            System.err.println("property file not found, default values will be used");
        }

        gbdArgs.setHost(nerdHost);
        gbdArgs.setSleepTime(sleepTime);
        
        if (pArgs.length == 0) {
            System.out.println(getHelp());
            result = false;
        } else {
            String currArg;
            for (int i = 0; i < pArgs.length; i++) {
                currArg = pArgs[i];
                if (currArg.equals("-h")) {
                    System.out.println(getHelp());
                    result = false;
                    break;
                }
                if (currArg.equals("-in")) {
                    if (pArgs[i + 1] != null) {
                        gbdArgs.setInput(pArgs[i + 1]);
                    }
                    i++;
                    continue;
                }
                if (currArg.equals("-out")) {
                    if (pArgs[i + 1] != null) {
                        gbdArgs.setOutput(pArgs[i + 1]);
                    }
                    i++;
                    continue;
                }
                if (currArg.equals("-n")) {
                    if (pArgs[i + 1] != null) {
                        String nb = pArgs[i + 1];
                        int nbConcurrency = 1;
                        try {
                            nbConcurrency = Integer.parseInt(nb);
                            gbdArgs.setNbConcurrency(nbConcurrency);
                        } catch (Exception e) {
                            System.err.println("-n value should be an integer, default value will be used");
                        }
                    }
                    i++;
                    continue;
                }
            }
        }
        return result;
    }

    /**
     * Starts nerd from command line using the following parameters:
     *
     * @param args The arguments
     */
    public static void main(final String[] args) throws Exception {
        gbdArgs = new MainArgs();
        if (processArgs(args)) {

            File dirOutputPath = new File(gbdArgs.getOutput());
            if (!dirOutputPath.exists()) {
                System.out.println("Cannot find the destination directory " + dirOutputPath.getAbsolutePath() + ". Creating it.");
                dirOutputPath.mkdir();
            }

            File dirInputPath = new File(gbdArgs.getInput());
            if (!dirInputPath.exists()) {
                System.err.println("Cannot find the input directory: " + dirInputPath.getAbsolutePath());
            }

            long startTime = System.nanoTime();
            NerdBatch process = new NerdBatch(gbdArgs.getHost(), gbdArgs.getSleepTime());
            process.process(dirInputPath.getAbsolutePath(), dirOutputPath.getAbsolutePath(), gbdArgs.getNbConcurrency());
            long endTime = System.nanoTime();
            System.out.println("\ntotal runtime:" + (endTime - startTime) / 1000000 + " ms");
        }
    }

}
