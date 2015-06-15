package com.RobD.Moba.Utils;

import android.graphics.Color;

public class Constants {
	// mob spawn coordinates
	public final static float L1T1SpawnX = 0.135f;
	public final static float L1T1SpawnY = 0.717f;
	public final static float L2T1SpawnX = 0.261f;
	public final static float L2T1SpawnY = 0.806f;
	public final static float L3T1SpawnX = 0.305f;
	public final static float L3T1SpawnY = 0.956f;

	public final static float L1T2SpawnX = 0.729f;
	public final static float L1T2SpawnY = 0.029f;
	public final static float L2T2SpawnX = 0.82f;
	public final static float L2T2SpawnY = 0.183f;
	public final static float L3T2SpawnX = 0.963f;
	public final static float L3T2SpawnY = 0.25f;

	public final static float L1MidX = 0.135f;
	public final static float L1MidY = 0.029f;
	public final static float L3MidX = 0.963f;
	public final static float L3MidY = 0.956f;

	// tower coordinates
	public final static float[][][] towerXPos = {{{0.19f,0.19f,0.19f},{0.285f,0.346f,0.429f},{0.368f,0.524f,.731f}},{{0.635f,0.508f,0.351f},{0.789f,0.73f,0.658f},{0.924f,0.924f,0.924f}}};
	public final static float[][][] towerYPos = {{{0.62f,0.452f,0.227f},{0.704f,0.625f,0.523f},{0.891f,0.891f,0.891f}},{{0.1f,0.1f,0.1f},{0.319f,0.385f,0.473f},{0.358f,0.523f,0.721f}}};
	
	// health bar colours
	public final static int healthColour1 = Color.argb(255, 204, 15, 0);
	public final static int healthColour2 = Color.argb(255, 204, 78, 0);
	public final static int healthColour3 = Color.argb(255, 204, 136, 0);
	public final static int healthColour4 = Color.argb(255, 101, 127, 0);
	public final static int healthColour5 = Color.argb(255, 19, 106, 0);
}
