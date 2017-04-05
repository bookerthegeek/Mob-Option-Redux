package com.bookerthegeek.moboptions;

import com.bookerthegeek.moboptions.Capabilities.CapabilityStats;

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
    public static final String VERSION = "1.0";
    
    @Instance("moboptions")
    public static MobOptions INSTANCE;
    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
    	ConfigHandler.createConfigs(event);
    	
    	CapabilityStats.register();
    	
    	MinecraftForge.EVENT_BUS.register(new MobOptionsEventHandler());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    }
}
