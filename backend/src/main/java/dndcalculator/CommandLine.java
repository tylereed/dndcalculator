package dndcalculator;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import dndcalculator.model.Roll;
import dndcalculator.simulation.Calculator;
import dndcalculator.simulation.Simulator;

public class CommandLine {

	public static void main(String... args) throws Exception {
		var attack = Roll.parseRoll("d20+4");
		var damage = Roll.parseRoll("d8+14");

		int repetition = 500000000;

//		int[] acs = IntStream.rangeClosed(1, 30).toArray();
//		System.out.println("roll type,average damage,enemy ac,repititions");
//		for (int ac : acs) {
//			String[] calc = doCalc(attack, damage, ac, repetitions);
//			System.out.println(String.join(",", calc));
//		}

		System.out.println("roll type,damage,probability,enemy ac");
		doHistogram(attack, damage, 18, repetition).map(l -> String.join(",", l)).forEach(l -> System.out.println(l));

	}

	private static String[] doCalc(Roll attack, Roll damage, int ac, int repetitions) {
		var sim = new Simulator(attack, damage, ac);

		double aveDamage = Calculator.calculateAverageDamage(sim, repetitions);

		String[] results = { attack.getName() + ":" + damage.getName(), String.format("%.03f", aveDamage),
				String.valueOf(ac), String.valueOf(repetitions) };
		return results;
	}

	private static Stream<String[]> doHistogram(Roll attack, Roll damage, int ac, int repetitions) {
		var sim = new Simulator(attack, damage, ac);

		var histogram = Calculator.calculateDamageHistogram(sim, repetitions);

		return histogram.entrySet().stream().map(e -> new String[] { attack.getName() + ":" + damage.getName(),
				String.valueOf(e.getKey()), String.format("%.02f", e.getValue()), String.valueOf(ac) });
	}

}
