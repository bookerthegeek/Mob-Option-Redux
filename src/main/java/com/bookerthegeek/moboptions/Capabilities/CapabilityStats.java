package com.bookerthegeek.moboptions.Capabilities;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.bookerthegeek.moboptions.MobOptions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class CapabilityStats {

	@CapabilityInject(IStats.class)
	public static final Capability<IStats> MAX_HEALTH_CAPABILITY = null;

	public static final EnumFacing DEFAULT_FACING = null;


	public static final ResourceLocation ID = new ResourceLocation(MobOptions.MODID, "MaxHealth");

	public static void register() {
		CapabilityManager.INSTANCE.register(IStats.class, new Capability.IStorage<IStats>() {
			@Override
			public NBTBase writeNBT(final Capability<IStats> capability, final IStats instance, final EnumFacing side) {
				return new NBTTagFloat(instance.getBonusMaxHealth());
			}

			@Override
			public void readNBT(final Capability<IStats> capability, final IStats instance, final EnumFacing side, final NBTBase nbt) {
				instance.setBonusMaxHealth(((NBTTagFloat) nbt).getFloat());
			}
		}, () -> new Stats(null));
	}


	@Nullable
	public static IStats getMaxHealth(final EntityMob entity) {
		return CapabilityProvider.getCapability(entity, MAX_HEALTH_CAPABILITY, DEFAULT_FACING);
	}


	public static ICapabilityProvider createProvider(final IStats maxHealth) {
		return new CapabilityProvider<>(MAX_HEALTH_CAPABILITY, DEFAULT_FACING, maxHealth);
	}


	public static String formatMaxHealth(final float maxHealth) {
		return ItemStack.DECIMALFORMAT.format(maxHealth);
	}


	@Mod.EventBusSubscriber
	private static class EventHandler {


		@SubscribeEvent
		public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof EntityMob) {
				final Stats stats = new Stats((EntityMob) event.getObject());
				event.addCapability(ID, createProvider(stats));
			}
		}
	}
}
