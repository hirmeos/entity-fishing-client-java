package com.scienceminer.nerd.main;

/**
 * Class containing args of the command line {@link Main}.
 *
 * @author Patrice
 */
public class MainArgs {

    private String input;
    private String output;
    private int nbConcurrency = 4; // default

    // from property file
    private String host;
    private int sleepTime;

    public final String getInput() {
        return this.input;
    }

    public final void setInput(final String pPathInputDirectory) {
        this.input = pPathInputDirectory;
    }

    public final String getOutput() {
        return this.output;
    }

    public final void setOutput(final String pPathOutputDirectory) {
        this.output = pPathOutputDirectory;
    }
    
    public int getNbConcurrency() {
        return this.nbConcurrency;
    }

    public void setNbConcurrency(int nb) {
        this.nbConcurrency = nb;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getSleepTime() {
        return this.sleepTime;
    }

    public void setSleepTime(int nb) {
        this.sleepTime = nb;
    }
}
