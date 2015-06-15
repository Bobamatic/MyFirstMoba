package com.RobD.Moba;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MobaThread extends Thread{
	private static final String TAG = MobaThread.class.getSimpleName();
	private SurfaceHolder surfaceHolder;
	private MobaGamePanel gamePanel;
	private boolean running;
	public float canvasWidth, canvasHeight;

	private final static int MAX_FPS = 30;
	private final static int MAX_FRAME_SKIPS = 5;
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;

	public MobaThread(SurfaceHolder surfaceHolder, MobaGamePanel gamePanel) {
		super();
				
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		Canvas canvas;
		Log.d(TAG, "Starting game loop");

		long beginTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped 

		sleepTime = 0;

		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing on the surface
			try {				
				canvas = this.surfaceHolder.lockCanvas();
				canvasWidth = canvas.getWidth();
				canvasHeight = canvas.getHeight();
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;
					// update game state
					this.gamePanel.update(canvas);
					// render state to the screen
					this.gamePanel.render(canvas);
					// how long the cycle took
					timeDiff = System.currentTimeMillis() - beginTime;
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					if (sleepTime > 0) {
						try {
							// games running well so sleep a little if done processing before next frame
							// this can save batter because of lack of processing
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {}
					}

					// games lagging update without rendering to catch up
					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						this.gamePanel.update(canvas);
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
					
					if (framesSkipped > 0) {
						Log.d(TAG, "Skipped:" + framesSkipped);
					}
				}
			} finally {
				// in case of an exception the surface is not left in an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}

		}
	}
	
	public float getCanvasWidth(){		
		return canvasWidth;
	}
	
	public float getCanvasHeight(){
		return canvasHeight;
	}
}
