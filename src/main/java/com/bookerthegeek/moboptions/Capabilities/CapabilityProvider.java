package com.bookerthegeek.moboptions.Capabilities;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityProvider<C> implements ICapabilityProvider, INBTSerializable<NBTBase>{

	protected final Capability<C> capability;

	protected final EnumFacing facing;

	protected final C instance;

	public CapabilityProvider(final C instance, final Capability<C> capability, @Nullable final EnumFacing facing) {
		this.instance = instance;
		this.capability = capability;
		this.facing = facing;
	}
	
	public CapabilityProvider(final Capability<C> capability, @Nullable final EnumFacing facing, final C instance) {
		this(instance, capability, facing);
	}
	
	public CapabilityProvider(final Capability<C> capability, @Nullable final EnumFacing facing) {
		this(capability, facing, capability.getDefaultInstance());
	}

	@Override
	public boolean hasCapability(final Capability<?> capability, @Nullable final EnumFacing facing) {
		return capability == getCapability();
	}

	@Override
	@Nullable
	public <T> T getCapability(final Capability<T> capability, @Nullable final EnumFacing facing) {
		if (capability == getCapability()) {
			return getCapability().cast(getInstance());
		}

		return null;
	}

	public final Capability<C> getCapability() {
		return capability;
	}

	@Nullable
	public EnumFacing getFacing() {
		return facing;
	}

	public final C getInstance() {
		return instance;
	}

	@Override
	public NBTBase serializeNBT() {
		return getCapability().writeNBT(getInstance(), getFacing());
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		getCapability().readNBT(getInstance(), getFacing(), nbt);
	}
	
	
	@Nullable
	public static <T> T getCapability(@Nullable ICapabilityProvider provider, Capability<T> capability, @Nullable EnumFacing facing) {
		return provider != null && provider.hasCapability(capability, facing) ? provider.getCapability(capability, facing) : null;
	}
}
