/**
 * American Brownies 
 *  
 * 200g butter 
 * 2 cups brown sugar (really really packed, generous cups!) 
 * 1/2 cup cocoa 
 * 2 eggs 
 * 1 tsp vanilla 
 * 1 cup flour 
 * pinch of salt 
 *  
 * Preheat oven to 180 C, and grease well a square cake/baking tin (approx. 20 x 20cm or 8 x 8 inch) - or use a silicone one! 
 *  
 * Melt butter in medium saucepan over medium heat. Add brown sugar and cocoa and stir to mix well, making sure all is dissolved and nice sludgey consistency. Take off heat and let cool a few minutes if necessary (don't want to poach the eggs!! I have done this before...) before breaking in the eggs. Stir eggs well into mixture, then add vanilla, flour, salt and stir well for deliciously smooth consistency:) 
 *  
 * Pour into tin and bake for 20-25 minutes, checking with a skewer - when skewer comes out clean, take it out - do not over cook!  
 *  
 * Let the brownie sit in the pan untouched (yes I know this is hard) for several hours, preferably most of the day or overnight, so that it can all set nicely together and is much easier to take out.  
 *  
 * When cool, flip brownie onto plate or chopping board if using a silicone pan, then proceed to cut into squares. If it is a regular pan, cut into squares while still in the pan, and lift each brownie out individually (if you try to flip it out it will all fall apart). 
 *  
 * Enjoy!! Great with a glass of milk, or vanilla ice cream, or just by themselves:) 
 *  
 * ** This recipe can easily be double, tripled etc. to suit the numbers of people catered for, or the dimensions of the pan. I often double it and then cook it in a large rectangle brownie tin, about 25 x 35cm or 10 x 14 inch) 
 */

package com.RobD.Units;

import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

import com.RobD.Moba.Utils.Constants;
import com.RobD.Moba.Utils.Speed;

public abstract class Unit {
	private static final boolean rangeDebug = false;
	private static final int[] expToLevel = {200,300,400,500,600,700,800,900,1000,1100};	
	public static final int respawnTime = 30000;
	
	protected static int nextId	= 0;

	protected int IDNum;
	protected String id;			
	protected float x;
	protected float y;
	protected Speed speed;
	protected boolean alive;
	protected float targetX;
	protected float targetY;
	protected float finalTargetX;
	protected float finalTargetY;
	protected Paint paint;
	protected int team;
	protected float width;
	protected float height;
	protected float velocity;	
	protected Unit targetUnit;
	protected int pos;
	protected Unit attacking;
	protected boolean ranged;
	protected ArrayList<Float> xCoordinates;
	protected ArrayList<Float> yCoordinates;
	//private int coordinatePos;
	protected float rangedAttackWidth;
	protected int unitType; // 0 = hero, 1 = mob, 2 = tower, 3 = castle 
	private Date timeofDeath;
	protected float spawnX;
	protected float spawnY;
	protected int lane; // 1 = top, 2 == mid, 3 == bottom
	protected boolean healing;

	protected Bitmap bitmap;
	protected Bitmap deadBitmap;
	protected float spriteWidth;
	protected float spriteHeight;
	protected float facing;
	protected Rect sourceRect;	
	protected int frameNr;		
	protected int currentFrame;	
	protected long frameTicker;	
	protected int framePeriod;	
	protected Date lastFrameChanged;

	protected int attackDelay;
	protected int health;
	protected int[] maxHealth;	
	protected int[] attackPower;
	protected float attackRange;
	protected Date lastAttack;
	protected int rangedAttackColour;

	protected boolean targetedByPlayer;
	protected boolean playerControlled;
	protected Unit lastHitBy;

	protected int gold;
	protected int exp;
	protected int level;

	private RangedAttack rangedAttack;

	protected Paint healthPaint;
	private float healthP;
	
