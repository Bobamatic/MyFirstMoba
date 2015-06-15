package com.RobD.Units.Heros;

public abstract class HeroType {
	
	protected String name;
	protected int widthDivider;
	protected float attackRangeMultiplier;
	protected boolean ranged;
	protected int rangedAttackcolour;
	protected float velocity;
	protected int bmpID;
	protected int deadBmpID;
	protected int NumOfFrames;
	protected int AttackDelay;
	protected int[] maxHealth;
	protected int[] attackPower;
	protected boolean playerControlled;
	

	public int getDeadBmpID() {
		return deadBmpID;
	}
	public void setDeadBmpID(int deadBmpID) {
		this.deadBmpID = deadBmpID;
	}
	public int getRangedAttackcolour() {
		return rangedAttackcolour;
	}
	public void setRangedAttackcolour(int rangedAttackcolour) {
		this.rangedAttackcolour = rangedAttackcolour;
	}
	public boolean isPlayerControlled() {
		return playerControlled;
	}
	public void setPlayerControlled(boolean playerControlled) {
		this.playerControlled = playerControlled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWidthDivider() {
		return widthDivider;
	}
	public void setWidthDivider(int widthDivider) {
		this.widthDivider = widthDivider;
	}
	public float getAttackRangeMultiplier() {
		return attackRangeMultiplier;
	}
	public void setAttackRangeMultiplier(float attackRangeMultiplier) {
		this.attackRangeMultiplier = attackRangeMultiplier;
	}
	public boolean isRanged() {
		return ranged;
	}
	public void setRanged(boolean ranged) {
		this.ranged = ranged;
	}
	public float getVelocity() {
		return velocity;
	}
	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}
	public int getBmpID() {
		return bmpID;
	}
	public void setBmpID(int bmpID) {
		this.bmpID = bmpID;
	}
	public int getNumOfFrames() {
		return NumOfFrames;
	}
	public void setNumOfFrames(int numOfFrames) {
		NumOfFrames = numOfFrames;
	}
	public int getAttackDelay() {
		return AttackDelay;
	}
	public void setAttackDelay(int attackDelay) {
		AttackDelay = attackDelay;
	}
	public int[] getMaxHealth() {
		return maxHealth;
	}
	public void setMaxHealth(int[] maxHealth) {
		this.maxHealth = maxHealth;
	}
	public int[] getAttackPower() {
		return attackPower;
	}
	public void setAttackPower(int[] attackPower) {
		this.attackPower = attackPower;
	}
}
