package com.RobD.Units;

import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.RobD.Moba.Utils.Constants;
import com.RobD.Moba.Utils.Speed;

public class Mob extends Unit{
	
	private final float[][] mobXPos = {{Constants.L1T1SpawnX,Constants.L2T1SpawnX,Constants.L3T1SpawnX},{Constants.L1T2SpawnX,Constants.L2T2SpawnX,Constants.L3T2SpawnX}};
	private final float[][] mobYPos = {{Constants.L1T1SpawnY,Constants.L2T1SpawnY,Constants.L3T1SpawnY},{Constants.L1T2SpawnY,Constants.L2T2SpawnY,Constants.L3T2SpawnY}};

	private final float[][] targX = {{Constants.L1MidX,Constants.L2T2SpawnX,Constants.L3MidX},{Constants.L1MidX,Constants.L2T1SpawnX,Constants.L3MidX}};
	private final float[][] targY = {{Constants.L1MidY,Constants.L2T2SpawnY,Constants.L3MidY},{Constants.L1MidY,Constants.L2T1SpawnY,Constants.L3MidY}};

	/*private final float[][] targX2 = {{0.729f,0.82f,0.963f},{0.135f,0.261f,0.963f}};
	private final float[][] targY2 = {{0.029f,0.183f,0.25f},{0.029f,0.806f,0.956f}};*/

	public Mob(Bitmap bmp, int laneNum, int posNum, int teamNum, float canvasWidth, float canvasHeight, ArrayList<Unit> towers) {
		id="MOB-" + teamNum + "-" + (++Unit.nextId);
		IDNum = Unit.nextId;
		lane = laneNum;
		pos = posNum;

		unitType = 1;
		velocity = 0.4f;
		alive = true;
		team = teamNum;
		speed = new Speed(0,0);
		ranged = false;
		bitmap = bmp;	
		currentFrame = 0;
		frameNr = 4;
		spriteWidth = bitmap.getWidth() / frameNr;
		spriteHeight = bitmap.getHeight() / 4;
		sourceRect = new Rect(0, 0, (int)spriteWidth, (int)spriteHeight);
		framePeriod = 100;
		frameTicker = 0l;
		lastFrameChanged = new Date();

		//the number of towers in the same lane on the enemy team determines the mob level
		int towerNum = 4;
		for(Unit t : towers){
			if(t.getLane() == lane){
				if(t.getTeam() != team){
					if(t.isAlive()){
						towerNum--;
					}
				}
			}
		}
		
		level = towerNum;
		
		switch(level){
		case 1:
			width = canvasWidth / 120;
			break;
		case 2:
			width = canvasWidth / 117;
			break;
		case 3:
			width = canvasWidth / 115;
			break;
		default:
			width = canvasWidth / 110;
			break;
		}
		height = width;
		
		attackDelay = 1000;
		maxHealth = new int[]{45, 50, 55, 60, 65};
		health = maxHealth[level - 1];
		attackPower = new int[]{12, 14, 16, 18, 20};
		attackRange = width;
		lastAttack = new Date();
		healthPaint = new Paint();
		rangedAttackWidth = width / 4;

		if(posNum == 4){
			ranged = true;
			attackRange = width * 5;
		}

		int color = Color.argb(255, 255, 255, 255);
		if(team == 1){
			color = Color.argb(255, 0, 106, 255);
		}else{
			color = Color.argb(255, 240, 0, 0);
		}
		paint = new Paint();
		paint.setColor(color);
		rangedAttackColour = color;

		float adjustmentX = (float)Math.pow(0.99, posNum);
		float adjustmentY = (float)Math.pow(0.99, posNum);

		switch(teamNum){
		case 1:
			finalTargetX = canvasWidth * Constants.towerXPos[1][2][0];
			finalTargetY = canvasHeight * Constants.towerYPos[1][0][0];
			
			switch(laneNum){
			case 1:
				adjustmentX = 1f;
				break;
			case 2:
				break;
			case 3:
				adjustmentY = 1f;
				break;
			}
			break;
		case 2:
			finalTargetX = canvasWidth * Constants.towerXPos[0][0][0];
			finalTargetY = canvasHeight * Constants.towerYPos[0][2][0];
			
			switch(laneNum){
			case 1:
				adjustmentY = 1f;
				break;
			case 2:
				break;
			case 3:
				adjustmentX = 1f;
				break;
			}			

			break;
		}
		
		x = mobXPos[teamNum-1][laneNum-1] * adjustmentX * canvasWidth;
		y = mobYPos[teamNum-1][laneNum-1] * adjustmentY * canvasHeight;
		addTargets(targX[teamNum-1][laneNum-1] * canvasWidth, targY[teamNum-1][laneNum-1] * canvasHeight);

		setTarget(targX[teamNum-1][laneNum-1] * canvasWidth, targY[teamNum-1][laneNum-1] * canvasHeight);
	}

}