	/**
	 * The render event for a unit.
	 * Draws the unit sprite frame, health bar, 
	 * ranged attacks, level and targeting graphics 
	 * 
	 * @param  canvas  the canvas to be drawn onto
	 */
	public void render(Canvas canvas) {
		if(this.alive){		
			float usableWidth = canvas.getHeight() / 200f;

			if(unitType == 0){
				//if player draw movement marker
				if(this.targetUnit == null && (this.speed.getXv() + this.speed.getYv() > 0) && this.playerControlled){
					// if player is moving but not targeting a player
					Paint waypointPaint = new Paint();
					waypointPaint.setColor(Color.argb(255, 0, 255, 0));
					waypointPaint.setStyle(Style.STROKE);
					waypointPaint.setStrokeWidth(usableWidth/2);

					RectF waypointRect = new RectF(targetX - width/2, targetY, targetX + width/2, targetY + width/2); 
					canvas.drawOval(waypointRect , waypointPaint);
				}
			}

			// the left, top, right and bottom coordinates of the unit
			float L = this.x - this.width/2;
			float T = this.y - this.height/2;
			float R = this.x + this.width/2;
			float B = this.y + this.height/2;			

			RectF destRect = new RectF(L, T, R, B);

			// draw the range circle
			if(rangeDebug){
				Paint rangePaint = new Paint();
				if(team == 1){
					rangePaint.setColor(Color.argb(75, 255, 0, 0));
				}else{
					rangePaint.setColor(Color.argb(75, 0, 0, 255));
				}
				canvas.drawCircle(x, y, attackRange, rangePaint);
			}

			// draw targeting marker behind unit
			if(this.targetedByPlayer){
				Paint targetedPaint = new Paint();
				targetedPaint.setColor(Constants.healthColour1);
				targetedPaint.setStyle(Style.STROKE);
				targetedPaint.setStrokeWidth(usableWidth);

				RectF waypointRect = new RectF(L, B - width/3, R, B + width/3); 
				canvas.drawOval(waypointRect , targetedPaint);
			}
			
			if(this.bitmap != null){
				// draw the unit frame
				canvas.drawBitmap(bitmap, sourceRect, destRect, null);
			}else{				
				// draw a coloured square if no bitmap is available
				canvas.save();
				canvas.rotate(this.facing, x, y);
				canvas.drawRect(destRect, paint);
				canvas.restore();
			}

			// draw the units ranged attack
			if(this.rangedAttack != null){
				this.rangedAttack.render(canvas);
			}

			// draw the units health bar
			// health bar outline
			float HealthBarSize = (R - L) * healthP;
			canvas.drawRect(L, B, L + HealthBarSize, B + usableWidth, healthPaint);
			// health bar contents
			Paint outlinePaint = new Paint();
			outlinePaint.setColor(Color.BLACK);
			outlinePaint.setStyle(Style.STROKE);
			outlinePaint.setStrokeWidth(canvas.getHeight() / 800f);
			canvas.drawRect(L, B, R, B + usableWidth, outlinePaint);
			
			if(this.unitType == 0){
				// if hero draw hero level
				Paint textPaint = new Paint();
				textPaint.setColor(Color.BLACK);
				float textSize = canvas.getWidth() / 120f;
				textPaint.setTextSize(textSize);
				textPaint.setTextAlign(Align.CENTER);
				canvas.drawText("" + this.level, R, T, textPaint);
			}
		}else{
			// dead
			if(this.unitType == 0){
				// dead hero
				// the left, top, right and bottom coordinates of the unit
				float L = this.x - this.width/2;
				float T = this.y - this.height/2.5f;
				float R = this.x + this.width/2;
				float B = this.y + this.height/2.5f;			

				RectF destRect = new RectF(L, T, R, B);
				
				if(this.deadBitmap != null){
					// draw the unit frame
					canvas.drawBitmap(deadBitmap, null, destRect, null);
				}else{				
					// draw a coloured square if no bitmap is available
					canvas.save();
					canvas.rotate(this.facing, x, y);
					canvas.drawRect(destRect, paint);
					canvas.restore();
				}
			}
		}
	}
	
	/**
	 * Unused
	 * 
	 * @param  targX  the x target coordinate
	 * @param  targY  the y target coordinate
	 */
	public void addTargets(float targX, float targY){
		if(this.xCoordinates ==  null){
			xCoordinates = new ArrayList<Float>();
		}
		if(this.yCoordinates ==  null){
			yCoordinates = new ArrayList<Float>();
		}

		this.xCoordinates.add(targX);
		this.yCoordinates.add(targY);
	}

	/**
	 * Set the target coordinates for the unit
	 * 
	 * @param  targX  the x target coordinate
	 * @param  targY  the y target coordinate
	 */
	public void setTarget(float targX, float targY){
		// set the units target to a coordinate
		this.targetX = targX;
		this.targetY = targY;
		
		float distX = Math.abs(this.targetX - getX());
		float distY = Math.abs(this.targetY - getY());
		float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

		// calculate the speed to move at a diagonal 
		float time = distance / this.velocity;		

		float ratioX = distX /time;
		float ratioY = distY /time;

		float xSpeed = ratioX;
		float ySpeed = ratioY;

		if(getX() < targetX){
			this.speed.setXv(xSpeed);
			this.speed.setxDirection(Speed.DIRECTION_RIGHT);
		}else if(getX() > targetX){
			this.speed.setXv(xSpeed);
			this.speed.setxDirection(Speed.DIRECTION_LEFT);			
		}

		if(getY() < targetY){
			this.speed.setYv(ySpeed);
			this.speed.setyDirection(Speed.DIRECTION_DOWN);
		}else if(getY() > targetY){
			this.speed.setYv(ySpeed);
			this.speed.setyDirection(Speed.DIRECTION_UP);			
		}
	}

