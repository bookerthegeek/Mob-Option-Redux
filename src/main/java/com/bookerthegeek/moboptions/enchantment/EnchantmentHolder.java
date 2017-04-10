package com.bookerthegeek.moboptions.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentHolder {
	
	public final String enchantment;
	public final int chanceToApply;
	public final int maxLvl;
	public final int minLvl;
	
	public EnchantmentHolder(ResourceLocation loc, int chance, int max, int min){
		enchantment = loc.toString();
		chanceToApply = chance;
		maxLvl = max;
		minLvl = min;
	}

}
