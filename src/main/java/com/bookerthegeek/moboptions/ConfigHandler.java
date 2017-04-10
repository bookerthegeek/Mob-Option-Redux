package com.bookerthegeek.moboptions;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

	public static int spawnRadius;
	public static boolean spawnLocation;
	public static int underground;
	public static boolean spawnWhitelist;
	public static float undergroundChance;
	public static float depthMultiplier;
	public static float depthOffset;
	public static int distanceDifficulty;
	public static float probTier5;
	public static float probTier4;
	public static float probTier3;
	public static float probTier2;
	public static float probTier1;
	public static float enchantCost;
	public static int defaultDim;
	public static HashMap<Integer, DimensionMobOptions> dimData;
	public static float maxSpeed;
	public static float speedGain;
	public static int maxRange;
	public static float rangeGain;
	public static float armourPerc;
	public static float armourToughPerc;
	public static float attackGain;
	public static float weaponDrop;
	public static float armourDrop;
	public static List mobsToSpawn;
	public static Random rand = new Random();

	public static void createConfigs(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(new File(MobOptions.INSTANCE.getConfigFolder() + "/moboptions.cfg"));
		config.load();

		underground = config.getInt("Sea Level", "Underground Spawning", 50, 0, 255,"Mobs will spawn more often and are unaffected by light below this Y coordinate.");
		undergroundChance = config.getFloat("Spawn Chance", "Underground Spawning", .01F, 0F, 1F,"Chance to spawn mobs under sea level. Careful!");
		
		spawnWhitelist = config.getBoolean("Use Whitelist", "Underground Spawning", true,"If set to false Mobs that ignore light-level will act as a blacklist.");
		String defaultMobsToSpawn = "Zombie, Skeleton, Creeper, Spider";
		defaultMobsToSpawn = config.getString("Mobs that ignore light-level", "Underground Spawning", defaultMobsToSpawn,"Comma seperated list of mobs that will spawn under sea level ignoring light.");
		mobsToSpawn = Arrays.asList(defaultMobsToSpawn.replace(" ", "").split(","));

		spawnRadius = config.getInt("Spawn Area", "Difficulty Settings", 400, 0, 10000,"Radius around 0,0 to not buff mobs by distance (will still buff from height).");
		spawnLocation = config.getBoolean("Spawn Location Calculation", "Difficulty Settings", true, "If true, will calculate against the overworld's actual spawn-point. If false, will calculate the safe-area centered at x=0,z=0");
		depthMultiplier = config.getFloat("Depth Multipler", "Difficulty Settings", -.1F, -10F, 10F,"How quickly will mobs get easier or harder as y increases. (negative is easier)");
		depthOffset = config.getFloat("Depth Offset", "Difficulty Settings", 5F, 0F, 1000F, "Mob Strength at y=0");
		distanceDifficulty = config.getInt("Distance Difficulty", "Difficulty Settings", 200, 0, 1000,"Distance / Difficulty (Lower is harder!)(2000/200 mobs will be level 10)");
		attackGain = config.getFloat("Attack Gain", "Difficulty Settings", 0.5F, 0F, 5F,"How much (percentage) attack damage mobs gain approx every two levels.");

		probTier5 = config.getFloat("Tier5 Chance", "Armor and Weapons", .01F, 0F, 1F,"Percent a mob will get Tier5 gear per 2 levels approx.");
		probTier4 = config.getFloat("Tier4 Chance", "Armor and Weapons", .01F, 0F, 1F,"Percent a mob will get Tier4 gear per 2 levels approx.");
		probTier3 = config.getFloat("Tier3 Chance", "Armor and Weapons", .02F, 0F, 1F,"Percent a mob will get Tier3 gear per 2 levels approx.");
		probTier2 = config.getFloat("Tier2 Chance", "Armor and Weapons", .04F, 0F, 1F,"Percent a mob will get Tier2 gear per 2 levels approx.");
		probTier1 = config.getFloat("Tier1 Chance", "Armor and Weapons", .06F, 0F, 1F,"Percent a mob will get Tier1 gear per 2 levels approx.");
		weaponDrop = config.getFloat("Weapon Drop", "Armor and Weapons", .00F, 0F, 1F,"Percent a mob will drop its weapon");
		armourDrop = config.getFloat("Armor Drop", "Armor and Weapons", .01F, 0F, 1F,"Percent a mob will drop a piece of armor");
		enchantCost = config.getFloat("Enchantment Cost", "Enchantments", 2F, 1F, 100F,"How many levels does it take for a mob to gain an enchantment?");

		maxSpeed = config.getFloat("Creeper Max Speed", "Mob Settings", .4F, 0F, 5F,"Max creeper speed. (base speed is .25");
		speedGain = config.getFloat("Creeper Speed Gain", "Mob Settings", .01F, 0F, .5F,"Approx. creeper speed gain per 2 levels.");
		maxRange = config.getInt("Range Max", "Mob Settings", 80, 16, 100,"Max mob tracking range.");
		rangeGain = config.getFloat("Range Gain", "Mob Settings", 1F, 0F, 10F,"Approx. tracking range gain per 2 levels.");

		dimData = new HashMap<Integer, DimensionMobOptions>();
		String rawDimData = "0, 1, 25, 400, 40 : 7, 5, 30, 500, 50 : -1, 10, 35, 600, 60 : 1, 20, 40, 2000, 75";
		rawDimData = config.getString("Dimension Difficulty", "Dimension Settings", rawDimData,"Difficulty settings by dimension. (Dimension ID, Base difficulty, Max difficulty, Health cap, Damage cap) Seperate dimensions with ':', each dimension MUST have 5 values.");
		defaultDim = config.getInt("Default Dimension", "Dimension Settings", 0, -10000, 10000,"If a dimension is not specified use the settings of this dimension");
		for (String thisDim : rawDimData.replace(" ", "").split(":")) {
			String[] thisDimData = thisDim.split(",");
			dimData.put(Integer.parseInt(thisDimData[0]), 
				new DimensionMobOptions(
					Integer.parseInt(thisDimData[1]), 
					Integer.parseInt(thisDimData[2]),
					Integer.parseInt(thisDimData[3]), 
					Integer.parseInt(thisDimData[4]))
				);
		}

		config.save();
	}

}
