package com.bookerthegeek.moboptions.equipment;

public class SetHandler {

	public String helmet;
	public String chestplate;
	public String leggings;
	public String boots;
	public String mainHand;
	public String offHand;
	public boolean setPartsCanDrop;

	public SetHandler(String head, String chest, String legs, String feet, String weapon, String shield, boolean drops) {
		helmet = head;
		chestplate = chest;
		leggings = legs;
		boots = feet;
		mainHand = weapon;
		offHand = shield;
		setPartsCanDrop = drops;
	}

}
