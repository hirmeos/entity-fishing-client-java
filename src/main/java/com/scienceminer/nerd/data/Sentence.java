package com.scienceminer.nerd.data;

import java.io.Serializable;

/**
 * This class represents a sentence with stand-off position to mark its boundaries in a text.
 */
public class Sentence implements Serializable {

    private int offsetStart;
    private int offsetEnd;

    public Sentence(int start, int end) {
        this.offsetStart = start;
        this.offsetEnd = end;
    }

    public int getOffsetStart() {
        return offsetStart;
    }

    public int getOffsetEnd() {
        return offsetEnd;
    }

    public void setOffsetStart(int start) {
        offsetStart = start;
    }

    public void setOffsetEnd(int end) {
        offsetEnd = end;
    }


}