	/**
	 * Set a target unit for the unit to target
	 * 
	 * @param  targUnit  the unit to target
	 */
	public void setTargetUnit(Unit targUnit){
		// set the units target to another unit
		this.targetUnit = targUnit;
		if(targUnit == null){
			this.attacking = null;
		}
	}

	/**
	 * check if the unit was touch during the touch event
	 * 
	 * @param  eventX  the x coordinate touched
	 * @param  eventY  the y coordinate touched
	 * @param  player  the player unit
	 * @return a boolean depicting whether or not a unit was touched
	 */
	public boolean handleActionDown(float eventX, float eventY, Unit player) {
		if (eventX >= (x - width) && (eventX <= (x + width))) {
			if (eventY >= (y - height ) && (eventY <= (y + height ))) {
				player.setTargetUnit(this);
				player.setTarget(player.getX(), player.getY());
				this.setTargetedByPlayer(true);

				// touched by player
				return true;
			}
		}

		// not touched by player
		return false;
	}

	/**
	 * The update method for the castle
	 * checks nearby units to heal
	 * 
	 * @param  mobs  an Array of all mobs
	 * @param  heroes  an array of all heroes
	 * @param  player  the player unit
	 */
	public void updateCastle(ArrayList<Unit> mobs, ArrayList<Unit> heroes, Unit player){
		checkHealth();
		
		long curTime = new Date().getTime();

		if(curTime - this.lastAttack.getTime() >= this.attackDelay){
			// heal nearby units
			for(Unit m : mobs){
				if(m.getTeam() == this.team && m.isAlive()){
					// don't check units at full health, just to speed things up a little
					if(m.getHealth() < m.getmaxHealth()){
						float distX = Math.abs(this.x - m.getX());
						float distY = Math.abs(this.y - m.getY());
						float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

						// if within castles range
						if(distance <= this.attackRange){
							// subtract a negative (effectively adding)
							//m.subtractHealth(this.attackPower[0] * -1, this);
							m.subtractHealth(m.getAttackPower()/-2, this);
						}
					}
				}
			}
			
			// heal nearby heroes
			for(Unit h : heroes){
				if(h.getTeam() == this.team && h.isAlive()){
					// don't check units at full health, just to speed things up a little
					if(h.getHealth() < h.getmaxHealth()){
						h.setTarget(this.x, this.y);
						//h.setTargetUnit(null);
						float distX = Math.abs(this.x - h.getX());
						float distY = Math.abs(this.y - h.getY());
						float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

						// if within castles range
						if(distance <= this.attackRange){
							// subtract a negative (effectively adding)
							//h.subtractHealth(this.attackPower[0] * -1, this);
							h.subtractHealth(h.getAttackPower() / -2, this);
						}
					}
				}
			}

			// heal player
			if(this.team == player.getTeam() && player.isAlive()){
				if(player.getHealth() < player.getmaxHealth()){
					float distX = Math.abs(this.x - player.getX());
					float distY = Math.abs(this.y - player.getY());
					float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

					// if within castles range
					if(distance <= this.attackRange){
						// subtract a negative (effectively adding)
						//player.subtractHealth(this.attackPower[0] * -1, this);
						player.subtractHealth(player.getAttackPower() / -2, this);
					}
				}
			}

			this.lastAttack = new Date();
		}
	}
	
	/**
	 * The update method for the tower unit
	 * checks units to target
	 * 
	 */
	public void updateTower(){
		if(this.alive){
			checkHealth();
			
			if(targetUnit != null){
				if(targetUnit.isAlive()){	
					Date curTime = new Date();

					if(curTime.getTime() - this.lastAttack.getTime() >= attackDelay){
						if(this.rangedAttack == null){									
							//this.rangedAttack = new RangedAttack(this, this.targetUnit, null, this.rangedAttackWidth, this.attackPower[this.level - 1]);
							this.rangedAttack = new RangedAttack(this, this.targetUnit, null, this.rangedAttackWidth, this.attackPower[0]);
						}
						
						this.lastAttack = new Date();
					}
				}
			}
			
			if(this.rangedAttack != null){
				this.rangedAttack.update();
				
				if(!this.rangedAttack.isAlive()){									
					this.rangedAttack = null;
				}
			}
		}
	}

