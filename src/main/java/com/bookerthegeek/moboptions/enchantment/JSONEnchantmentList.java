package com.bookerthegeek.moboptions.enchantment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import com.bookerthegeek.moboptions.MobOptions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

public class JSONEnchantmentList {

	public File jsonLoc = new File(MobOptions.INSTANCE.getConfigFolder() + "/enchantments.json");
	public File enchLoc = new File(MobOptions.INSTANCE.getConfigFolder() + "/enchantment-dump.txt");
	public final EnchantmentList list;

	public JSONEnchantmentList() {
		list = new EnchantmentList();
		list.addToList(1, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 1, 1))
			.addToList(1, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 1, 1))
			.addToList(1, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 1, 1))
			
			.addToList(2, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 2, 1))
			.addToList(2, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 2, 1))
			.addToList(2, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 2, 1))
			.addToList(2, new EnchantmentHolder(new ResourceLocation("minecraft", "fire_protection"), 50, 1, 1))
			
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 3, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 3, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 3, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 3, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 3, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 3, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "fire_protection"), 50, 2, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "fire_aspect"), 50, 1, 1))
			.addToList(3, new EnchantmentHolder(new ResourceLocation("minecraft", "sharpness"), 50, 1, 1))
			
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 4, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 4, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 4, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 4, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 4, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 4, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "fire_protection"), 50, 3, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "fire_aspect"), 50, 2, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "sharpness"), 50, 3, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "blast_protection"), 50, 3, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "projectile_protection"), 50, 3, 1))
			.addToList(4, new EnchantmentHolder(new ResourceLocation("minecraft", "unbreaking"), 50, 2, 1))
			
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "protection"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "knockback"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "feather_falling"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "fire_protection"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "fire_aspect"), 50, 2, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "sharpness"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "blast_protection"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "projectile_protection"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "unbreaking"), 50, 5, 1))
			.addToList(5, new EnchantmentHolder(new ResourceLocation("minecraft", "thorns"), 50, 5, 1));
	}

	public JSONEnchantmentList addEnchants() {

		

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		if (!jsonLoc.exists()) {
			try (Writer writer = new FileWriter(jsonLoc)) {
				jsonLoc.createNewFile();
				gson.toJson(list, writer);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!enchLoc.exists()) {
			try (Writer writer = new FileWriter(enchLoc, true)) {
				enchLoc.createNewFile();
				writer.append("These are the enchantments available in the current Registry. If you add a mod that adds enchantments, delete this file, and a new one will be created that includes the new ones."+ System.lineSeparator());
				ArrayList<String> enchants = new ArrayList<String>();
				for (Enchantment ench : Enchantment.REGISTRY)
					enchants.add(ench.getRegistryName().toString() + System.lineSeparator());
				
				Collections.sort(enchants);
				
				for(String ench : enchants)
					writer.append(ench);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return this;

	}

}
