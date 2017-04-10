package com.bookerthegeek.moboptions.enchantment;

import java.util.ArrayList;
import java.util.HashMap;

import com.bookerthegeek.moboptions.equipment.SetHandler;
import com.bookerthegeek.moboptions.equipment.TierHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;

public class EnchantmentList {
	
	public String info = "Only enchantments that are applicable to a set-item can be used. Silk-Touch will thus never be applied to a sword or armour piece, even if added, but may be applicable if a set contains a pickaxe or shovel";
	
	private ArrayList<EnchantmentHolder> tier1 = Lists.newArrayList();
	private ArrayList<EnchantmentHolder> tier2 = Lists.newArrayList();
	private ArrayList<EnchantmentHolder> tier3 = Lists.newArrayList();
	private ArrayList<EnchantmentHolder> tier4 = Lists.newArrayList();
	private ArrayList<EnchantmentHolder> tier5 = Lists.newArrayList();
	
	public EnchantmentList addToList(int tier, EnchantmentHolder enchant) {
		if (tier == 1)
			tier1.add(enchant);
		if (tier == 2)
			tier2.add(enchant);
		if (tier == 3)
			tier3.add(enchant);
		if (tier == 4)
			tier4.add(enchant);
		if (tier == 5)
			tier5.add(enchant);

		return this;
	}
	
	public ArrayList<EnchantmentHolder> getEnchantsForTier(int tier) {
		switch (tier) {
		case 1:
			return tier1;
		case 2:
			return tier2;
		case 3:
			return tier3;
		case 4:
			return tier4;
		case 5:
			return tier5;
		default:
			return new ArrayList<EnchantmentHolder>();
		}
	}

}