	/**
	 * The general update method, used to update
	 * the player unit and mob units.
	 * Handles updating positioning, sprite frame information
	 * 
	 */
	public void update() {		
		if(this.alive){
			checkHealth();
			
			// if hero, mob or tower
			float HeadingX = this.targetX;
			float HeadingY = this.targetY;
			this.attacking = null;

			if(targetUnit != null){
				if(targetUnit.isAlive()){	
					HeadingX = targetUnit.getX();
					HeadingY = targetUnit.getY();

					float distX = Math.abs(HeadingX - getX());
					float distY = Math.abs(HeadingY - getY());
					
					float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

					float time = distance / this.velocity;		

					float ratioX = distX /time;
					float ratioY = distY /time;

					if(distX - this.width/2 - this.attackRange <= 0 && distY - this.height/2 - this.attackRange <= 0){
						// attacking
						//TODO: set facing enemy
						this.attacking = targetUnit;
						this.speed.setXv(0);
						this.speed.setYv(0);

						Date curTime = new Date();

						if(curTime.getTime() - this.lastAttack.getTime() >= attackDelay){
							//TODO: show attack animation
							if(!this.ranged){
								this.targetUnit.subtractHealth(this.attackPower[this.level - 1], this);						
							}else{
								if(this.rangedAttack == null){									
									this.rangedAttack = new RangedAttack(this, this.targetUnit, null, this.rangedAttackWidth, this.attackPower[this.level - 1]);
								}
							}

							this.lastAttack = new Date();
						}


					}else{
						float xSpeed = ratioX;
						float ySpeed = ratioY;

						if(getX() < HeadingX){
							this.speed.setXv(xSpeed);
							this.speed.setxDirection(Speed.DIRECTION_RIGHT);
						}else if(getX() > HeadingX){
							this.speed.setXv(xSpeed);
							this.speed.setxDirection(Speed.DIRECTION_LEFT);			
						}

						if(getY() < HeadingY){
							this.speed.setYv(ySpeed);
							this.speed.setyDirection(Speed.DIRECTION_DOWN);
						}else if(getY() > HeadingY){
							this.speed.setYv(ySpeed);
							this.speed.setyDirection(Speed.DIRECTION_UP);			
						}
						//}
					}
				}
			}else{				
				// check if the unit is at it's location if not, make sure it is still moving
				if(HeadingX > -1){
					if((this.speed.getxDirection() == Speed.DIRECTION_RIGHT && this.x >= HeadingX - this.speed.getXv()) || (this.speed.getxDirection() == Speed.DIRECTION_LEFT && this.x <= HeadingX + this.speed.getXv())){
						this.x = this.targetX;
						this.speed.setXv(0);
					}else{
						if(this.speed.getXv() == 0){
							setTarget(this.targetX, this.targetY);
						}
					}
				}

				if(HeadingY > -1){
					if((this.speed.getyDirection() == Speed.DIRECTION_DOWN && this.y >= HeadingY - this.speed.getYv()) || (this.speed.getyDirection() == Speed.DIRECTION_UP && this.y <= HeadingY + this.speed.getYv())){
						this.y = this.targetY;
						this.speed.setYv(0);
					}else{
						if(this.speed.getYv() == 0){
							setTarget(this.targetX, this.targetY);
						}
					}
				}
								
				if(this.unitType == 1){
					if(this.speed.getXv() + this.speed.getYv() == 0){
						// update mobs target to be the enemy castle
						this.targetX = this.finalTargetX;
						this.targetY = this.finalTargetY;
						setTarget(this.finalTargetX,  this.finalTargetY);
					}
				}
			}	

			// work out direction unit should be facing
			float distX = HeadingX - getX();
			float distY = HeadingY - getY();

			if(targetUnit!=null){
				distX = targetUnit.getX() - getX();
				distY = targetUnit.getY() - getY();
			}

			if(this.speed.getXv() > 0 && this.speed.getYv() > 0){
				float angle = (float)Math.atan2(distY, distX);
				this.facing = (float)Math.toDegrees(angle);
			}

			// if not attacking update sprite then move
			if(this.attacking == null){	
				//if(Math.abs(distX) > this.speed.getXv() || Math.abs(distY) > this.speed.getYv()){
				if(Math.abs(distX) + Math.abs(distY) > this.velocity){
					if(this.bitmap != null && this.unitType < 3){
						// update sprite
						Date nowTime = new Date(); 
						if (nowTime.getTime() > this.lastFrameChanged.getTime() + this.framePeriod) {					
							this.lastFrameChanged = new Date();

							this.currentFrame++;
							if (this.currentFrame >= this.frameNr) {
								this.currentFrame = 0;
							}
						}

						// define the rectangle to cut out sprite
						this.sourceRect.left = (int) (this.currentFrame * this.spriteWidth);
						this.sourceRect.right = (int) (this.sourceRect.left + this.spriteWidth);

						// moving up
						int spriteBearing = 3;

						if(this.facing >= -45 && this.facing < 45){
							// moving right
							spriteBearing = 0;
						}else if (this.facing >= 45 && this.facing < 135){
							// moving down
							spriteBearing = 1;
						}else if (this.facing >= 135 || this.facing < -135){
							// moving left
							spriteBearing = 2;
						}

						this.sourceRect.top = (int) (spriteBearing * this.spriteHeight);
						this.sourceRect.bottom = (int) (this.sourceRect.top + this.spriteHeight);

						if(this.speed.getXv() == 0 && this.speed.getYv() == 0){
							this.sourceRect.left = (int) (0 * this.spriteWidth);
							this.sourceRect.right = (int) (this.sourceRect.left + this.spriteWidth);
						}
					}

					// move
					this.x += (this.speed.getXv() * this.speed.getxDirection());
					this.y += (this.speed.getYv() * this.speed.getyDirection());
				}
			}

		}else if(isHero()){
			// dead hero
			long curTime = new Date().getTime();
			
			if(curTime - timeofDeath.getTime() >= respawnTime){
				// respawn
				this.x = this.spawnX;
				this.y = this.spawnY;
				this.targetX = this.x;
				this.targetY = this.y;
				this.alive = true;
				this.health = this.maxHealth[this.level - 1];
			}
		}

		// I moved this outside of the attack range if statement because a projectile would hang if a unit moved out of attack range
		if(this.ranged){
			if(this.rangedAttack != null){
				this.rangedAttack.update();

				if(!this.rangedAttack.isAlive()){									
					this.rangedAttack = null;
				}
			}
		}
	}

