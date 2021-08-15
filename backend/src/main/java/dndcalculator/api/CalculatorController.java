package dndcalculator.api;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dndcalculator.api.result.CalculationResult;
import dndcalculator.model.Roll;
import dndcalculator.simulation.Calculator;
import dndcalculator.simulation.Simulator;
import lombok.Value;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class CalculatorController {

	@GetMapping({ "/calculate/{attack}/{damage}" })
	@Cacheable("ACvAD")
	public CalculationResult<Integer, Double> calculateDamage(@PathVariable String attack, @PathVariable String damage,
			@RequestParam Optional<Integer> critThreshold, @RequestParam Optional<Integer> repititions) {

		var attackRoll = Roll.parseRoll(attack);
		var damageRoll = Roll.parseRoll(damage);
		int crit = critThreshold.orElse(20);
		int reps = repititions.orElse(1000000);

		Map<Integer, Double> result = IntStream.rangeClosed(1, 30).mapToObj(ac -> {
			var simulator = new Simulator(attackRoll, damageRoll, ac, crit);
			double d = Calculator.calculateAverageDamage(simulator, reps);
			return new AverageDamageToAc(ac, d);
		}).collect(Collectors.toMap(AverageDamageToAc::getArmorClass, AverageDamageToAc::getAverageDamage));

		return new CalculationResult<Integer, Double>("Armor Class", "Average Damage", attackRoll.getName(),
				damageRoll.getName(), result);
	}

	@Value
	private class AverageDamageToAc {
		private final int armorClass;
		private final double averageDamage;
	}

}
