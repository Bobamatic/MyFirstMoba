package com.RobD.Units;

import java.util.Date;

import com.RobD.Moba.Utils.Constants;
import com.RobD.Moba.Utils.Speed;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public class Castle extends Unit{

	public Castle(Bitmap bmp, int teamNum, float canvasWidth, float canvasHeight) {
		id="CASTLE-" + teamNum + "-" + (++Unit.nextId);
		
		unitType = 3;
		velocity = 0;
		alive = true;
		width = canvasWidth / 20;
		height = width;
		team = teamNum;
		speed = new Speed(0,0);
		ranged = true;
		bitmap = bmp;
		
		attackPower = new int[]{2};
		attackDelay = 500; // for healing
		maxHealth = new int[]{4000};
		health = maxHealth[0];
		attackRange = width * 3;
		healthPaint = new Paint();
		lastAttack = new Date();
		level = 1;
		
		int color = Color.argb(255, 255, 255, 255);
		if(team == 1){
			 color = Color.argb(255, 128, 0, 0);
		}else{
			 color = Color.argb(255, 0, 0, 128);
		}
		paint = new Paint();
		paint.setColor(color);
		
		
		if(team == 1){
			x = canvasWidth * Constants.towerXPos[0][0][0];
			y = canvasHeight * Constants.towerYPos[0][2][0];
		}else{
			x = canvasWidth * Constants.towerXPos[1][2][0];
			y = canvasHeight * Constants.towerYPos[1][0][0];
		}
	}
	
}
