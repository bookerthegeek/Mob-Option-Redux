package com.bookerthegeek.moboptions.Capabilities.stats;

import java.util.Collections;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.world.WorldServer;

public class Stats implements IHealth, IAttack{

	protected static final UUID MODIFIER_HEALTH_ID = UUID.fromString("e1d51c86-f189-407b-a032-c0b735442a18");
	protected static final String MODIFIER_HEALTH_NAME = "Bonus Max Health";
	protected static final float MIN_HEALTH_AMOUNT = 20.0f;
	private float bonusMaxHealth;
	
	protected static final UUID MODIFIER_ATTACK_ID = UUID.fromString("774e0442-4571-43c6-b492-5db31d3c9d23");
	protected static final String MODIFIER_ATTACK_NAME = "Bonus Max Health";
	protected static final float MIN_ATTACK_AMOUNT = 1.0f;
	private float bonusMaxAttack;


	private final EntityMob entity;


	


	private final IAttributeInstance dummyMaxHealthAttribute = new AttributeMap()
			.registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
	
	private final IAttributeInstance dummyMaxAttackAttribute = new AttributeMap()
			.registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

	
	
	public Stats(final EntityMob entity) {
		this.entity = entity;
	}
	
	


	@Override
	public final float getBonusMaxHealth() {
		return bonusMaxHealth;
	}
	
	@Override
	public float getBonusMaxAttack() {
		return bonusMaxAttack;
	}


	@Override
	public final void setBonusMaxHealth(final float bonusMaxHealth) {
		this.bonusMaxHealth = bonusMaxHealth;

		onBonusMaxHealthChanged();
	}
	
	@Override
	public void setBonusMaxAttack(float bonusMaxAttack) {
		this.bonusMaxAttack = bonusMaxAttack;
		
		onBonusAttackChanged();
		
	}


	@Override
	public final void addBonusMaxHealth(final float healthToAdd) {
		setBonusMaxHealth(getBonusMaxHealth() + healthToAdd);
	}
	
	@Override
	public void addBonusMaxAttack(float attackToAdd) {
		setBonusMaxAttack(getBonusMaxHealth() + attackToAdd);
		
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


	protected AttributeModifier createModifier(String type) {
		if(type.equals("health"))
			return new AttributeModifier(MODIFIER_HEALTH_ID, MODIFIER_HEALTH_NAME, getBonusMaxHealth(), 0);
		
		if(type.equals("attack"))
			return new AttributeModifier(MODIFIER_ATTACK_ID, MODIFIER_ATTACK_NAME, getBonusMaxAttack(), 0);
		
		return null;
	}

	
	protected void onBonusMaxHealthChanged() {
		if (entity == null)
			return;

		final IAttributeInstance entityMaxHealthAttribute = entity
				.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);

		dummyMaxHealthAttribute.getModifiers().forEach(dummyMaxHealthAttribute::removeModifier);


		dummyMaxHealthAttribute.setBaseValue(entityMaxHealthAttribute.getBaseValue());
		entityMaxHealthAttribute.getModifiers().stream().filter(modifier -> !modifier.getID().equals(MODIFIER_HEALTH_ID))
				.forEach(dummyMaxHealthAttribute::applyModifier);

		AttributeModifier modifier = createModifier("health");
		dummyMaxHealthAttribute.applyModifier(modifier);

		while (dummyMaxHealthAttribute.getAttributeValue() < MIN_HEALTH_AMOUNT) {
			dummyMaxHealthAttribute.removeModifier(modifier);
			bonusMaxHealth += 0.5f;
			modifier = createModifier("health");
			dummyMaxHealthAttribute.applyModifier(modifier);
		}

		final float newAmount = getBonusMaxHealth();
		final float oldAmount;

		final AttributeModifier oldModifier = entityMaxHealthAttribute.getModifier(MODIFIER_HEALTH_ID);
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
	
	protected void onBonusAttackChanged() {
		if (entity == null)
			return;

		final IAttributeInstance entityMaxAttackAttribute = entity
				.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

		dummyMaxAttackAttribute.getModifiers().forEach(dummyMaxAttackAttribute::removeModifier);


		dummyMaxAttackAttribute.setBaseValue(entityMaxAttackAttribute.getBaseValue());
		entityMaxAttackAttribute.getModifiers().stream().filter(modifier -> !modifier.getID().equals(MODIFIER_ATTACK_ID))
				.forEach(dummyMaxAttackAttribute::applyModifier);

		AttributeModifier modifier = createModifier("attack");
		dummyMaxAttackAttribute.applyModifier(modifier);

		while (dummyMaxAttackAttribute.getAttributeValue() < MIN_HEALTH_AMOUNT) {
			dummyMaxAttackAttribute.removeModifier(modifier);
			bonusMaxAttack += 0.5f;
			modifier = createModifier("attack");
			dummyMaxAttackAttribute.applyModifier(modifier);
		}

		final float newAmount = getBonusMaxAttack();
		final float oldAmount;

		final AttributeModifier oldModifier = entityMaxAttackAttribute.getModifier(MODIFIER_ATTACK_ID);
		if (oldModifier != null) {
			entityMaxAttackAttribute.removeModifier(oldModifier);

			oldAmount = (float) oldModifier.getAmount();
		} else
			oldAmount = 0.0f;

		entityMaxAttackAttribute.applyModifier(modifier);
	}
}
