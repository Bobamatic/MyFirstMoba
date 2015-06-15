package com.RobD.Units;

import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

import com.RobD.Moba.Utils.Constants;
import com.RobD.Moba.Utils.Speed;

public class Tower extends Unit{
	// tower positions calculated relative to screen size
	//private final float[][][] towerXPos = {{{0.19f,0.19f,0.19f},{0.285f,0.346f,0.429f},{0.368f,0.524f,.731f}},{{0.635f,0.508f,0.351f},{0.789f,0.73f,0.658f},{0.924f,0.924f,0.924f}}};
	//private final float[][][] towerYPos = {{{0.62f,0.452f,0.227f},{0.704f,0.625f,0.523f},{0.891f,0.891f,0.891f}},{{0.1f,0.1f,0.1f},{0.319f,0.385f,0.473f},{0.358f,0.523f,0.721f}}};
	
	public Tower(Bitmap bmp, int laneNum, int posNum, int teamNum, float canvasWidth, float canvasHeight) {
		id="TOWER-" + teamNum + "-" + (++Unit.nextId);
		IDNum = Unit.nextId;

		lane = laneNum;		
		unitType = 2;
		velocity = 0;
		alive = true;
		width = canvasWidth / 40;
		height = width;
		team = teamNum;
		speed = new Speed(0,0);
		ranged = true;
		bitmap = bmp;
		
		attackDelay = 1000;
		maxHealth = new int[]{2000};
		health = maxHealth[0];
		attackPower = new int[]{40};
		attackRange = width * 2.5f;
		lastAttack = new Date();
		healthPaint = new Paint();
		rangedAttackWidth = width / 6;
		level = 1;
		
		int color = Color.argb(255, 255, 255, 255);
		if(team == 1){
			color = Color.argb(255, 0, 106, 255);
		}else{
			color = Color.argb(255, 240, 0, 0);
		}
		paint = new Paint();
		paint.setColor(color);
		rangedAttackColour = color;
		
		
		x = Constants.towerXPos[teamNum - 1][laneNum - 1][posNum] * canvasWidth;
		y = Constants.towerYPos[teamNum - 1][laneNum - 1][posNum] * canvasHeight;
		
		/*if(teamNum == 1){
			switch(lane){
			case 1:
				x = 100;
				y = 30;
				break;
			case 2:
				x = 0;
				y = 10;
				break;
			case 3:
				x = 0;
				y = 10;
				break;
			}
		}else{
			
		}*/
	}
}
