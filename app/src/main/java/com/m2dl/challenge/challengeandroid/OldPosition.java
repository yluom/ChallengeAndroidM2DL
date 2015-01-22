package com.m2dl.challenge.challengeandroid;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by T430 on 22/01/2015.
 */
public class OldPosition {

    private float x;
    private float y;
    private float vX;
    private float vY;
    private Timestamp time;



    public OldPosition(float x, float y, float vX, float vY) {
        this.x = x;
        this.y = y;
        this.vX = vX;
        this.vY = vY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getvX() {
        return vX;
    }

    public float getvY() {
        return vY;
    }

    @Override
    public String toString() {
        return "OldPosition{" +
                "x=" + x +
                ", y=" + y +
                ", vX=" + vX +
                ", vY=" + vY +
                '}';
    }
}
