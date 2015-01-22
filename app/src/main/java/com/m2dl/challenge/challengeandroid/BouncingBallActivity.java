package com.m2dl.challenge.challengeandroid;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.SENSOR_ACCELEROMETER;
import static android.hardware.SensorManager.SENSOR_DELAY_GAME;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

/**
 * This activity shows a ball that bounces around. The phone's 
 * accelerometer acts as gravity on the ball. When the ball hits
 * the edge, it bounces back and triggers the phone vibrator.
 */
public class BouncingBallActivity extends Activity implements View.OnTouchListener, Callback, SensorListener {
	private static final int BALL_RADIUS = 30;
	private SurfaceView surface;
	private SurfaceHolder holder;
	private BouncingBallModel model;
	private GameLoop gameLoop;
	private Paint backgroundPaint;
	private Paint ballPaint;
    private Paint wallPaint;
	private SensorManager sensorMgr;
	private long lastSensorUpdate = -1;

    private PlateauModel plateauModel;
    private EnumSurfaceType etat;
    private ToggleButton outil;
    //private CircularQueue oldPositions;
    private ImageButton outil;
    private ImageButton btnSurfaceType;
    private float oldX, oldY;
    private int cptOldposition = 0;
    private Bitmap imgWall;


	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.bouncing_ball);
    	
    	surface = (SurfaceView) findViewById(R.id.bouncing_ball_surface);
    	holder = surface.getHolder();
    	surface.getHolder().addCallback(this);


        LinearLayout ll = (LinearLayout) findViewById(R.id.bouncing_ball);
        ll.setOnTouchListener(this);

        this.btnSurfaceType = (ImageButton) findViewById(R.id.btnSurfaceType);


    	
    	backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);

		ballPaint = new Paint();
		ballPaint.setColor(Color.BLUE);
		ballPaint.setAntiAlias(true);

        wallPaint = new Paint();
        wallPaint.setColor(Color.RED);


        this.plateauModel = new PlateauModel();
        this.etat = EnumSurfaceType.MUR;

        this.outil = (ImageButton) findViewById(R.id.btnSurfaceType);

        this.model  = new BouncingBallModel(BALL_RADIUS,this.plateauModel);

       // this.oldPositions = new CircularQueue(200);
        imgWall = BitmapFactory.decodeResource(getResources(), R.drawable.brique);
        imgWall = Bitmap.createScaledBitmap(imgWall, 50, 50, false);
    }
    
	@Override
	protected void onPause() {
		super.onPause();
		
		model.setVibrator(null);
		
		sensorMgr.unregisterListener(this, SENSOR_ACCELEROMETER);
		sensorMgr = null;
		
		model.setAccel(0, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean accelSupported = sensorMgr.registerListener(this, 
				SENSOR_ACCELEROMETER,
				SENSOR_DELAY_GAME);
		
		if (!accelSupported) {
			// on accelerometer on this device
			sensorMgr.unregisterListener(this, SENSOR_ACCELEROMETER);
			// TODO show an error
		}
		
		// NOTE 1: you cannot get system services before onCreate()
		// NOTE 2: AndroidManifest.xml must contain this line:
		// <uses-permission android:name="android.permission.VIBRATE"/>
		Vibrator vibrator = (Vibrator) getSystemService(Activity.VIBRATOR_SERVICE);
		model.setVibrator(vibrator);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		model.setSize(width, height);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		gameLoop = new GameLoop();
		gameLoop.start();
	}
	
	private void draw() {
		// TODO thread safety - the SurfaceView could go away while we are drawing
		
		Canvas c = null;
		try {
			// NOTE: in the LunarLander they don't have any synchronization here,
			// so I guess this is OK. It will return null if the holder is not ready
			c = holder.lockCanvas();
			
			// TODO this needs to synchronize on something
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
		
		float ballX, ballY;
		synchronized (model.LOCK) {
			ballX = model.ballPixelX;
			ballY = model.ballPixelY;
		}

		c.drawCircle(ballX, ballY, BALL_RADIUS, ballPaint);


        for (int i = 0; i < 38; i++){
            for(int j = 0; j < 21; j++){
                if(this.plateauModel.isAMur(i, j)){
                    int pixelX = i*50;
                    int pixelY = j*50;
                    //c.drawRect(pixelX,pixelY,pixelX+50,pixelY+50, wallPaint);
                    c.drawBitmap(imgWall,pixelX,pixelY,wallPaint);
                }
            }
        }
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			model.setSize(0,0);
			gameLoop.safeStop();
		} finally {
			gameLoop = null;
		}
	}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.plateauModel.setSurface((int) event.getX(), (int) event.getY(), this.etat);
        return true;
    }

    public void btnSurfaceChange(View v) {
        if(EnumSurfaceType.MUR == this.etat){
            this.etat = EnumSurfaceType.VIDE;
            btnSurfaceType.setImageDrawable(getResources().getDrawable(R.drawable.pen));
        } else if(EnumSurfaceType.VIDE == this.etat){
            this.etat = EnumSurfaceType.MUR;
            btnSurfaceType.setImageDrawable(getResources().getDrawable(R.drawable.delete));
        }
    }

    private class GameLoop extends Thread {
		private volatile boolean running = true;
		
		public void run() {
			while (running) {
				try {
					// TODO don't like this hardcoding
					TimeUnit.MILLISECONDS.sleep(5);
                   // oldPositions.push(new OldPosition(model.ballPixelX, model.ballPixelY, model.velocityX, model.velocityY));

					draw();
					model.updatePhysics();

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

	public void onAccuracyChanged(int sensor, int accuracy) {		
	}

	public void onSensorChanged(int sensor, float[] values) {
		if (sensor == SENSOR_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			// only allow one update every 50ms, otherwise updates
			// come way too fast
			if (lastSensorUpdate == -1 || (curTime - lastSensorUpdate) > 50) {
				lastSensorUpdate = curTime;
				
				model.setAccel(values[DATA_X], values[DATA_Y]);
			}
		}
	}


    public void cleanPlateau(View v){
        this.plateauModel.cleanPlateau();
    }

    public void replayBall(View v) {

        // get 1s ago position
       // OldPosition pos = this.oldPositions.getLastInsertedElement();
        //model.setBallPositionAndVelocity((int) pos.getX(), (int) pos.getY(), (int) pos.getvX(), (int) pos.getvY());

    }
}
