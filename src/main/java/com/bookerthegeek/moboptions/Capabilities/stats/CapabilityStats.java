package com.bookerthegeek.moboptions.Capabilities.stats;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.bookerthegeek.moboptions.MobOptions;
import com.bookerthegeek.moboptions.Capabilities.CapabilityProvider;

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

	@CapabilityInject(IHealth.class)
	public static final Capability<IHealth> MAX_HEALTH_CAPABILITY = null;
	public static final ResourceLocation HEALTH_ID = new ResourceLocation(MobOptions.MODID, "MaxHealth");
	
	@CapabilityInject(IAttack.class)
	public static final Capability<IAttack> ATTACK_CAPABILITY = null;
	public static final ResourceLocation ATTACK_ID = new ResourceLocation(MobOptions.MODID, "Attack");
	
	@CapabilityInject(IFollow.class)
	public static final Capability<IFollow> FOLLOW_RANGE_CAPABILITY = null;
	public static final ResourceLocation FOLLOW_ID = new ResourceLocation(MobOptions.MODID, "FollowRange");

	public static final EnumFacing DEFAULT_FACING = null;


	

	public static void register() {
		CapabilityManager.INSTANCE.register(IHealth.class, new Capability.IStorage<IHealth>() {
			@Override
			public NBTBase writeNBT(final Capability<IHealth> capability, final IHealth instance, final EnumFacing side) {
				return new NBTTagFloat(instance.getBonusMaxHealth());
			}

			@Override
			public void readNBT(final Capability<IHealth> capability, final IHealth instance, final EnumFacing side, final NBTBase nbt) {
				instance.setBonusMaxHealth(((NBTTagFloat) nbt).getFloat());
			}
		}, () -> new Stats(null));
		
		CapabilityManager.INSTANCE.register(IAttack.class, new Capability.IStorage<IAttack>() {
			@Override
			public NBTBase writeNBT(final Capability<IAttack> capability, final IAttack instance, final EnumFacing side) {
				return new NBTTagFloat(instance.getBonusMaxAttack());
			}

			@Override
			public void readNBT(final Capability<IAttack> capability, final IAttack instance, final EnumFacing side, final NBTBase nbt) {
				instance.setBonusMaxAttack(((NBTTagFloat) nbt).getFloat());
			}
		}, () -> new Stats(null));
		
		CapabilityManager.INSTANCE.register(IFollow.class, new Capability.IStorage<IFollow>() {
			@Override
			public NBTBase writeNBT(final Capability<IFollow> capability, final IFollow instance, final EnumFacing side) {
				return new NBTTagFloat(instance.getBonusFollowRange());
			}

			@Override
			public void readNBT(final Capability<IFollow> capability, final IFollow instance, final EnumFacing side, final NBTBase nbt) {
				instance.setBonusFollowRange(((NBTTagFloat) nbt).getFloat());
			}
		}, () -> new Stats(null));
	}


	@Nullable
	public static IHealth getMaxHealth(final EntityMob entity) {
		return CapabilityProvider.getCapability(entity, MAX_HEALTH_CAPABILITY, DEFAULT_FACING);
	}
	
	@Nullable
	public static IAttack getMaxAttack(final EntityMob entity) {
		return CapabilityProvider.getCapability(entity, ATTACK_CAPABILITY, DEFAULT_FACING);
	}
	
	@Nullable
	public static IFollow getFollow(final EntityMob entity) {
		return CapabilityProvider.getCapability(entity, FOLLOW_RANGE_CAPABILITY, DEFAULT_FACING);
	}


	public static ICapabilityProvider createProvider(final IHealth maxHealth) {
		return new CapabilityProvider<>(MAX_HEALTH_CAPABILITY, DEFAULT_FACING, maxHealth);
	}
	
	public static ICapabilityProvider createProvider(final IAttack attack) {
		return new CapabilityProvider<>(ATTACK_CAPABILITY, DEFAULT_FACING, attack);
	}
	
	public static ICapabilityProvider createProvider(final IFollow attack) {
		return new CapabilityProvider<>(FOLLOW_RANGE_CAPABILITY, DEFAULT_FACING, attack);
	}


	@Mod.EventBusSubscriber
	private static class EventHandler {


		@SubscribeEvent
		public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof EntityMob) {
				final Stats stats = new Stats((EntityMob) event.getObject());
				event.addCapability(HEALTH_ID, createProvider((IHealth)stats));
				event.addCapability(ATTACK_ID, createProvider((IAttack)stats));
				event.addCapability(FOLLOW_ID, createProvider((IFollow)stats));
			}
		}
	}
}
