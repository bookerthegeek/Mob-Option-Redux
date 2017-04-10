package com.bookerthegeek.moboptions;

import static com.bookerthegeek.moboptions.ConfigHandler.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.bookerthegeek.moboptions.Capabilities.stats.CapabilityStats;
import com.bookerthegeek.moboptions.enchantment.EnchantmentHolder;
import com.bookerthegeek.moboptions.enchantment.EnchantmentList;
import com.bookerthegeek.moboptions.equipment.JSONEquipmentReader;
import com.bookerthegeek.moboptions.equipment.SetHandler;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobOptionsEventHandler {
	
	EnchantmentList enchants;

	/**
	 * Checks spawning
	 */
	@SubscribeEvent
	public void onSpawn(CheckSpawn event) {
		//If the entity is not a mob or above the forced-spawn-y-level, stop
		if (!(event.getEntity() instanceof EntityMob && event.getY() < underground))
			return;

		EntityMob mob = (EntityMob) event.getEntity();
		String name = EntityList.getEntityString(mob);

		//If whitelist and list contains mob, or blacklist and list does not contain mob
		if ((spawnWhitelist && mobsToSpawn.contains(name)) || (!(spawnWhitelist && mobsToSpawn.contains(name)))){
			//If the chance is within allowed mob-spawn-under-y-level range, and the game is not on Peaceful
			if (rand.nextFloat() < undergroundChance && event.getWorld().getDifficulty() != EnumDifficulty.PEACEFUL){
				//If there are no other entities in this area, and there are no collisions, and there is not a liquid
				if (event.getWorld().checkNoEntityCollision(mob.getEntityBoundingBox())
						&& event.getWorld().getCollisionBoxes(mob, mob.getEntityBoundingBox()).isEmpty()
						&& !event.getWorld().containsAnyLiquid(mob.getEntityBoundingBox())){
					//Allow the spawn
					event.setResult(Result.ALLOW);
				}
			}
		}

	}

	@SubscribeEvent
	public void afterSpawn(EntityJoinWorldEvent event) {
		//If the entity is not a mob, or if it is a player, or if we are on the client, stop
		if (!(event.getEntity() instanceof EntityMob) || event.getEntity() instanceof EntityPlayer || event.getWorld().isRemote)
			return;

		EntityMob mob = (EntityMob) event.getEntity();
		
		/*
		 * Edits Difficulty settings
		 */
		int dim = event.getWorld().provider.getDimension();
		if (!dimData.containsKey(dim))
			if (dimData.containsKey(defaultDim))
				dim = defaultDim;
			else
				return;

		double defenceVal, attackVal;
		int intEnchants;
		double depthMult = (depthMultiplier * mob.posY + depthOffset);
		depthMult = Math.max(depthMult, 0);
		int x = mob.worldObj.getSpawnPoint().getX();
		int z = mob.worldObj.getSpawnPoint().getZ();
		if(!ConfigHandler.spawnLocation){
			x = 0;
			z = 0;
		}
		double distance = mob.getDistance(x, mob.posY, z);
		double distMult;
		if (dim == 0 && distance < spawnRadius)
			distMult = 0;
		else if (distanceDifficulty < 1)
			distMult = 0;
		else
			distMult = (distance - spawnRadius) / distanceDifficulty;

		float ratio = (rand.nextInt(3) + 3) / 10.0F;

		defenceVal = (dimData.get(dim).dimBaseDifficulty + ratio * (depthMult + distMult));
		defenceVal = Math.min(defenceVal, dimData.get(dim).dimMaxDifficulty);
		attackVal = (dimData.get(dim).dimBaseDifficulty + (1 - ratio) * (depthMult + distMult));
		attackVal = Math.min(attackVal, dimData.get(dim).dimMaxDifficulty);
		intEnchants = (int) Math.floor(attackVal / enchantCost);

		//if the mob has the health-capability, edit health
		if (mob.hasCapability(CapabilityStats.MAX_HEALTH_CAPABILITY, CapabilityStats.DEFAULT_FACING)) {

			int baseHealth = (int) mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
			int health = (int) (defenceVal * baseHealth);
			health = Math.min(health, dimData.get(dim).dimHealthCap);
			health = Math.max(health, baseHealth);

			mob.getCapability(CapabilityStats.MAX_HEALTH_CAPABILITY, CapabilityStats.DEFAULT_FACING)
					.addBonusMaxHealth(health);
		}

		//if the mob has the attack-capability, edit attack
		if (mob.hasCapability(CapabilityStats.ATTACK_CAPABILITY, CapabilityStats.DEFAULT_FACING)) {

			int baseDamage = (int) mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
			int damage = (int) (baseDamage + (attackVal * attackGain) * baseDamage);
			damage = Math.min(damage, dimData.get(dim).dimDamageCap);
			damage = Math.max(damage, baseDamage);

			mob.getCapability(CapabilityStats.ATTACK_CAPABILITY, CapabilityStats.DEFAULT_FACING)
					.addBonusMaxAttack(damage);
		}

		//if the mob has the follow-range capability, edit follow-range
		if (mob.hasCapability(CapabilityStats.FOLLOW_RANGE_CAPABILITY, CapabilityStats.DEFAULT_FACING)) {

			int baseRange = (int) mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
			int range = (int) ((attackVal * rangeGain) + baseRange);
			range = Math.min(range, maxRange);
			range = Math.max(range, baseRange);

			mob.getCapability(CapabilityStats.FOLLOW_RANGE_CAPABILITY, CapabilityStats.DEFAULT_FACING)
					.addBonusFollowRange(range);
		}

		//if the entity is within the safe-zone, stop
		if (distMult == 0)
			return;

		//Make sure that the entity will at least get tier-1 equipment
		int tier = 1;
		float[] chances = new float[] { probTier1, probTier2, probTier3, probTier4, probTier5 };

		//Calculate the chances for getting a tier
		for (int i = 0; i < chances.length; i++) {
			if (rand.nextFloat() < defenceVal * chances[i]){
				tier = i + 1;
				break;
			}
		}
		
		//Dresses the entity with a set, and returns set for drop-chance logic
		SetHandler set = JSONEquipmentReader.dressEntity(tier, mob);
		float dropChance = armourDrop;

		//If the set does not allow pieces to drop, set chance to 0
		if (!set.setPartsCanDrop)
			dropChance = 0.0F;

		//Set the drop-chance for each piece of equipment
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
			mob.setDropChance(slot, dropChance);
		
		
		//getting all enchants from file, ONCE
		if(enchants == null)
			enchants = MobOptions.INSTANCE.enchants.list;
		
		//Getting the enchants for the current tier
		ArrayList<EnchantmentHolder> possible = enchants.getEnchantsForTier(tier);
		
		//For each possible equipment slot
		for(EntityEquipmentSlot piece : EntityEquipmentSlot.values()){
			
			//If the mob has something in said slot
			if(mob.getItemStackFromSlot(piece) != null){
				
				//List of enchants that has been used on this item
				List<Enchantment> used = new ArrayList<Enchantment>();
				
				//How many times an enchantment has been checked against the item
				int tries = 0;
				
				//Only allow max 10 tries. Otherwise, if a tier doesn't have something appropriate for the item, it will try forever, and ever, and ever...
				for(;tries < 10; tries++){
					
					//Get a random enchant-value from the tier's possible enchants
					EnchantmentHolder held = possible.get(rand.nextInt(possible.size()));
					
					int chance = rand.nextInt(100);
					//Skip if above allowed apply-chance
					if(!(chance < held.chanceToApply))
						continue;
					
					
					//converting to actual enchant
					Enchantment enchant = Enchantment.getEnchantmentByLocation(held.enchantment);
					//if the enchant cannot be used on this item, or we have already used this enchant, skip
					if(!enchant.canApply(mob.getItemStackFromSlot(piece)) || used.contains(enchant))
						continue;
					
					//put the enchant on the item, with a random value between min & max
					mob.getItemStackFromSlot(piece).addEnchantment(enchant, ThreadLocalRandom.current().nextInt(held.minLvl, held.maxLvl + 1));
					used.add(enchant);
				}
				//Sent packet to everyone tracking this entity, updating the new equipment
				SPacketEntityEquipment equipPacket= new SPacketEntityEquipment(mob.getEntityId(), piece, mob.getItemStackFromSlot(piece));
				((WorldServer) mob.getEntityWorld()).getEntityTracker().sendToTrackingAndSelf(mob, equipPacket);
			}
		}
	}

}
