package com.bookerthegeek.moboptions.Capabilities.stats;

import java.util.Arrays;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.world.WorldServer;

public class Stats implements IHealth, IAttack, IFollow {

	protected static final UUID MODIFIER_HEALTH_ID = UUID.fromString("e1d51c86-f189-407b-a032-c0b735442a18");
	protected static final String MODIFIER_HEALTH_NAME = "Bonus Max Health";
	protected static final float MIN_HEALTH_AMOUNT = 20.0f;
	private float bonusMaxHealth;

	protected static final UUID MODIFIER_ATTACK_ID = UUID.fromString("774e0442-4571-43c6-b492-5db31d3c9d23");
	protected static final String MODIFIER_ATTACK_NAME = "Bonus Attack Damage";
	protected static final float MIN_ATTACK_AMOUNT = 1.0f;
	private float bonusMaxAttack;

	protected static final UUID MODIFIER_FOLLOW_ID = UUID.fromString("8da6675d-655d-49d5-9ffa-26263b6aca89");
	protected static final String MODIFIER_FOLLOW_NAME = "Bonus Follow-Range";
	protected static final float MIN_FOLLOW_AMOUNT = 1.0f;
	private float bonusFollow;

	private final EntityMob entity;

	private final IAttributeInstance dummyMaxHealthAttribute = new AttributeMap().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);

	private final IAttributeInstance dummyAttackAttribute = new AttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

	private final IAttributeInstance dummyFollowAttribute = new AttributeMap().registerAttribute(SharedMonsterAttributes.FOLLOW_RANGE);

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
	public float getBonusFollowRange() {
		return bonusFollow;
	}

	@Override
	public final void setBonusMaxHealth(final float bonusMaxHealth) {
		this.bonusMaxHealth = bonusMaxHealth;

		onStatChanged(MODIFIER_HEALTH_ID, SharedMonsterAttributes.MAX_HEALTH, dummyMaxHealthAttribute, "health", MIN_HEALTH_AMOUNT, getBonusMaxHealth());
	}

	@Override
	public void setBonusMaxAttack(final float bonusMaxAttack) {
		this.bonusMaxAttack = bonusMaxAttack;

		onStatChanged(MODIFIER_ATTACK_ID, SharedMonsterAttributes.ATTACK_DAMAGE, dummyAttackAttribute, "attack", MIN_ATTACK_AMOUNT, getBonusMaxAttack());
	}

	@Override
	public void setBonusFollowRange(final float bonusFollowRange) {
		this.bonusFollow = bonusFollowRange;

		onStatChanged(MODIFIER_FOLLOW_ID, SharedMonsterAttributes.FOLLOW_RANGE, dummyFollowAttribute, "follow", MIN_FOLLOW_AMOUNT, getBonusFollowRange());
	}

	@Override
	public final void addBonusMaxHealth(final float healthToAdd) {
		setBonusMaxHealth(getBonusMaxHealth() + healthToAdd);
	}

	@Override
	public void addBonusMaxAttack(final float attackToAdd) {
		setBonusMaxAttack(getBonusMaxAttack() + attackToAdd);
	}

	@Override
	public void addBonusFollowRange(final float followRangeToAdd) {
		setBonusMaxAttack(getBonusFollowRange() + followRangeToAdd);
	}

	@Override
	public void synchronise() {
		if (entity != null && !entity.getEntityWorld().isRemote) {
			final IAttributeInstance health = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);

			final IAttributeInstance attack = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

			final IAttributeInstance follow = entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);

			final SPacketEntityProperties packet = new SPacketEntityProperties(entity.getEntityId(), Arrays.asList(new IAttributeInstance[] { health, attack, follow }));

			((WorldServer) entity.getEntityWorld()).getEntityTracker().sendToTrackingAndSelf(entity, packet);
		}
	}

	protected AttributeModifier createModifier(String type) {
		if (type.equals("health"))
			return new AttributeModifier(MODIFIER_HEALTH_ID, MODIFIER_HEALTH_NAME, getBonusMaxHealth(), 0);

		if (type.equals("attack"))
			return new AttributeModifier(MODIFIER_ATTACK_ID, MODIFIER_ATTACK_NAME, getBonusMaxAttack(), 0);

		if (type.equals("follow"))
			return new AttributeModifier(MODIFIER_FOLLOW_ID, MODIFIER_FOLLOW_NAME, getBonusFollowRange(), 0);

		return null;
	}

	protected void onStatChanged(UUID id, IAttribute type, IAttributeInstance dummy, String mod, float minAmountForType, float newAmount) {
		if (entity == null)
			return;

		final IAttributeInstance entityAttribute = entity.getEntityAttribute(type);

		dummy.getModifiers().forEach(dummy::removeModifier);

		dummy.setBaseValue(entityAttribute.getBaseValue());
		entityAttribute.getModifiers().stream().filter(modifier -> !modifier.getID().equals(id)).forEach(dummy::applyModifier);

		AttributeModifier modifier = createModifier(mod);
		dummy.applyModifier(modifier);

		while (dummy.getAttributeValue() < minAmountForType) {
			dummy.removeModifier(modifier);
			bonusMaxHealth += 0.5f;
			modifier = createModifier(mod);
			dummy.applyModifier(modifier);
		}

		final float _newAmount = newAmount;
		final float oldAmount;

		final AttributeModifier oldModifier = entityAttribute.getModifier(id);
		if (oldModifier != null) {
			entityAttribute.removeModifier(oldModifier);

			oldAmount = (float) oldModifier.getAmount();
		} else
			oldAmount = 0.0f;

		entityAttribute.applyModifier(modifier);

		if (mod.equals("health")) {
			final float amountToHeal = _newAmount - oldAmount;
			if (amountToHeal > 0) {
				entity.heal(amountToHeal);
			}
		}
	}
}
