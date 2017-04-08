package com.bookerthegeek.moboptions;

import java.io.File;

import com.bookerthegeek.moboptions.Capabilities.stats.CapabilityStats;
import com.bookerthegeek.moboptions.utils.JSONEquipmentReader;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MobOptions.MODID, 
	version = MobOptions.VERSION, 
	name = "MobOptions", 
	clientSideOnly = false, 
	serverSideOnly = false
)
public class MobOptions{
    public static final String MODID = "moboptions";
    public static final String VERSION = "1.1";
    
    @Instance("moboptions")
    public static MobOptions INSTANCE;
    
    public File configDir;
    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	
    	configDir = new File(event.getModConfigurationDirectory(), "MobOptions");
    	if(!configDir.exists())
			configDir.mkdirs();
		JSONEquipmentReader.makeEquipmentJSON();
    	
    	
    	ConfigHandler.createConfigs(event);
    	
    	CapabilityStats.register();
    	
    	MinecraftForge.EVENT_BUS.register(new MobOptionsEventHandler());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    }
    
    public File getConfigFolder(){
    	return configDir;
    }
}
