package moboptions;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

@Mod(modid = "mobopt", version = "0.9")
public class MobOptions {
	
	@Instance ("mobopt")
	public static MobOptions instance;	
	public static int spawnRadius;
	public static int underground;
	public static boolean spawnWhitelist;
	public static float undergroundChance;
	public static float depthMultiplier;
	public static float depthOffset;
	public static int distanceDifficulty;
	public static float probDiamond;
	public static float probIron;
	public static float probGold;
	public static float probChain;
	public static float probLeather;
	public static float enchantCost;	
	public static int defaultDim;
	public static HashMap<Integer, DimensionMobOpt> dimData;
	public static float maxSpeed;
	public static float speedGain;
	public static int maxRange;
	public static float rangeGain;
	public static float attackGain;	
	public static float weaponDrop;
	public static float armorDrop;
	public static List mobsToSpawn;
	public static Random rand = new Random();
	
	//
	// CONFIG
	//
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		underground = config.getInt("Sea Level", "Underground Spawning", 50, 0, 255, "Mobs will spawn more often and are unaffected by light below this Y coordinate.");
		undergroundChance = config.getFloat("Spawn Chance", "Underground Spawning", .01F, 0F, 1F, "Chance to spawn mobs under sea level. Careful!");
		spawnWhitelist = config.getBoolean("Use Whitelist", "Underground Spawning", true, "If set to false Mobs to spawn will act as a blacklist.");
		String defaultMobsToSpawn = "Zombie, Skeleton, Creeper, Spider";
		defaultMobsToSpawn = config.getString("Mobs to Spawn", "Underground Spawning", defaultMobsToSpawn, "Comma seperated list of mobs that will spawn under sea level ignoring light.");
		mobsToSpawn = Arrays.asList(defaultMobsToSpawn.replace(" ","").split(","));
		
		spawnRadius = config.getInt("Spawn Area", "Difficulty Settings", 400, 0, 10000, "Radius around 0,0 to not buff mobs by distance (will still buff from height).");
		depthMultiplier = config.getFloat("Depth Multipler", "Difficulty Settings", -.1F, -10F, 10F, "How quickly will mobs get easier or harder as y increases. (negative is easier)");
		depthOffset = config.getFloat("Depth Offset", "Difficulty Settings", 5F, 0F, 1000F, "Mob Strength at y=0");
		distanceDifficulty = config.getInt("Distance Difficulty", "Difficulty Settings", 200, 0, 1000, "Distance / Difficulty (Lower is harder!)(2000/200 mobs will be level 10)");	
		attackGain = config.getFloat("Attack Gain", "Difficulty Settings", 0.5F, 0F, 5F, "How much (percentage) attack damage mobs gain approx every two levels.");
		
		probDiamond = config.getFloat("Diamond Chance", "Armor and Weapons", .01F, 0F, 1F, "Percent a mob will get diamond gear per 2 levels approx.");
		probIron = config.getFloat("Iron Chance", "Armor and Weapons", .01F, 0F, 1F, "Percent a mob will get iron gear per 2 levels approx.");
		probGold = config.getFloat("Gold Chance", "Armor and Weapons", .02F, 0F, 1F, "Percent a mob will get gold gear per 2 levels approx.");
		probChain = config.getFloat("Chain Chance", "Armor and Weapons", .04F, 0F, 1F, "Percent a mob will get chain / stone gear per 2 levels approx.");
		probLeather = config.getFloat("Leather Chance", "Armor and Weapons", .06F, 0F, 1F, "Percent a mob will get leather / wood gear per 2 levels approx.");	
		weaponDrop = config.getFloat("Weapon Drop", "Armor and Weapons", .00F, 0F, 1F, "Percent a mob will drop its weapon");
		armorDrop = config.getFloat("Armor Drop", "Armor and Weapons", .01F, 0F, 1F, "Percent a mob will drop a piece of armor");
		
		enchantCost = config.getFloat("Enchantment Cost", "Enchantments", 2F, 1F, 100F, "How many levels does it take for a mob to gain an enchantment?");	
		
