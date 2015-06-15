package com.RobD.Units;

import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.RobD.Moba.Utils.Speed;

public class RangedAttack {

	private Speed speed;
	private float x;
	private float y;
	private float width;
	private Bitmap bitmap;
	private boolean alive;
	private int velocity;
	private Unit unit;
	private Unit target;
	private int damage;
	private int lifetime;
	private long createdTime;
	
	public RangedAttack(Unit pUnit, Unit pTarget, Bitmap bitmap, float width, int attackPower){
		this.x = pUnit.getX();
		this.y = pUnit.getY();
		this.target = pTarget;
		this.unit = pUnit;
		this.bitmap = bitmap;
		this.width = width/2;
		this.alive = true;
		this.velocity = 3;
		this.damage = attackPower;
		this.lifetime = 2000;
		this.createdTime= new Date().getTime();		
	}
	
	public void update(){
		if(this.alive){	
			long curTime = new Date().getTime();
			if(curTime - this.createdTime < this.lifetime){
				float distX = Math.abs(this.target.getX() - this.x);
				float distY = Math.abs(this.target.getY() - this.y);
				float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

				float time = distance / this.velocity;		

				if(distance > this.target.getWidth()/2){				
					float ratioX = distX /time;
					float ratioY = distY /time;

					float xSpeed = ratioX;
					float ySpeed = ratioY;

					this.speed = new Speed(0,0);

					if(this.x < this.target.getX()){
						this.speed.setXv(xSpeed);
						this.speed.setxDirection(Speed.DIRECTION_RIGHT);
					}else if(this.x > this.target.getX()){
						this.speed.setXv(xSpeed);
						this.speed.setxDirection(Speed.DIRECTION_LEFT);			
					}

					if(this.y < this.target.getY()){
						this.speed.setYv(ySpeed);
						this.speed.setyDirection(Speed.DIRECTION_DOWN);
					}else if(this.y > this.target.getY()){
						this.speed.setYv(ySpeed);
						this.speed.setyDirection(Speed.DIRECTION_UP);			
					}

					this.x += (speed.getXv() * speed.getxDirection());
					this.y += (speed.getYv() * speed.getyDirection());
				}else{			
					this.target.subtractHealth(this.damage, this.unit);
					this.alive = false;
				}
			}else{
				this.alive = false;
			}
		}
	}
	
	public void render(Canvas canvas){
		if(this.alive){
			if(bitmap == null){
				Paint paint = new Paint();
				paint.setColor(this.unit.getRangedAttackColour());
				canvas.drawCircle(this.x, this.y, this.width, paint);
			}
		}
	}
	
	public boolean isAlive(){
		return this.alive;
	}
}
