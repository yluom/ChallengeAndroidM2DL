package com.m2dl.challenge.challengeandroid;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class DessinPlateauActivity extends Activity implements View.OnTouchListener , SurfaceHolder.Callback {

    private PlateauModel plateauModel;
    private SurfaceView svPlateau;

    private GameLoop gameLoop;
    private SurfaceHolder holder;
    private Paint backgroundPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dessin_plateau);


        this.plateauModel = new PlateauModel();

        svPlateau = (SurfaceView) findViewById(R.id.dessinPlateau);

        svPlateau.setOnTouchListener(this);

        holder = svPlateau.getHolder();
        svPlateau.getHolder().addCallback(this);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.plateauModel.setSurface((int) event.getX(), (int) event.getY(), EnumSurfaceType.MUR);
        Log.e("TOuch ! ", " x = + " + (int) event.getX() + " y = " + (int) event.getY());
        return false;
    }


    private void draw() {
        Canvas c = null;
        try {
            c = holder.lockCanvas();
            if (c != null) {
                doDraw(c);
            }
        } finally {
            if (c != null) {
                holder.unlockCanvasAndPost(c);
            }
        }
    }

    private void doDraw(Canvas c) {
        int width = c.getWidth();
        int height = c.getHeight();
        c.drawRect(0, 0, width, height, backgroundPaint);

        Paint p = new Paint();
        p.setColor(Color.BLACK);

        for (int i = 0; i < 1920; i++){
            for(int j = 0; j < 1054; j++){
                if(this.plateauModel.isAMur(i, j)){
                    Log.e("Mur", "Mur rencontré à  " + i + " " + j);
                    c.drawRect(new Rect(200,200,400,400), p);
                }
            }
        }
    }

    private class GameLoop extends Thread {
        private volatile boolean running = true;

        public void run() {
            while (running) {
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                    draw();
                } catch (InterruptedException ie) {
                    running = false;
                }
            }
        }

        public void safeStop() {
            running = false;
            interrupt();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameLoop = new GameLoop();
        gameLoop.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            gameLoop.safeStop();
        } finally {
            gameLoop = null;
        }
    }
}
