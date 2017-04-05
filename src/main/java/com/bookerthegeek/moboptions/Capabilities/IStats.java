package com.bookerthegeek.moboptions.Capabilities;

public interface IStats {

	float getBonusMaxHealth();

	void setBonusMaxHealth(float bonusMaxHealth);

	void addBonusMaxHealth(float healthToAdd);

	void synchronise();

}
