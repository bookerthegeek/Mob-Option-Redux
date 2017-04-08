package com.bookerthegeek.moboptions.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;

import com.bookerthegeek.moboptions.MobOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JSONEquipmentReader {

	public static File jsonLoc = new File(MobOptions.INSTANCE.getConfigFolder() + "/equipment.json");

	public static void makeEquipmentJSON() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		TierHandler handler = new TierHandler();

		// Leather set
		SetHandler leatherSet = new SetHandler(Items.LEATHER_HELMET.getRegistryName().toString() + "#0",
				Items.LEATHER_CHESTPLATE.getRegistryName().toString() + "#0",
				Items.LEATHER_LEGGINGS.getRegistryName().toString() + "#0",
				Items.LEATHER_BOOTS.getRegistryName().toString() + "#0",
				Items.WOODEN_SWORD.getRegistryName().toString() + "#0", "", true);

		// Chainmail set
		SetHandler chainSet = new SetHandler(Items.CHAINMAIL_HELMET.getRegistryName().toString() + "#0",
				Items.CHAINMAIL_CHESTPLATE.getRegistryName().toString() + "#0",
				Items.CHAINMAIL_LEGGINGS.getRegistryName().toString() + "#0",
				Items.CHAINMAIL_BOOTS.getRegistryName().toString() + "#0",
				Items.STONE_SWORD.getRegistryName().toString() + "#0", "", true);

		// Gold set
		SetHandler goldSet = new SetHandler(Items.GOLDEN_HELMET.getRegistryName().toString() + "#0",
				Items.GOLDEN_CHESTPLATE.getRegistryName().toString() + "#0",
				Items.GOLDEN_LEGGINGS.getRegistryName().toString() + "#0",
				Items.GOLDEN_BOOTS.getRegistryName().toString() + "#0",
				Items.GOLDEN_SWORD.getRegistryName().toString() + "#0", "", true);

		// Iron set
		SetHandler ironSet = new SetHandler(Items.IRON_HELMET.getRegistryName().toString() + "#0",
				Items.IRON_CHESTPLATE.getRegistryName().toString() + "#0",
				Items.IRON_LEGGINGS.getRegistryName().toString() + "#0",
				Items.IRON_BOOTS.getRegistryName().toString() + "#0",
				Items.IRON_SWORD.getRegistryName().toString() + "#0", "", true);

		// Diamond set
		SetHandler diamondSet = new SetHandler(Items.DIAMOND_HELMET.getRegistryName().toString() + "#0",
				Items.DIAMOND_CHESTPLATE.getRegistryName().toString() + "#0",
				Items.DIAMOND_LEGGINGS.getRegistryName().toString() + "#0",
				Items.DIAMOND_BOOTS.getRegistryName().toString() + "#0",
				Items.DIAMOND_SWORD.getRegistryName().toString() + "#0",
				Items.SHIELD.getRegistryName().toString() + "#0", true);

		handler.addToList(1, leatherSet).addToList(1, leatherSet).addToList(2, chainSet).addToList(3, goldSet)
				.addToList(4, ironSet).addToList(5, diamondSet);

		if (!jsonLoc.exists()) {
			try {
				jsonLoc.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try (Writer writer = new FileWriter(jsonLoc)) {
				gson.toJson(handler, writer);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static TierHandler getHandler() {
		Gson gson = new Gson();
		TierHandler handler = new TierHandler();
		try {
			handler = gson.fromJson(new FileReader(jsonLoc), TierHandler.class);
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return handler;
	}

	public static ArrayList<SetHandler> getSetsInTier(int tier) {
		TierHandler handler = getHandler();
		switch (tier) {
		case 1:
			return handler.tier1;
		case 2:
			return handler.tier2;
		case 3:
			return handler.tier3;
		case 4:
			return handler.tier4;
		case 5:
			return handler.tier5;
		default:
			return new ArrayList<SetHandler>();
		}
	}

	public static SetHandler getSet(int tier) {
		ArrayList<SetHandler> sets = getSetsInTier(tier);
		SetHandler set = sets.get(new Random().nextInt(sets.size()));
		return set;
	}

	public static SetHandler dressEntity(int tier, EntityMob mob) {
		SetHandler set = getSet(tier);

		if (!set.helmet.isEmpty() && mob.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null)
			mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, getStackFromString(set.helmet));

		if (!set.chestplate.isEmpty() && mob.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == null)
			mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, getStackFromString(set.chestplate));

		if (!set.leggings.isEmpty() && mob.getItemStackFromSlot(EntityEquipmentSlot.LEGS) == null)
			mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, getStackFromString(set.leggings));

		if (!set.boots.isEmpty() && mob.getItemStackFromSlot(EntityEquipmentSlot.FEET) == null)
			mob.setItemStackToSlot(EntityEquipmentSlot.FEET, getStackFromString(set.boots));

		if (!set.mainHand.isEmpty() && mob.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == null)
			mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getStackFromString(set.mainHand));

		if (!set.offHand.isEmpty() && mob.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND) == null)
			mob.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, getStackFromString(set.offHand));

		return set;

	}

	public static ItemStack getStackFromString(String string) {
		String[] values = string.split("#");
		Item item = Item.getByNameOrId(values[0]);
		int meta = Integer.parseInt(values[1]);
		return new ItemStack(item, 1, meta);
	}

}
