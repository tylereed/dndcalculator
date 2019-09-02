package dndcalculator;

import java.util.stream.IntStream;

import dndcalculator.model.Roll;
import dndcalculator.simulation.Calculator;
import dndcalculator.simulation.Simulator;
import lombok.Value;

public class CommandLine {

	public static void main(String... args) throws Exception {
		var attack = Roll.parseRoll("d20+5");
		var damage = Roll.parseRoll("2d6+2");

		int repetitions = 5000000;

		int[] acs = IntStream.rangeClosed(1, 30).toArray();

		System.out.println("roll type,average damage,enemy ac,repititions");
		for (int ac : acs) {
			String[] calc = doCalc(attack, damage, ac, repetitions);
			System.out.println(String.join(",", calc));
		}

	}

	private static String[] doCalc(Roll attack, Roll damage, int ac, int repititions) {
		var sim = new Simulator(attack, damage, ac);

		double aveDamage = Calculator.calculateAverageDamage(sim, repititions);

		String[] results = { attack.getName() + ":" + damage.getName(), String.format("%.03f", aveDamage),
				String.valueOf(ac), String.valueOf(repititions) };
		return results;
	}

}
