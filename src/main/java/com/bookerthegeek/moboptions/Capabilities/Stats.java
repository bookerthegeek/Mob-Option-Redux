package com.bookerthegeek.moboptions.Capabilities;

import java.util.Collections;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.world.WorldServer;

public class Stats implements IStats {

	protected static final UUID MODIFIER_ID = UUID.fromString("d5d0d878-b3c2-469b-ba89-ac01c0635a9c");


	protected static final String MODIFIER_NAME = "Bonus Max Health";


	protected static final float MIN_AMOUNT = 20.0f;


	private final EntityMob entity;


	private float bonusMaxHealth;


	private final IAttributeInstance dummyMaxHealthAttribute = new AttributeMap()
			.registerAttribute(SharedMonsterAttributes.MAX_HEALTH);

	public Stats(final EntityMob entity) {
		this.entity = entity;
	}


	@Override
	public final float getBonusMaxHealth() {
		return bonusMaxHealth;
	}


	@Override
	public final void setBonusMaxHealth(final float bonusMaxHealth) {
		this.bonusMaxHealth = bonusMaxHealth;

		onBonusMaxHealthChanged();
	}


	@Override
	public final void addBonusMaxHealth(final float healthToAdd) {
		setBonusMaxHealth(getBonusMaxHealth() + healthToAdd);
	}


	@Override
	public void synchronise() {
		if (entity != null && !entity.getEntityWorld().isRemote) {
			final IAttributeInstance entityMaxHealthAttribute = entity
					.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
			final SPacketEntityProperties packet = new SPacketEntityProperties(entity.getEntityId(),
					Collections.singleton(entityMaxHealthAttribute));
			((WorldServer) entity.getEntityWorld()).getEntityTracker().sendToTrackingAndSelf(entity, packet);
		}
	}


	protected AttributeModifier createModifier() {
		return new AttributeModifier(MODIFIER_ID, MODIFIER_NAME, getBonusMaxHealth(), 0);
	}

	
	protected void onBonusMaxHealthChanged() {
		if (entity == null)
			return;

		final IAttributeInstance entityMaxHealthAttribute = entity
				.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);

		dummyMaxHealthAttribute.getModifiers().forEach(dummyMaxHealthAttribute::removeModifier);


		dummyMaxHealthAttribute.setBaseValue(entityMaxHealthAttribute.getBaseValue());
		entityMaxHealthAttribute.getModifiers().stream().filter(modifier -> !modifier.getID().equals(MODIFIER_ID))
				.forEach(dummyMaxHealthAttribute::applyModifier);

		AttributeModifier modifier = createModifier();
		dummyMaxHealthAttribute.applyModifier(modifier);

		while (dummyMaxHealthAttribute.getAttributeValue() < MIN_AMOUNT) {
			dummyMaxHealthAttribute.removeModifier(modifier);
			bonusMaxHealth += 0.5f;
			modifier = createModifier();
			dummyMaxHealthAttribute.applyModifier(modifier);
		}

		final float newAmount = getBonusMaxHealth();
		final float oldAmount;

		final AttributeModifier oldModifier = entityMaxHealthAttribute.getModifier(MODIFIER_ID);
		if (oldModifier != null) {
			entityMaxHealthAttribute.removeModifier(oldModifier);

			oldAmount = (float) oldModifier.getAmount();
		} else
			oldAmount = 0.0f;

		entityMaxHealthAttribute.applyModifier(modifier);

		final float amountToHeal = newAmount - oldAmount;
		if (amountToHeal > 0) {
			entity.heal(amountToHeal);
		}
	}
}
