package com.RobD.Moba;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.RobD.Moba.Utils.Constants;
import com.RobD.Moba.Utils.Zoom;
import com.RobD.Units.Castle;
import com.RobD.Units.Mob;
import com.RobD.Units.Tower;
import com.RobD.Units.Unit;
import com.RobD.Units.Heros.Hero;
import com.RobD.Units.Heros.Hero_Ganon;
import com.RobD.Units.Heros.Hero_Link;
import com.RobD.robsapp.MainActivity;
import com.RobD.robsapp.R;

public class MobaGamePanel extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final int mobWaveTime = 25000;
	//private static final String TAG = FishGamePanel.class.getSimpleName();
	private static final float GUIRatio = 0.1f;
	private static final int maxMobsOnScreen = 150;
	
	private MobaThread thread;
	private float clickX, clickY;
	private Unit player;
	private Zoom zoom;
	private ArrayList<Unit> mobs;
	private ArrayList<Unit> towers;
	private ArrayList<Unit> deadMobs;
	private ArrayList<Unit> heroes;
	private Castle team1Castle;
	private Castle team2Castle;
	private Date mobsSent;
	private float canvasWidth;
	private float canvasHeight;
	
	private MainActivity mainActivity;

	public MobaGamePanel(Context context, AttributeSet attrs){
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		
		mainActivity = (MainActivity) context;
		
		// create the game loop thread
		thread = new MobaThread(getHolder(), this);

		mobsSent = new Date();
		mobs = new ArrayList<Unit>();
		deadMobs = new ArrayList<Unit>();
		heroes = new ArrayList<Unit>();
		zoom = new Zoom();	
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	/**
	 * create 3 towers per lane per team
	 */
	private void setTowers() {
		for(int t=1; t<=2; t++){
			for(int l=1;l<=3; l++){
				for(int i=0;i<3;i++){
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.tower_red);
					if(t == 1){
						bmp = BitmapFactory.decodeResource(getResources(), R.drawable.tower_blue);
					}
					Unit tower = new Tower(bmp, l, i, t, canvasWidth, canvasHeight);
					towers.add(tower);
				}
			}
		}
	}

	/**
	 * Spawn a wave of mobs
	 * 3 groups of 5 per team
	 */
	private void summonMobs() {
		Date curTime = new Date();
		if(curTime.getTime() - mobsSent.getTime() > mobWaveTime){
			// create new mobs
			mobsSent = new Date();

			for(int t=1;t<=2;t++){
				for(int l=1;l<=3;l++){
					for(int m=0; m<5; m++){
						Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mob_red);
						if(t == 1){
							bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mob_blue);
						}
						
						Unit mob = new Mob(bmp, l,m,t, canvasWidth, canvasHeight, towers);
						mobs.add(mob);
					}
				}
			}
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		thread.setRunning(false);
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (Exception e) {
				// try again shutting down the thread
			}
		}
	}
	
	/**
	 * the on touch event for the game
	 * checks if the player is clicking on a unit
	 * 
	 * @param  event  the touch event
	 * @return a boolean depicting whether or not the event was handled
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			clickX = event.getX();
			clickY = event.getY();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

		} else if (event.getAction() == MotionEvent.ACTION_UP){
			// check that it was a touch and not a drag
			if(event.getX() < clickX +5 && event.getX() > clickX -5){
				if(event.getY() < clickY +5 && event.getY() > clickY -5){
					// touch event occurred within game
					if(event.getX() > getWidth() * GUIRatio){			
						if(player.isAlive()){				
							float adjustedX = event.getX();
							float adjustedY = event.getY();

							
							// the movement is different if zoomed because we are just using screen coordinates
							if(zoom.getTargetZoom() != Zoom.ZOOM_FAR){
								adjustedX = player.getX() - ((thread.getCanvasWidth() / 2) - event.getX() + thread.getCanvasWidth() * GUIRatio) / zoom.getTargetZoom();
								adjustedY = player.getY() - ((thread.getCanvasHeight() / 2) - event.getY()) / zoom.getTargetZoom();
							}

							// make sure the player isn't trying to move out of the screen
							if(adjustedX > thread.getCanvasWidth() * GUIRatio && adjustedX < thread.getCanvasWidth() && adjustedY > 0 && adjustedY < thread.getCanvasHeight()){
								// clear the targeted mob so if clicked moving can take priority
								player.setTargetUnit(null);

								// remove mobs targetByPlayer so the play can target a new mob
								removePlayerTargeting();

								player.setTarget(adjustedX, adjustedY);
								//player.setX(adjustedX);
								//player.setY(adjustedY);

								// so no need to check if priority target clicked
								boolean priorityFound = false;
								
								// check heroes
								for(Unit h : heroes){
									if(h.isAlive()){
										h.setTargetedByPlayer(false);
										if(h.getTeam() != player.getTeam()){
											if(h.handleActionDown(adjustedX, adjustedY, player)){
												priorityFound = true;
												break;
											}
										}
									}
								}
								
								// check towers
								for(Unit t : towers){
									if(t.isAlive()){
										t.setTargetedByPlayer(false);
										if(t.getTeam() != player.getTeam()){
											if(t.handleActionDown(adjustedX, adjustedY, player)){
												priorityFound = true;
												break;
											}
										}
									}
								}

								// check mobs	
								if(!priorityFound){
									for(Unit mob : mobs){
										if(mob.isAlive()){
											mob.setTargetedByPlayer(false);

											if(mob.getTeam() != player.getTeam()){
												if(mob.handleActionDown(adjustedX, adjustedY, player)){
													priorityFound = true;
													break;
												}
											}
										}
									}
								}
								
								//check if clicking the enemy castle
								if(!priorityFound){
									team2Castle.handleActionDown(adjustedX, adjustedY, player);
								}
							}
						}
					}else{
						// touch event occurred within GUI
						float buttonHeight = getHeight()/6;
						if(event.getY() < buttonHeight){
							zoom.setTargetZoom(Zoom.ZOOM_FAR);
						}else if(event.getY() > buttonHeight && event.getY() < buttonHeight * 2){
							zoom.setTargetZoom(Zoom.ZOOM_MID);
						}else if(event.getY() > buttonHeight * 2 && event.getY() < buttonHeight * 3){
							zoom.setTargetZoom(Zoom.ZOOM_NEAR);
						}
					}
				}
			}

			clickX = 0f;
			clickY = 0f;
		}
		
		return true;
	}
	
	/**
	 * the update method for all game elements
	 * 
	 * @param  canvas  the canvas to update onto, used for dimensions
	 */
	public void update(Canvas canvas) {	
		// update the background image
		if(player != null){
			mainActivity.setBackground(mainActivity, zoom.targetZoom, player.getX(), player.getY());
		}		
		
		canvasWidth = thread.getCanvasWidth();
		canvasHeight = thread.getCanvasHeight();
		
		if(towers == null){
			// first run, create the things
			towers = new ArrayList<Unit>();
			setTowers();
			//player = new Hero(1, BitmapFactory.decodeResource(getResources(), R.drawable.link_sprite), canvasWidth, canvasHeight, 1);
			player = new Hero(1, canvasWidth, canvasHeight, new Hero_Link(), this.getContext(), true, -1);
			team1Castle = new Castle(BitmapFactory.decodeResource(getResources(), R.drawable.blue_castle), 1, canvasWidth, canvasHeight);
			team2Castle = new Castle(BitmapFactory.decodeResource(getResources(), R.drawable.red_castle), 2, canvasWidth, canvasHeight);
			
			Unit hero_ganon = new Hero(2, canvasWidth, canvasHeight, new Hero_Ganon(), this.getContext(), false, 1);
			hero_ganon.setTarget(canvasWidth * Constants.L1T2SpawnX,  canvasHeight * Constants.L1T2SpawnY);
			heroes.add(hero_ganon);	
			
			Unit hero_ganon2 = new Hero(2, canvasWidth, canvasHeight, new Hero_Ganon(), this.getContext(), false, 2);
			hero_ganon2.setTarget(canvasWidth * Constants.L2T2SpawnX,  canvasHeight * Constants.L2T2SpawnY);
			heroes.add(hero_ganon2);	
			
			Unit hero_ganon3 = new Hero(2, canvasWidth, canvasHeight, new Hero_Ganon(), this.getContext(), false, 3);
			hero_ganon3.setTarget(canvasWidth * Constants.L3T2SpawnX,  canvasHeight * Constants.L3T2SpawnY);
			heroes.add(hero_ganon3);	
		}
		
		cleanUpDeadMobs();
		
		if(mobs.size() < maxMobsOnScreen){
			summonMobs();
		}

		//trackMobs();
		
		for(Unit t : towers){
			t.seekTarget(true, mobs, heroes, player, null, null, null);
			t.updateTower();
		}
		
		team1Castle.updateCastle(mobs, heroes, player);	
		team2Castle.updateCastle(mobs, heroes, player);	
		
		for(Unit u : mobs){
			u.seekTarget(false, mobs, heroes, player, towers, team1Castle, team2Castle);
			u.update();			
		}		
		
		for(Unit h: heroes){
			h.seekTarget(false, mobs, heroes, player, towers, team1Castle, team2Castle);
			h.updateHero(mobs);
		}

		//player.seekTarget(false, mobs, heroes, player, towers, team1Castle, team2Castle);
		player.update();
	}
	
	/**
	 * remove dead mobs from their arraylists 
	 */
	private void cleanUpDeadMobs() {
		for(Unit m : deadMobs){
			mobs.remove(m);
		}
		
		deadMobs.clear();
	}
	
	/**
	 * the method to draw everything onto the canvas
	 * 
	 * @param  canvas  the canvas to update onto
	 */
	protected void render(Canvas canvas) {
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		
		canvas.scale(zoom.getTargetZoom(), zoom.getTargetZoom(), canvas.getWidth()/2, canvas.getHeight()/2);
		zoom.setCurrentZoom(zoom.getTargetZoom());		
		
		if(zoom.getCurrentZoom() == Zoom.ZOOM_MID || zoom.getCurrentZoom() == Zoom.ZOOM_NEAR){
			float guiOffset = (getWidth() * GUIRatio) / zoom.getTargetZoom();
			canvas.translate(canvas.getWidth() / 2 - player.getX() + guiOffset, canvas.getHeight() / 2 - player.getY());
			
		}
		
		for(Unit t : towers){
			t.render(canvas);
		}

		team1Castle.render(canvas);	
		team2Castle.render(canvas);
		
		for(Unit u : mobs){
			u.render(canvas);
		}
		
		for(Unit h: heroes){
			h.render(canvas);
		}
		
		player.render(canvas);
		drawGUI(canvas);
				
		if(!team1Castle.isAlive()){
			// team 2 wins!
			Paint paint = new Paint();
			paint.setColor(Color.argb(255, 204, 15, 0));
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(canvasHeight / 10);
			canvas.drawText("Red team wins!", canvasWidth / 2 + canvasWidth / 18, canvasHeight / 2, paint);
			thread.setRunning(false);
		}else if(!team2Castle.isAlive()){
			// team 1 wins!
			Paint paint = new Paint();
			paint.setColor(Color.argb(255, 0, 15, 204));
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(canvasHeight / 10);
			canvas.drawText("Blue team wins!", canvasWidth / 2 + canvasWidth / 18, canvasHeight / 2, paint);
			thread.setRunning(false);
		}
	}
	
	/**
	 * set no units to be targeted by the player
	 */
	private void removePlayerTargeting(){
		for(Unit mob : mobs){
			mob.setTargetedByPlayer(false);
		}
		
		for(Unit tower : towers){
			tower.setTargetedByPlayer(false);
		}
		
		for(Unit hero : heroes){
			hero.setTargetedByPlayer(false);
		}
		
		team2Castle.setTargetedByPlayer(false);
	}
	
	/**
	 * the method to draw the GUI on the left of the canvas
	 * 
	 * @param  canvas  the canvas to update onto, used for dimensions
	 */
	private void drawGUI(Canvas canvas) {		
		float zoomButHeight = canvas.getHeight()/6;
		float zoomButWidth = (int)(getWidth() * GUIRatio);
		float GUIPosX = 0;
		float GUIPosY = 0;
		Paint paintBlack = new Paint();
		paintBlack.setColor(Color.BLACK);
		Paint paintZoom = new Paint();
		paintZoom.setColor(Color.BLUE);
		Paint paintZoomLine = new Paint();
		paintZoomLine.setStrokeWidth(2);
		paintZoomLine.setStyle(Paint.Style.STROKE);
		paintZoomLine.setColor(Color.WHITE);
				
		if(zoom.getTargetZoom() != Zoom.ZOOM_FAR){
			// calculate how to display the GUI if zoomed in
			paintZoomLine.setStrokeWidth(2 / zoom.getTargetZoom());
			zoomButHeight /= zoom.getTargetZoom();
			zoomButWidth /= zoom.getTargetZoom();
			GUIPosX = (player.getX() - getWidth() / zoom.getTargetZoom() * GUIRatio - getWidth() / zoom.getTargetZoom() / 2); 
			GUIPosY = (player.getY() - (getHeight()/ 2) / zoom.getTargetZoom());
		}		

		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		float textSize = zoomButHeight / 4f;
		textPaint.setTextSize(textSize);
		textPaint.setTextAlign(Align.CENTER);
		float textXPos = (float)(GUIPosX + zoomButWidth / 2f);
		
		// draw GUI background
		canvas.drawRect(GUIPosX, GUIPosY, GUIPosX + zoomButWidth, GUIPosY + zoomButHeight*6, paintBlack);
		
		// draw zoom buttons
		canvas.drawRect(GUIPosX, GUIPosY, GUIPosX + zoomButWidth, GUIPosY + zoomButHeight, paintZoom);
		canvas.drawRect(GUIPosX, GUIPosY, GUIPosX + zoomButWidth, GUIPosY + zoomButHeight, paintZoomLine);
		
		canvas.drawRect(GUIPosX, GUIPosY + zoomButHeight, GUIPosX + zoomButWidth, GUIPosY + zoomButHeight*2, paintZoom);
		canvas.drawRect(GUIPosX, GUIPosY + zoomButHeight, GUIPosX + zoomButWidth, GUIPosY + zoomButHeight*2, paintZoomLine);
		
		canvas.drawRect(GUIPosX, GUIPosY + zoomButHeight*2, GUIPosX + zoomButWidth, GUIPosY + zoomButHeight*3, paintZoom);
		canvas.drawRect(GUIPosX, GUIPosY + zoomButHeight*2, GUIPosX + zoomButWidth, GUIPosY + zoomButHeight*3, paintZoomLine);
		
		canvas.drawText("1x", GUIPosX + zoomButWidth/2, GUIPosY + (int)(zoomButHeight*0.5), textPaint);
		canvas.drawText("5x", GUIPosX + zoomButWidth/2, GUIPosY + (int)(zoomButHeight*1.5), textPaint);
		canvas.drawText("10x", GUIPosX + zoomButWidth/2, GUIPosY + (int)(zoomButHeight*2.5), textPaint);
			
		// draw hero
		float heroIconHeight = (GUIPosY + zoomButHeight*3) + zoomButWidth * 0.74f;
		RectF playerRect = new RectF(GUIPosX, GUIPosY + zoomButHeight*3, GUIPosX + zoomButWidth, heroIconHeight);
		canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.link_icon), null, playerRect, null);
		
		textSize = zoomButHeight / 7f;
		textPaint.setTextSize(textSize);
		
		// draw the players health bar
		float healthP = player.getHealthPercent();
		float barHeight = zoomButHeight*0.2f;
		Paint healthPaint = new Paint();
		healthPaint.setColor(Constants.healthColour1);
		
		if(healthP > 0.8f){
			healthPaint.setColor(Constants.healthColour5);
		}else if(healthP > 0.6f){
			healthPaint.setColor(Constants.healthColour4);				
		}else if(healthP > 0.3f){
			healthPaint.setColor(Constants.healthColour3);				
		}else if(healthP > 0.1f){
			healthPaint.setColor(Constants.healthColour2);				
		}
		
		canvas.drawRect(GUIPosX, heroIconHeight, GUIPosX + (zoomButWidth * healthP), heroIconHeight + barHeight, healthPaint);
		canvas.drawRect(GUIPosX, heroIconHeight, GUIPosX + zoomButWidth, heroIconHeight + barHeight, paintZoomLine);
		canvas.drawText(player.getHealth() + "/" + player.getmaxHealth(), textXPos, (float)(heroIconHeight + zoomButHeight*0.15), textPaint);
		
		// draw EXP bar
		barHeight = zoomButHeight*4.2f;
		float expP = player.getEXPPercent();
		canvas.drawRect(GUIPosX, GUIPosY + zoomButHeight*4, GUIPosX + (zoomButWidth * expP), (float)(GUIPosY + barHeight), paintZoom);
		canvas.drawRect(GUIPosX, GUIPosY + zoomButHeight*4, GUIPosX + zoomButWidth, (float)(GUIPosY + barHeight), paintZoomLine);
		canvas.drawText("Level: " + player.getLevel(), textXPos, (float)(GUIPosY + zoomButHeight*4.15), textPaint);
		
		//draw info text
		canvas.drawText("G: " + player.getGold(), textXPos, (float)(GUIPosY + barHeight + (2 * textSize)), textPaint);
		
		// if player is dead show respawn info
		if(!player.isAlive()){
			long curTime = new Date().getTime();
			long timeToRes = Unit.respawnTime - (curTime - player.getTimeOfDeath().getTime());
			canvas.drawText("Respawn in: " + timeToRes / 1000, textXPos, (float)(GUIPosY + barHeight + (4 * textSize)), textPaint);
		}
	}
	
}