	/**
	 * The update method for NPC heroes
	 * Handles updating positioning, sprite 
	 * frame information and simple A.I.
	 * 
	 * @param  mobs  An array of all mobs
	 */
	public void updateHero(ArrayList<Unit> mobs) {		
		if(this.alive){
			checkHealth();
			
			// check if an AI hero should retreat
			if(this.healthP < 0.3f){
				if(this.targetUnit != null && this.targetUnit.isHero() && this.targetUnit.getHealthPercent() < this.getHealthPercent()){
					// if targeting a hero with less health than yourself keep attacking
				}else{
					// retreat
					this.healing = true;
					this.targetUnit = null;
					this.setTarget(this.spawnX, this.spawnY);
				}
			}else if(this.healing){
				// keep healing until at full health
				if(this.health >= this.getmaxHealth()){
					this.healing = false;
				}
			}

			float HeadingX = this.targetX;
			float HeadingY = this.targetY;
				
			if(this.targetUnit != null){
				if(targetUnit.isAlive()){
					HeadingX = targetUnit.getX();
					HeadingY = targetUnit.getY();
					
					float distX = Math.abs(HeadingX - getX());
					float distY = Math.abs(HeadingY - getY());
					
					/*float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

					float time = distance / this.velocity;		

					float ratioX = distX /time;
					float ratioY = distY /time;*/
					if(distX - this.width/2 - this.attackRange <= 0 && distY - this.height/2 - this.attackRange <= 0){
						// attacking
						this.attacking = targetUnit;
						this.speed.setXv(0);
						this.speed.setYv(0);

						Date curTime = new Date();

						if(curTime.getTime() - this.lastAttack.getTime() >= attackDelay){
							//TODO: show attack animation
							if(!this.ranged){
								this.targetUnit.subtractHealth(this.attackPower[this.level - 1], this);							
							}else{
								if(this.rangedAttack == null){									
									this.rangedAttack = new RangedAttack(this, this.targetUnit, null, this.rangedAttackWidth, this.attackPower[this.level - 1]);
								}
							}

							this.lastAttack = new Date();
						}
					}else{
						// not in range of target
						Unit leaderMob = null;
						
						// find a mob to follow
						for(Unit mob : mobs){
							if(mob.getTeam() == this.team && mob.getLane() == this.lane && mob.isAlive()){
								if(leaderMob != null){
									if(leaderMob.getIDNum() > mob.getIDNum()){
										leaderMob = mob;
									}
								}else{
									leaderMob = mob;
								}
							}
						}
						
						if(leaderMob != null){
							// follow mob							
							HeadingX = leaderMob.getX();
							HeadingY = leaderMob.getY();							
							distX = Math.abs(HeadingX - getX());
							distY = Math.abs(HeadingY - getY());
							
							float distance = (float) Math.sqrt((distX * distX) + (distY * distY));
							
							float time;
							if(distance < this.width){		
								// if caught up with leader mob move at their pace
								time = distance / leaderMob.getVelocity();
							}else{
								// lagging behind leader mob so move at full speed		
								time = distance / this.velocity;
							}	
							
							float xSpeed = distX / time;
							float ySpeed = distY / time;

							if(this.x < leaderMob.getX()){
								this.speed.setXv(xSpeed);
								this.speed.setxDirection(Speed.DIRECTION_RIGHT);
							}else if(this.x > leaderMob.getX()){
								this.speed.setXv(xSpeed);
								this.speed.setxDirection(Speed.DIRECTION_LEFT);			
							}

							if(this.y < leaderMob.getY()){
								this.speed.setYv(ySpeed);
								this.speed.setyDirection(Speed.DIRECTION_DOWN);
							}else if(this.y > leaderMob.getY()){
								this.speed.setYv(ySpeed);
								this.speed.setyDirection(Speed.DIRECTION_UP);			
							}	
						}
					}
				}
			}else if (this.healing == false){
				// no target unit
				Unit leaderMob = null;
				
				// find a mob to follow
				for(Unit mob : mobs){
					if(mob.getTeam() == this.team && mob.getLane() == this.lane && mob.isAlive()){
						if(leaderMob != null){
							if(leaderMob.getIDNum() > mob.getIDNum()){
								leaderMob = mob;
							}
						}else{
							leaderMob = mob;
						}
					}
				}
				
				if(leaderMob != null){
					// follow mob							
					HeadingX = leaderMob.getX();
					HeadingY = leaderMob.getY();							
					float distX = Math.abs(HeadingX - getX());
					float distY = Math.abs(HeadingY - getY());
					
					float distance = (float) Math.sqrt((distX * distX) + (distY * distY));
					
					float time;
					if(distance < this.width){		
						// if caught up with leader mob move at their pace
						time = distance / leaderMob.getVelocity();
					}else{
						// lagging behind leader mob so move at full speed		
						time = distance / this.velocity;
					}	
					
					float xSpeed = distX / time;
					float ySpeed = distY / time;

					if(this.x < leaderMob.getX()){
						this.speed.setXv(xSpeed);
						this.speed.setxDirection(Speed.DIRECTION_RIGHT);
					}else if(this.x > leaderMob.getX()){
						this.speed.setXv(xSpeed);
						this.speed.setxDirection(Speed.DIRECTION_LEFT);			
					}

					if(this.y < leaderMob.getY()){
						this.speed.setYv(ySpeed);
						this.speed.setyDirection(Speed.DIRECTION_DOWN);
					}else if(this.y > leaderMob.getY()){
						this.speed.setYv(ySpeed);
						this.speed.setyDirection(Speed.DIRECTION_UP);			
					}	
				}
			}
			
			// work out direction unit should be facing
			float distX = HeadingX - getX();
			float distY = HeadingY - getY();

			if(targetUnit!=null){
				distX = targetUnit.getX() - getX();
				distY = targetUnit.getY() - getY();
			}

			if(this.speed.getXv() > 0 && this.speed.getYv() > 0){
				float angle = (float)Math.atan2(distY, distX);
				this.facing = (float)Math.toDegrees(angle);
			}

			// if not attacking update sprite then move
			if(this.attacking == null){	
				if(Math.abs(distX) > this.speed.getXv() || Math.abs(distY) > this.speed.getYv()){
					if(this.bitmap != null && this.unitType < 3){
						// update sprite
						Date nowTime = new Date(); 
						if (nowTime.getTime() > this.lastFrameChanged.getTime() + this.framePeriod) {					
							this.lastFrameChanged = new Date();

							this.currentFrame++;
							if (this.currentFrame >= this.frameNr) {
								this.currentFrame = 0;
							}
						}

						// define the rectangle to cut out sprite
						this.sourceRect.left = (int) (this.currentFrame * this.spriteWidth);
						this.sourceRect.right = (int) (this.sourceRect.left + this.spriteWidth);

						// moving up
						int spriteBearing = 3;

						if(this.facing >= -45 && this.facing < 45){
							// moving right
							spriteBearing = 0;
						}else if (this.facing >= 45 && this.facing < 135){
							// moving down
							spriteBearing = 1;
						}else if (this.facing >= 135 || this.facing < -135){
							// moving left
							spriteBearing = 2;
						}

						this.sourceRect.top = (int) (spriteBearing * this.spriteHeight);
						this.sourceRect.bottom = (int) (this.sourceRect.top + this.spriteHeight);

						if(speed.getXv() == 0 && speed.getYv() == 0){
							this.sourceRect.left = (int) (0 * this.spriteWidth);
							this.sourceRect.right = (int) (this.sourceRect.left + this.spriteWidth);
						}
					}

					// move
					x += (speed.getXv() * speed.getxDirection());
					y += (speed.getYv() * speed.getyDirection());
				}
			}
		}else{
			// hero is dead
			long curTime = new Date().getTime();
			
			if(curTime - timeofDeath.getTime() >= respawnTime){
				// respawn
				this.x = this.spawnX;
				this.y = this.spawnY;
				this.targetX = this.x;
				this.targetY = this.y;
				this.alive = true;
				this.health = this.maxHealth[this.level - 1];
			}
		}

		// I moved this outside of the attack range if statement because a projectile would hang if a unit moved out of attack range
		if(this.ranged){
			if(this.rangedAttack != null){
				this.rangedAttack.update();

				if(!this.rangedAttack.isAlive()){									
					this.rangedAttack = null;
				}
			}
		}
	}
	

