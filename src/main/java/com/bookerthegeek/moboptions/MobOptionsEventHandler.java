package com.bookerthegeek.moboptions;

import static com.bookerthegeek.moboptions.ConfigHandler.mobsToSpawn;
import static com.bookerthegeek.moboptions.ConfigHandler.rand;
import static com.bookerthegeek.moboptions.ConfigHandler.spawnWhitelist;
import static com.bookerthegeek.moboptions.ConfigHandler.underground;
import static com.bookerthegeek.moboptions.ConfigHandler.undergroundChance;

import java.util.UUID;

import com.bookerthegeek.moboptions.Capabilities.CapabilityStats;
import static com.bookerthegeek.moboptions.ConfigHandler.*;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobOptionsEventHandler {

	/**
	 * Checks spawning
	 */
	@SubscribeEvent
	public void onSpawn(CheckSpawn event) {
		if (!(event.getEntity() instanceof EntityMob && event.getY() < underground))
			return;

		EntityMob mob = (EntityMob) event.getEntity();
		String name = EntityList.getEntityString(mob);

		if ((spawnWhitelist && mobsToSpawn.contains(name))
				|| (!(spawnWhitelist && mobsToSpawn.contains(name))))
			if (rand.nextFloat() < undergroundChance
					&& event.getWorld().getDifficulty() != EnumDifficulty.PEACEFUL)
				if (event.getWorld().checkNoEntityCollision(mob.getEntityBoundingBox())
						&& event.getWorld().getCollisionBoxes(mob, mob.getEntityBoundingBox()).isEmpty()
						&& !event.getWorld().containsAnyLiquid(mob.getEntityBoundingBox()))
					event.setResult(Result.ALLOW);

	}

	@SubscribeEvent
	public void afterSpawn(EntityJoinWorldEvent event) {
		if (!(event.getEntity() instanceof EntityMob) || event.getEntity() instanceof EntityPlayer)
			return;

		EntityMob mob = (EntityMob) event.getEntity();

		//if(mob.hasCapability(StatsCapability.MOB_OPTIONS_STATS_CAP, StatsCapability.DEFAULT_FACE)){
			//System.out.println(mob.getName());
			//mob.getCapability(StatsCapability.MOB_OPTIONS_STATS_CAP, StatsCapability.DEFAULT_FACE);
		//}

		if (!mob.getEntityData().hasKey("MobOptionsAtt")) {
			
			
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
			double distance = mob.getDistance(0, mob.posY, 0);
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
			intEnchants = (int) Math.floor(attackVal/enchantCost);
			
			if(mob.hasCapability(CapabilityStats.MAX_HEALTH_CAPABILITY, CapabilityStats.DEFAULT_FACING)){
				
				int baseHealth = (int) mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
				int health = (int)(defenceVal * baseHealth);
				health = Math.min(health, dimData.get(dim).dimHealthCap);
				health = Math.max(health, baseHealth);
				
				mob.getCapability(CapabilityStats.MAX_HEALTH_CAPABILITY, CapabilityStats.DEFAULT_FACING).addBonusMaxHealth(health);
			}
			
			
			//mob.getEntityData().setDouble("MobOptionsAtt", attackVal);
			//mob.getEntityData().setDouble("MobOptionsDef", defenceVal);
			//mob.getEntityData().setInteger("MobOptionsEnch", intEnchants);
		}
			
			/*
			 * Health and Attack damage
			 *
			
			int baseHealth = (int) mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
			int health = (int)(defenceVal * baseHealth);
			health = Math.min(health, dimData.get(dim).dimHealthCap);
			health = Math.max(health, baseHealth);
			mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(health);
			mob.setHealth(health);
			
			int baseDamage = (int)mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
			int damage = (int)(baseDamage + (attackVal * attackGain) * baseDamage);
			damage = Math.min(damage, dimData.get(dim).dimDamageCap);
			damage = Math.max(damage,  baseDamage);
			mob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);
			
			int baseRange = (int)mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
			int range = (int)((attackVal * rangeGain) + baseRange);
			range = Math.min(range, maxRange);
			range = Math.max(range, baseRange);
			mob.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(range);
			
			/*
			 * Armour
			 *
			if(mob.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null){
				if(rand.nextFloat() < defenceVal * probDiamond)
					mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
				
				else if(rand.nextFloat() < defenceVal * probIron)
					mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
				
				else if(rand.nextFloat() < defenceVal * probGold)
					mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
				
				else if(rand.nextFloat() < defenceVal * probChain)
					mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
				
				else if(rand.nextFloat() < defenceVal * probLeather)
					mob.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			}
			
			
			if(mob.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == null){
				if(rand.nextFloat() < defenceVal * probDiamond)
					mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
				
				else if(rand.nextFloat() < defenceVal * probIron)
					mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
				
				else if(rand.nextFloat() < defenceVal * probGold)
					mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
				
				else if(rand.nextFloat() < defenceVal * probChain)
					mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
				
				else if(rand.nextFloat() < defenceVal * probLeather)
					mob.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
			}
			
			
			if(mob.getItemStackFromSlot(EntityEquipmentSlot.LEGS) == null){
				if(rand.nextFloat() < defenceVal * probDiamond)
					mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
				
				else if(rand.nextFloat() < defenceVal * probIron)
					mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
				
				else if(rand.nextFloat() < defenceVal * probGold)
					mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
				
				else if(rand.nextFloat() < defenceVal * probChain)
					mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
				
				else if(rand.nextFloat() < defenceVal * probLeather)
					mob.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
			}
			
			
			if(mob.getItemStackFromSlot(EntityEquipmentSlot.FEET) == null){
				if(rand.nextFloat() < defenceVal * probDiamond)
					mob.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
				
				else if(rand.nextFloat() < defenceVal * probIron)
					mob.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
				
				else if(rand.nextFloat() < defenceVal * probGold)
					mob.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
				
				else if(rand.nextFloat() < defenceVal * probChain)
					mob.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
				
				else if(rand.nextFloat() < defenceVal * probLeather)
					mob.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
			}
			
			for(EntityEquipmentSlot slot : EntityEquipmentSlot.values())
				mob.setDropChance(slot, armourDrop);
			
			
			/*
			 * Unique things for different mobs
			 *
			if(mob instanceof EntityZombie){
				
				if(mob.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) == null){
					
					if(rand.nextFloat() < attackVal * probDiamond)
						mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
					
					else if(rand.nextFloat() < attackVal * probIron)
						mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
					
					else if(rand.nextFloat() < attackVal * probGold)
						mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
					
					else if(rand.nextFloat() < attackVal * probChain)
						mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
					
					else if(rand.nextFloat() < attackVal * probLeather)
						mob.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.WOODEN_SWORD));
					
					mob.setDropChance(EntityEquipmentSlot.MAINHAND, weaponDrop);
				}
			}
			else if(mob instanceof EntityCreeper){
				
				double speed = ((attackVal * speedGain) + mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				speed = Math.min(speed, maxSpeed);
				mob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(speed);
			}
			else if(mob instanceof EntitySkeleton){
				mob.setDropChance(EntityEquipmentSlot.MAINHAND, weaponDrop);
			}
			
		}
		*/
	}
	
	@SubscribeEvent
	public void afterAfterSpawn(SpecialSpawn event){
		if(!(event.getEntity() instanceof EntityMob))
			return;
		
		EntityMob mob = (EntityMob) event.getEntity();
		mob.onInitialSpawn(event.getWorld().getDifficultyForLocation(new BlockPos(mob)), (IEntityLivingData)null);
		
		if(mob.getEntityData().hasKey("MobOptionsEnch")){
			int numEnchants = mob.getEntityData().getInteger("MobOptionsEnch");
			
			/*
			 * Enchant weapon, if one exists
			 */
			if(mob.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND) != null){
				ItemStack hand = mob.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
				
				//if bow
				if(Enchantments.POWER.canApply(hand)){
					int power = 0, punch = 0, flame = 0;
					while(numEnchants > 0){
						int enchant = rand.nextInt(3)+1;
						switch(enchant){
						case 1: power++; break;
						case 2: punch++; break;
						case 3: flame++; break;
						}
						numEnchants--;
					}
					if(power > 5)
						power = 5;
					
					if(punch > 2)
						punch = 2;
					
					if(flame > 1)
						flame = 1;
					
					hand.addEnchantment(Enchantments.POWER, power);
					hand.addEnchantment(Enchantments.PUNCH, punch);
					hand.addEnchantment(Enchantments.FLAME, flame);
				}
				//if sword
				else if(Enchantments.SHARPNESS.canApply(hand)){
					int sharp = 0, knock = 0, fire = 0;
					while(numEnchants > 0){
						int enchant = rand.nextInt(3) + 1;
						switch(enchant){
						case 1: sharp++; break;
						case 2: knock++; break;
						case 3: fire++; break;
						}
						numEnchants--;
					}
					if(sharp > 5)
						sharp = 5;
					
					if(knock > 2)
						knock = 2;
					
					if(fire > 2)
						fire = 2;
					
					hand.addEnchantment(Enchantments.SHARPNESS, sharp);
					hand.addEnchantment(Enchantments.KNOCKBACK, knock);
					hand.addEnchantment(Enchantments.FIRE_ASPECT, fire);
				}
			}
		}
	}

}
