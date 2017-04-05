package com.bookerthegeek.moboptions.Capabilities.stats;

public interface IFollow {
	
	float getBonusFollowRange();

	void setBonusFollowRange(float bonusFollowRange);

	void addBonusFollowRange(float followRangeToAdd);

	void synchronise();

}
