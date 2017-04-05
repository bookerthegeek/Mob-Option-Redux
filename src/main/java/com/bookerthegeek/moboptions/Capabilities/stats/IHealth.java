package com.bookerthegeek.moboptions.Capabilities.stats;

public interface IHealth {

	float getBonusMaxHealth();

	void setBonusMaxHealth(float bonusMaxHealth);

	void addBonusMaxHealth(float healthToAdd);

	void synchronise();

}
