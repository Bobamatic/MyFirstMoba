package com.RobD.Units.Heros;

import java.util.Date;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.RobD.Moba.Utils.Constants;
import com.RobD.Moba.Utils.Speed;
import com.RobD.Units.Unit;

public class Hero extends Unit{
	
	public Hero(int teamNum, float canvasWidth, float canvasHeight, HeroType heroType, Context context, boolean player, int laneNum){
		id="HERO-" + teamNum + "-" + (++Unit.nextId);
		IDNum = Unit.nextId;

		lane = laneNum;
		unitType = 0;
		velocity = heroType.getVelocity();
		alive = true;
		width = canvasWidth / heroType.getWidthDivider();		
		height = width;
		team = teamNum;
		speed = new Speed(0,0);
		bitmap = BitmapFactory.decodeResource(context.getResources(), heroType.getBmpID());
		deadBitmap = BitmapFactory.decodeResource(context.getResources(), heroType.getDeadBmpID());
		currentFrame = 0;
		frameNr = heroType.getNumOfFrames();
		spriteWidth = bitmap.getWidth() / frameNr;
		spriteHeight = bitmap.getHeight() / 4;
		sourceRect = new Rect(0, 0, (int)spriteWidth, (int)spriteHeight);
		framePeriod = 100;
		frameTicker = 0l;
		lastFrameChanged = new Date();
		ranged = heroType.isRanged();
		rangedAttackColour = heroType.getRangedAttackcolour();
		playerControlled = player;
		healing = false;
		
		gold = 0;
		exp = 0;
		level = 1;
		
		attackDelay = 500;
		maxHealth = heroType.getMaxHealth();
		health = maxHealth[0];
		attackPower = heroType.getAttackPower();
		attackRange = width * heroType.getAttackRangeMultiplier();
		lastAttack = new Date();
		healthPaint = new Paint();
		rangedAttackWidth = width / 4;

		int color = Color.argb(255, 255, 255, 255);
		if(team == 1){
			color = Color.argb(255, 255, 128, 128);
		}else{
			color = Color.argb(255, 0, 0, 255);
		}
		paint = new Paint();
		paint.setColor(color);

		// team castle locations
		switch(teamNum){
		case 1:
			x = canvasWidth * Constants.towerXPos[0][0][0];
			y = canvasHeight * Constants.towerYPos[0][2][0];
			break;
		case 2:
			x = canvasWidth * Constants.towerXPos[1][2][0];
			y = canvasHeight * Constants.towerYPos[1][0][0];
			break;
		}

		targetX = x;
		targetY = y;
		spawnX = x;
		spawnY = y;
	}

}
