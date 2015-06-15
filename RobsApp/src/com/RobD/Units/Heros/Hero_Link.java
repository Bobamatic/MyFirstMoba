package com.RobD.Units.Heros;

import com.RobD.robsapp.R;

public class Hero_Link extends HeroType{
	
	/**
	 * Link is a balanced melee hero
	 */
	public Hero_Link(){		
		 name = "Link";
		 widthDivider = 120;
		 attackRangeMultiplier = 1;
		 ranged = false;
		 bmpID = R.drawable.link_sprite;
		 deadBmpID = R.drawable.link_dead;
		 NumOfFrames = 6;
		 AttackDelay = 500;
		 maxHealth = new int[]{400,450,500,550,600,650,700,750,800,900};
		 attackPower = new int[]{20,22,24,27,30,34,38,43,48,54};
		 velocity = 0.8f;
		 /*velocity = 8f;
		 maxHealth = new int[]{1000,1500,2000,2500,3000,3500,4000,4500,5000,5500};
		 attackPower = new int[]{100,110,120,150,170,220,290,410,600,800};	*/	
	}
}