	/**
	 * Searches for a unit to target based off location
	 * 
	 * @param  tower   a boolean depicting whether not the unit is a tower
	 * @param  mobs    a list of all alive mobs
	 * @param  heroes  a list of all heroes
	 * @param  player  the player unit
	 * @param  towers  a list of all towers
	 * @param  team1Castle  the castle unit of team 1
	 * @param  team2Castle  the castle unit of team 2
	 */
	public void seekTarget(boolean tower, ArrayList<Unit> mobs, ArrayList<Unit> heroes, Unit player, ArrayList<Unit> towers, Unit team1Castle, Unit team2Castle) {
		boolean priorityFound = false;
		Unit mobToTrack = null;
		float minimumSoFar = width * 11;
		
		if(tower){
			minimumSoFar = this.attackRange;
		}
				
		// check heroes
		for(Unit hero : heroes){
			if(hero.getTeam() != this.team && hero.isAlive()){
				float distance = scopeUnit(hero, this);
				if(distance < minimumSoFar){
					mobToTrack = hero;
					minimumSoFar = distance;
					priorityFound = true;
				}
			}
		}

		//check player
		if(player.getTeam() != this.team && player.isAlive()){
			float distance = scopeUnit(player, this);
			if(distance < minimumSoFar){
				mobToTrack = player;
				minimumSoFar = distance;
				priorityFound = true;
			}
		}

		// is a hero has been targeted then skip checking all mobs (saves time)
		if(!priorityFound){
			// check mobs
			for(Unit mob : mobs){
				if(mob.getTeam() != this.team && mob.isAlive()){
					float distance = scopeUnit(mob, this);
					if(distance < minimumSoFar){
						mobToTrack = mob;
						minimumSoFar = distance;
						priorityFound = true;
					}
				}
			}
		}
		
		// only check tower to target if not a tower because towers will never be in tange of another tower
		if(!tower){
			this.setAttacking(null);
			if(!priorityFound){
				for(Unit t : towers){
					if(t.getTeam() != this.team && t.isAlive()){
						float distance = scopeUnit(t, this);
						if(distance < minimumSoFar){
							mobToTrack = t;
							minimumSoFar = distance;
							priorityFound = true;
						}
					}
				}
			}
			
			// check castle
			if(!priorityFound){
				Unit castle = null;
				if(this.team == team1Castle.getTeam()){
					castle = team2Castle;
				}else if(this.team == team2Castle.getTeam()){
					castle = team1Castle;
				}
				
				if(castle.isAlive()){
					float distance = scopeUnit(castle, this);
					if(distance < minimumSoFar){
						mobToTrack = castle;
						minimumSoFar = distance;
					}
				}
			}
		}
		
		// set target
		if(mobToTrack != null){
			setTargetUnit(mobToTrack);
		}else{
			setTargetUnit(null);
		}
	}
	
