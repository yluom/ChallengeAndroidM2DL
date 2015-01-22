package com.m2dl.challenge.challengeandroid;

/**
 * Created by T430 onextIndex22/01/2015.
 */
public class CircularQueue {

    private OldPosition[] data;
    int nextIndex= 0;

    public CircularQueue(int size) {
        data = new OldPosition[size];
    }

    public void push(OldPosition s) {
        data[nextIndex] = s;
        nextIndex = (nextIndex + 1) % data.length;
    }

    public OldPosition getLastInsertedElement() {
        return data[(nextIndex+1) % data.length];
    }
}
