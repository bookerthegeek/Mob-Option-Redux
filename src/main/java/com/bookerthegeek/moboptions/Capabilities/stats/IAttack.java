package com.bookerthegeek.moboptions.Capabilities.stats;

public interface IAttack {

	float getBonusMaxAttack();

	void setBonusMaxAttack(float bonusMaxAttack);

	void addBonusMaxAttack(float attackToAdd);

	void synchronise();
}
