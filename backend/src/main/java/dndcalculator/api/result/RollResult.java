package dndcalculator.api.result;

import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;

@Value
public class RollResult {
	
	private final String name;
	
	private final List<Integer> rolls;
	
	@ConstructorProperties({"name", "rolls"})
	public RollResult(String name, Collection<Integer> rolls) {
		this.name = name;
		this.rolls = Collections.unmodifiableList(rolls.stream().collect(Collectors.toList()));
	}

}