	/**
	 * calculate the distance between 2 units
	 * 
	 * @param  u  target unit
	 * @param  u  source unit
	 * @return a float representing the distance between the two units
	 */
	private float scopeUnit(Unit u, Unit z){
		if( u != null && z != null){
			float enemyX = u.getX();
			float enemyY = u.getY();

			float distX = Math.abs(enemyX - z.getX());
			float distY = Math.abs(enemyY - z.getY());

			float distance = (float) Math.sqrt((distX * distX) + (distY * distY));

			return distance;
		}
		
		return 10000;
	}
	
	/**
	 * Update the units healP (health percentage) value
	 */
	private void checkHealth() {
		healthP = (float)this.health / (float)this.maxHealth[this.level - 1];

		if(healthP > 0.8f){
			healthPaint.setColor(Constants.healthColour5);
		}else if(healthP > 0.6f){
			healthPaint.setColor(Constants.healthColour4);				
		}else if(healthP > 0.3f){
			healthPaint.setColor(Constants.healthColour3);				
		}else if(healthP > 0.1f){
			healthPaint.setColor(Constants.healthColour2);				
		}else{
			healthPaint.setColor(Constants.healthColour1);				
		}	
	}

	/**
	 * Add to subtract heal from a unit
	 * and kill unit if necessary 
	 * 
	 * @param  damage  The damage to be subtracted from the units health (a negative number will add health)
	 * @param  dealer  The unit that dealt the damage
	 */
	public void subtractHealth(int damage, Unit dealer){
		if(this.health - damage > this.maxHealth[this.level - 1]){
			this.health = maxHealth[this.level - 1];
		}else{
			this.health -= damage;
		}

		if(dealer != null){
			this.lastHitBy = dealer;
		}

		if(this.health<=0){
			this.health = 0;
			this.alive = false;
			this.timeofDeath = new Date();
			this.targetedByPlayer = false;
			
			// if unit is the player or another hero
			if(isHero()){
				this.targetUnit = null;
				this.speed.setXv(0);
				this.speed.setYv(0);
			}
			
			// check if damage dealer is a player
			if(this.lastHitBy.isHero()){
				int goldAmount = 10;
				int expAmount = 10;
				switch(this.unitType){
				case 0:// hero
					goldAmount = this.level * 100;
					expAmount = this.level * 100;
					break;
				case 1:// mob					
					goldAmount =  this.level * 10;
					expAmount =  this.level * 20;
					break;
				case 2:// tower
				case 3:// building
					goldAmount = 100;
					expAmount = 200;
					break;
				}

				this.lastHitBy.setGold(this.lastHitBy.getGold() + goldAmount);
				this.lastHitBy.addExp(expAmount);
			}
		}
	}
	