		maxSpeed = config.getFloat("Creeper Max Speed", "Mob Settings", .4F, 0F, 5F, "Max creeper speed. (base speed is .25");
		speedGain = config.getFloat("Creeper Speed Gain", "Mob Settings", .01F, 0F, .5F, "Approx. creeper speed gain per 2 levels.");
		maxRange = config.getInt("Range Max", "Mob Settings", 40, 16, 100, "Max mob tracking range. (base is 16 for most)");
		rangeGain = config.getFloat("Range Gain", "Mob Settings", 1F, 0F, 10F, "Approx. tracking range gain per 2 levels.");

		dimData = new HashMap<Integer, DimensionMobOpt>();
		String rawDimData = "0, 1, 25, 400, 40 : 7, 5, 30, 500, 50 : -1, 10, 35, 600, 60 : 1, 20, 40, 2000, 75";
		rawDimData = config.getString("Dimension Difficulty", "Dimension Settings", rawDimData, "Difficulty settings by dimension. (Dimension ID, Base difficulty, Max difficulty, Health cap, Damage cap) Seperate dimensions with ':', each dimension MUST have 5 values.");
		defaultDim = config.getInt("Default Dimension", "Dimension Settings", 0, -10000, 10000, "If a dimension is not specified use the settings of this dimension");
		for (String thisDim : rawDimData.replace(" ","").split(":")) {
		    String[] thisDimData = thisDim.split(",");
		    dimData.put(Integer.parseInt(thisDimData[0]), new DimensionMobOpt(Integer.parseInt(thisDimData[1]), Integer.parseInt(thisDimData[2]), Integer.parseInt(thisDimData[3]), Integer.parseInt(thisDimData[4])));	    
		}
		
