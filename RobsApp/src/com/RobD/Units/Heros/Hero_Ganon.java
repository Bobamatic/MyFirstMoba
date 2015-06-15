package com.RobD.Units.Heros;

import android.graphics.Color;

import com.RobD.robsapp.R;

public class Hero_Ganon extends HeroType{
	
	/**
	 * Link is a slower ranged hero
	 */
	public Hero_Ganon(){		
		 name = "Ganondorf";
		 widthDivider = 80;
		 attackRangeMultiplier = 4;
		 ranged = true;
		 rangedAttackcolour = Color.argb(255, 120, 0, 240);
		 velocity = 0.7f;
		 //velocity = 7f;
		 bmpID = R.drawable.ganon_sprite;
		 deadBmpID = R.drawable.gannondorf_dead;
		 NumOfFrames = 6;
		 AttackDelay = 1000;
		 maxHealth = new int[]{400,450,500,550,600,650,700,750,800,900};
		 attackPower = new int[]{15,16,18,20,23,26,29,33,37,41};	
	}
}
