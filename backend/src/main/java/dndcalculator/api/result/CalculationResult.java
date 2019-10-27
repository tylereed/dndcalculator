package dndcalculator.api.result;

import java.util.Map;

import lombok.Value;

@Value
public class CalculationResult<T, U> {

	private String keyName;
	private String valueName;
	private String attack;
	private String damage;
	private Map<T, U> results;

}