		config.save();	
	}
	
	//
	// INIT
	//
	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	//
	// SPAWNING
	//
	@SubscribeEvent(receiveCanceled = false)
	public void onSpawn(LivingSpawnEvent.CheckSpawn e){
		if(e.entity instanceof EntityMob && e.y < underground){
			EntityMob thisMob = (EntityMob) e.entity;
			String mobName = EntityList.getEntityString(thisMob);
			if((spawnWhitelist && mobsToSpawn.contains(mobName)) || (!spawnWhitelist && !mobsToSpawn.contains(mobName))){
				if (rand.nextFloat() < undergroundChance && !(e.world.difficultySetting == EnumDifficulty.PEACEFUL)){
					if(e.world.checkNoEntityCollision(thisMob.boundingBox) && e.world.getCollidingBoundingBoxes(thisMob, thisMob.boundingBox).isEmpty() && !e.world.isAnyLiquid(thisMob.boundingBox)){
						e.setResult(Result.ALLOW);
					}									
				}
			}
		}
	}
	
	//
	// MOB OPTIONS
	//
	@SubscribeEvent(receiveCanceled = false)
	public void onSpawned(EntityJoinWorldEvent e){		
		if(e.entity instanceof EntityMob){	
			if(!e.entity.getEntityData().hasKey("MobOptAtt")){
								
				//
				// Difficulty settings
				//	
				int dim = e.world.provider.dimensionId;	
				if(!dimData.containsKey(dim)){
					if(dimData.containsKey(defaultDim)){
						dim = defaultDim;
					}else{
						return;
					}
				}

				EntityMob thisMob = (EntityMob) e.entity;				
				double defValue, attValue;
				int intEnchants;
				double depthMult = (depthMultiplier * thisMob.posY + depthOffset);
				depthMult = Math.max(depthMult, 0);
				double distance = thisMob.getDistance(0, thisMob.posY, 0);
				double distMult;
				if(dim == 0 && distance < spawnRadius){
					distMult = 0;
				} else {
					if (distanceDifficulty < 1){
						distMult =  0;
					}else{
						distMult =  (distance - spawnRadius) / distanceDifficulty;
					}				
				}			
				float ratio = (rand.nextInt(5) + 3) / 10.0F;
				defValue = (dimData.get(dim).dimBaseDifficulty + ratio * (depthMult + distMult));
				defValue = Math.min(defValue, dimData.get(dim).dimMaxDifficulty);
				attValue = (dimData.get(dim).dimBaseDifficulty + (1 - ratio) * (depthMult + distMult));
				attValue = Math.min(attValue, dimData.get(dim).dimMaxDifficulty);
				intEnchants = (int) Math.floor(attValue / enchantCost);
				thisMob.getEntityData().setDouble("MobOptAtt", attValue);
				thisMob.getEntityData().setDouble("MobOptDef", defValue);		
				thisMob.getEntityData().setInteger("MobOptEnch", intEnchants);
				
				//
				// Health and attack Damage
				//	
				int baseHealth = (int) thisMob.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
				int health = (int)((defValue) * baseHealth);
				health = Math.min(health, dimData.get(dim).dimHealthCap);
				health = Math.max(health, baseHealth);
				thisMob.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(health);
				thisMob.setHealth(health);
				
				int baseDamage = (int)thisMob.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
				int damage = (int)(baseDamage + (attValue * attackGain) * baseDamage);
				damage = Math.min(damage, dimData.get(dim).dimDamageCap);
				damage = Math.max(damage, baseDamage);
				thisMob.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(damage);
				
				int baseRange = (int)thisMob.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
				int range = (int)((attValue * rangeGain) + baseRange);
				range = Math.min(range, maxRange);
				range = Math.max(range, baseRange);
				thisMob.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(range);
			
				//
				// Armor
				//
				if (thisMob.getEquipmentInSlot(1) == null){					
					if(rand.nextFloat() < defValue * probDiamond){
						thisMob.setCurrentItemOrArmor(1, new ItemStack(Items.diamond_helmet));
					} else if (rand.nextFloat() < defValue * probIron){
						thisMob.setCurrentItemOrArmor(1, new ItemStack(Items.iron_helmet));
					} else if (rand.nextFloat() < defValue * probGold){							
						thisMob.setCurrentItemOrArmor(1, new ItemStack(Items.golden_helmet));
					} else if (rand.nextFloat() < defValue * probChain){
						thisMob.setCurrentItemOrArmor(1, new ItemStack(Items.chainmail_helmet));
					} else if (rand.nextFloat() < defValue * probLeather){
						thisMob.setCurrentItemOrArmor(1, new ItemStack(Items.leather_helmet));
					}
					thisMob.setEquipmentDropChance(1, armorDrop);
				}
				if (thisMob.getEquipmentInSlot(2) == null){				
					if(rand.nextFloat() < defValue * probDiamond){
						thisMob.setCurrentItemOrArmor(2, new ItemStack(Items.diamond_chestplate));
					} else if (rand.nextFloat() < defValue * probIron){
						thisMob.setCurrentItemOrArmor(2, new ItemStack(Items.iron_chestplate));
					} else if (rand.nextFloat() < defValue * probGold){							
						thisMob.setCurrentItemOrArmor(2, new ItemStack(Items.golden_chestplate));
					} else if (rand.nextFloat() < defValue * probChain){
						thisMob.setCurrentItemOrArmor(2, new ItemStack(Items.chainmail_chestplate));
					} else if (rand.nextFloat() < defValue * probLeather){
						thisMob.setCurrentItemOrArmor(2, new ItemStack(Items.leather_chestplate));
					}
					thisMob.setEquipmentDropChance(2, armorDrop);
				}
				if (thisMob.getEquipmentInSlot(3) == null){				
					if(rand.nextFloat() < defValue * probDiamond){
						thisMob.setCurrentItemOrArmor(3, new ItemStack(Items.diamond_leggings));
					} else if (rand.nextFloat() < defValue * probIron){
						thisMob.setCurrentItemOrArmor(3, new ItemStack(Items.iron_leggings));
					} else if (rand.nextFloat() < defValue * probGold){							
						thisMob.setCurrentItemOrArmor(3, new ItemStack(Items.golden_leggings));
					} else if (rand.nextFloat() < defValue * probChain){
						thisMob.setCurrentItemOrArmor(3, new ItemStack(Items.chainmail_leggings));
					} else if (rand.nextFloat() < defValue * probLeather){
						thisMob.setCurrentItemOrArmor(3, new ItemStack(Items.leather_leggings));
					}
					thisMob.setEquipmentDropChance(3, armorDrop);
				}
				if (thisMob.getEquipmentInSlot(4) == null){				
					if(rand.nextFloat() < defValue * probDiamond){
						thisMob.setCurrentItemOrArmor(4, new ItemStack(Items.diamond_boots));
					} else if (rand.nextFloat() < defValue * probIron){
						thisMob.setCurrentItemOrArmor(4, new ItemStack(Items.iron_boots));
					} else if (rand.nextFloat() < defValue * probGold){							
						thisMob.setCurrentItemOrArmor(4, new ItemStack(Items.golden_boots));
					} else if (rand.nextFloat() < defValue * probChain){
						thisMob.setCurrentItemOrArmor(4, new ItemStack(Items.chainmail_boots));
					} else if (rand.nextFloat() < defValue * probLeather){
						thisMob.setCurrentItemOrArmor(4, new ItemStack(Items.leather_boots));
					}
					thisMob.setEquipmentDropChance(4, armorDrop);
				}					
							
				//
				// Unique things for different mobs
				//	
				if (EntityList.getEntityString(thisMob).equals("Zombie")){
					// Zombie
					if (thisMob.getEquipmentInSlot(0) == null){					
						if(rand.nextFloat() < attValue * probDiamond){
							thisMob.setCurrentItemOrArmor(0, new ItemStack(Items.diamond_sword));
						} else if (rand.nextFloat() < attValue * probIron){
							thisMob.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
						} else if (rand.nextFloat() < attValue * probGold){							
							thisMob.setCurrentItemOrArmor(0, new ItemStack(Items.golden_sword));
						} else if (rand.nextFloat() < attValue * probChain){
							thisMob.setCurrentItemOrArmor(0, new ItemStack(Items.stone_sword));
						} else if (rand.nextFloat() < attValue * probLeather){
							thisMob.setCurrentItemOrArmor(0, new ItemStack(Items.wooden_sword));
						}	
						thisMob.setEquipmentDropChance(0, weaponDrop);
					}
				} else if(EntityList.getEntityString(thisMob).equals("Creeper")){
					// Creeper
					double speed = ((attValue * speedGain) + thisMob.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
					speed = Math.min(maxSpeed, speed);
					thisMob.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(speed);
				} else if(EntityList.getEntityString(thisMob).equals("Skeleton")){
					thisMob.setEquipmentDropChance(0, weaponDrop);
				}		
			}
		}
	}

	//
	// ENCHANTMENTS
	//
	@SubscribeEvent(receiveCanceled = false)
	public void onAfterSpawn(LivingSpawnEvent.SpecialSpawn e){		
		if(e.entity instanceof EntityMob){	
			EntityMob thisMob = (EntityMob) e.entity;	
			thisMob.onSpawnWithEgg((IEntityLivingData)null);		
			if(e.entity.getEntityData().hasKey("MobOptEnch")){			
				int numEnchants = thisMob.getEntityData().getInteger("MobOptEnch");		
				//
				// Enchant weapon if they have one
				//
				if(thisMob.getEquipmentInSlot(0) != null){
					ItemStack weapon = thisMob.getEquipmentInSlot(0);	
					// If bow
					if(Enchantment.power.canApply(weapon)){
						int power = 0, punch = 0, flame = 0;
						while (numEnchants > 0){
							int enchant = rand.nextInt(3) + 1;
							switch (enchant){
							case 1:	power++;
							case 2: punch++;
							case 3: flame++;
							}
							numEnchants--;
						}					
						if(power > 5){
							power = 5;
						}
						if(punch > 2){
							punch = 2;
						}
						if(flame > 1){
							flame = 1;
						}				
						weapon.addEnchantment(Enchantment.power, power);
						weapon.addEnchantment(Enchantment.punch, punch);
						weapon.addEnchantment(Enchantment.flame, flame);
					// If Sword
						
					}else if(Enchantment.sharpness.canApply(weapon)){
						int sharp = 0, knock = 0, fire = 0;
						while (numEnchants > 0){
							int enchant = rand.nextInt(3) + 1;
							switch (enchant){
							case 1:	sharp++;
							case 2: knock++;
							case 3: fire++;
							}
							numEnchants--;
						}					
						if(sharp > 5){
							sharp = 5;
						}
						if(knock > 2){
							knock = 2;
						}
						if(fire > 2){
							fire = 2;
						}				
						weapon.addEnchantment(Enchantment.sharpness, sharp);
						weapon.addEnchantment(Enchantment.knockback, knock);
						weapon.addEnchantment(Enchantment.fireAspect, fire);
					}	
				}
			}
		}
		e.setCanceled(true);
	}
	
}