package dndcalculator.simulation;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Calculator {

	private Calculator() {
	}

	public static double calculateAverageDamage(Simulator sim, int repititions) {
		long totalDamage = LongStream.generate(() -> sim.doAttack()).limit(repititions).sum();
		return totalDamage / (double) repititions;
	}

	public static Map<Integer, Double> calculateDamageHistogram(Simulator sim, int repititions) {
		return IntStream.generate(() -> sim.doAttack()).limit(repititions)
				.collect(Counter::new, Counter::accept, Counter::combine).getHistogram(repititions);
	}

	public static double calculateAverageRounds(Simulator sim, int health, int repititions) {
		long totalRounds = LongStream.generate(() -> simulateNumberRounds(sim, health)).limit(repititions).sum();
		return totalRounds / (double) repititions;
	}

	public static Map<Integer, Double> calculateRoundsHistogram(Simulator sim, int health, int repititions) {
		return IntStream.generate(() -> simulateNumberRounds(sim, health)).limit(repititions).parallel()
				.collect(Counter::new, Counter::accept, Counter::combine).getHistogram(repititions);
	}

	private static int simulateNumberRounds(Simulator sim, int health) {

		int rounds = 0;
		while (health > 0) {
			++rounds;
			health -= sim.doAttack();
		}

		return rounds;
	}

	private static class Counter implements IntConsumer {

		private Map<Integer, Double> counts;

		public Counter() {
			counts = new TreeMap<>();
		}

		@Override
		public void accept(int key) {
			counts.compute(key, (k, v) -> v != null ? v + 1 : 1);
		}

		public void combine(Counter other) {
			for (var e : other.counts.entrySet()) {
				this.counts.merge(e.getKey(), e.getValue(), (x, y) -> x + y);
			}
		}

		public Map<Integer, Double> getHistogram(double repititions) {
			var clone = new TreeMap<Integer, Double>();
			clone.putAll(counts);
			clone.forEach((k, v) -> clone.put(k, (v / repititions) * 100));
			return clone;
		}

	}

}