	/**
	 * add experience to a unit and level up if appropriate
	 * 
	 * @param  expAmount an integer of the exp to be added
	 */
	public void addExp(int expAmount){
		
		this.exp += expAmount;

		int expNeeded = 0;

		// work out cumulative exp
		for(int i=0; i<this.level; i++){
			expNeeded += Unit.expToLevel[i];
		}

		if(this.exp >= expNeeded){
			if(this.exp < expNeeded + Unit.expToLevel[this.level]){
				// level up!
				this.level++;
				this.health = maxHealth[this.level - 1];
			}else{
				// TODO: leveled more than once, shouldn't be possible, but just in case
			}
		}
	}

	/**
	 * Get the percentage of the progress towards leveling up
	 * 
	 * @return a float between 0 and 1 representing the percentage of progress towards the next level up
	 */
	public float getEXPPercent(){
		float expPercent = 0;

		int pastEXP = 0;

		// work out cumulative exp
		for(int i=0; i<this.level-1; i++){
			pastEXP += Unit.expToLevel[i];
		}

		int curLevelEXP = this.exp - pastEXP;

		expPercent = curLevelEXP / (float)Unit.expToLevel[this.level-1];

		return expPercent;
	}	
	
	// There's only getters and setter from here on, turn back now!
	
	public float getTargetX() {
		return targetX;
	}

	public void setTargetX(float targetX) {
		this.targetX = targetX;
	}

	public float getTargetY() {
		return targetY;
	}

	public void setTargetY(float targetY) {
		this.targetY = targetY;
	}

	public boolean isHealing() {
		return healing;
	}

	public void setHealing(boolean healing) {
		this.healing = healing;
	}

	public int getLane() {
		return lane;
	}

	public void setLane(int lane) {
		this.lane = lane;
	}

	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}

	public void setAlive(boolean alive){
		this.alive = alive;
	}

	public boolean isAlive(){
		return this.alive;
	}

	public int getTeam(){
		return this.team;
	}
	
	public boolean isHero(){
		if(this.unitType == 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean isTargetedByPlayer() {
		return targetedByPlayer;
	}

	public void setTargetedByPlayer(boolean targetedByPlayer) {
		this.targetedByPlayer = targetedByPlayer;
	}

	public Unit getLastHitBy() {
		return lastHitBy;
	}

	public void setLastHitBy(Unit lastHitBy) {
		this.lastHitBy = lastHitBy;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getAttackRange() {
		return attackRange;
	}

	public void setRangedAttack(RangedAttack ra){
		this.rangedAttack = ra;
	}

	public Unit getAttacking(){
		return this.attacking;
	}

	public int getHealth(){
		return this.health;
	}

	public float getHealthPercent(){
		healthP = (float)this.getHealth() / (float)this.getmaxHealth();		
		return this.healthP;
	}

	public int getAttackPower(){
		return this.attackPower[this.level - 1];
	}

	public int getUnitType(){
		return this.unitType;
	}

	public Unit getTargetUnit(){
		return this.targetUnit;
	}

	public int getmaxHealth(){
		return this.maxHealth[this.level - 1];
	}

	public Date getLastAttack(){
		return this.lastAttack;
	}

	public int getAttackDelay(){
		return this.attackDelay;
	}
	
	public Date getTimeOfDeath(){
		return this.timeofDeath;
	}
	
	public int getIDNum() {
		return this.IDNum;
	}
	
	public String getid() {
		return this.id;
	}
	
	public float getVelocity() {
		return this.velocity;
	}
	
	public void setAttacking(Unit unit) {
		this.attacking = unit;
	}

	public int getRangedAttackColour() {
		return rangedAttackColour;
	}

	public void setRangedAttackColour(int rangedAttackColour) {
		this.rangedAttackColour = rangedAttackColour;
	}
}
