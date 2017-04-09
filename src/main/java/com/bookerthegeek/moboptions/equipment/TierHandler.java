package com.bookerthegeek.moboptions.equipment;

import java.util.ArrayList;

public class TierHandler {

	public ArrayList<SetHandler> tier1 = new ArrayList<SetHandler>();
	public ArrayList<SetHandler> tier2 = new ArrayList<SetHandler>();
	public ArrayList<SetHandler> tier3 = new ArrayList<SetHandler>();
	public ArrayList<SetHandler> tier4 = new ArrayList<SetHandler>();
	public ArrayList<SetHandler> tier5 = new ArrayList<SetHandler>();

	public TierHandler addToList(int tier, SetHandler set) {
		if (tier == 1)
			tier1.add(set);
		if (tier == 2)
			tier2.add(set);
		if (tier == 3)
			tier3.add(set);
		if (tier == 4)
			tier4.add(set);
		if (tier == 5)
			tier5.add(set);

		return this;
	}

}
