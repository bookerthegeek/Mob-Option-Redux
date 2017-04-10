package com.bookerthegeek.moboptions.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import com.bookerthegeek.moboptions.MobOptions;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityDump {
	
	public static File entLoc = new File(MobOptions.INSTANCE.getConfigFolder() + "/entity-dump.txt");
	
	public static void dumpAllEntities(){
		if (!entLoc.exists()) {
			try (Writer writer = new FileWriter(entLoc, true)) {
				entLoc.createNewFile();
				writer.append("These are all of the mobs currently in the game. Only (living) hostiles will have their spawn-logic affected."+ System.lineSeparator());
				ArrayList<String> entities = new ArrayList<String>();
				for (String ent : EntityList.getEntityNameList())
					entities.add(ent + System.lineSeparator());
				
				
				Collections.sort(entities);
				
				for(String ent : entities)
					writer.append(ent);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
