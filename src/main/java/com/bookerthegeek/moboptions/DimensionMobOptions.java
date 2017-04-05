package com.bookerthegeek.moboptions;

public class DimensionMobOptions {
	
	public int dimBaseDifficulty;
	public int dimMaxDifficulty;
	public int dimHealthCap;
	public int dimDamageCap;
	
	public DimensionMobOptions(int baseDiff, int maxDiff, int healthCap, int damageCap){
		dimBaseDifficulty = baseDiff;
		dimMaxDifficulty = maxDiff;
		dimHealthCap = healthCap;
		dimDamageCap = damageCap;
	}

}
