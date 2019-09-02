package dndcalculator.model;

import lombok.Value;

@Value
public class Throw {

	private final int base, modifier;
	
	public int getTotal() {
		return base + modifier;
	}
	
}
