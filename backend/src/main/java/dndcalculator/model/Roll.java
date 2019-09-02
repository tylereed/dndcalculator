package dndcalculator.model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import dndcalculator.exceptions.RollParseException;
import lombok.Value;

@Value
public class Roll {
	
	private final int dice;
	private final int sides;
	private final int modifier;
	
	private static final Pattern dicePattern = Pattern.compile("^(\\d*)d(\\d+)((\\+|-)\\d+)?$");

	public static Roll parseRoll(String description) {
		var matcher = dicePattern.matcher(description);
		if (matcher.matches()) {
			var sDice = matcher.group(1);
			var dice = sDice != null && !sDice.isBlank() ? Integer.parseInt(sDice) : 1;
			
			var sSides = matcher.group(2);
			var sides = Integer.parseInt(sSides);
			
			var sModifier = matcher.group(3);
			var modifier = sModifier != null && !sModifier.isBlank() ? Integer.parseInt(sModifier) : 0;
			
			return new Roll(dice, sides, modifier);
		} else {
			throw new RollParseException(String.format("Unable to parse supplied roll %s", description));
		}
	}
	
	public int throwDice() {
		var random = ThreadLocalRandom.current();
		return random.ints(dice, 1, sides+1).sum() + modifier;
	}
	
	public Throw doThrow() {
		var random = ThreadLocalRandom.current();
		int sum = random.ints(dice, 1, sides+1).sum();
		return new Throw(sum, modifier);
	}
	
	public String getName() {
		var builder = new StringBuilder();
		
		if (dice > 1) {
			builder.append(dice);
		}
		
		builder.append("d").append(sides);
		
		if (modifier < 0) {
			builder.append(modifier);
		} else if (modifier > 0) {
			builder.append("+").append(modifier);
		}
		
		return builder.toString();
	}
	
}
