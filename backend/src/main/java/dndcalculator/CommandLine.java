package dndcalculator;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dndcalculator.model.Roll;
import dndcalculator.simulation.Calculator;
import dndcalculator.simulation.Simulator;

public class CommandLine {

	public static void main(String... args) throws Exception {

		int repetition = 100_000_000;

		ExecutorService executorService = Executors.newFixedThreadPool(7);
		ArrayList<Future<String>> tasks = new ArrayList<>();

		tasks.add(executorService
				.submit(() -> "AofD average: " + doCalc(CommandLine::advantageOfDisadvantage, repetition)));
		tasks.add(executorService
				.submit(() -> "DofA average: " + doCalc(CommandLine::disadvantageOfAdvatage, repetition)));
		tasks.add(executorService
				.submit(() -> "S average: " + doCalc(() -> Roll.parseRoll("d20").doThrow().getTotal(), repetition)));

		for (var callable : findHit("AofD", CommandLine::advantageOfDisadvantage, repetition)) {
			tasks.add(executorService.submit(callable));
		}
		for (var callable : findHit("DofA", CommandLine::disadvantageOfAdvatage, repetition)) {
			tasks.add(executorService.submit(callable));
		}
		for (var callable : findHit("S", () -> Roll.parseRoll("d20").doThrow().getTotal(), repetition)) {
			tasks.add(executorService.submit(callable));
		}
		
		for (var task : tasks) {
			System.out.println(task.get());
		}

//		var attack = Roll.parseRoll("d20+4");
//		var damage = Roll.parseRoll("d8+14");
//		var d20 = Roll.parseRoll("d20");
//		Math.max(d20.doThrow().getTotal(), d20.doThrow().getTotal());
//
//		int[] acs = IntStream.rangeClosed(1, 30).toArray();
//		System.out.println("roll type,average damage,enemy ac,repititions");
//		for (int ac : acs) {
//			String[] calc = doCalc(attack, damage, ac, repetitions);
//			System.out.println(String.join(",", calc));
//		}
//
//		System.out.println("roll type,damage,probability,enemy ac");
//		doHistogram(attack, damage, 18, repetition).map(l -> String.join(",", l)).forEach(l -> System.out.println(l));
	}

	public static ArrayList<Callable<String>> findHit(String id, IntSupplier dieThrow, int repititions) {
		ArrayList<Callable<String>> result = new ArrayList<>();

		for (int i = 1; i <= 20; i++) {
			final int ac = i;
			result.add(() -> {
				IntSupplier succeeds = () -> dieThrow.getAsInt() >= ac ? 1 : 0;
				Double hitPercent = IntStream.generate(succeeds).limit(repititions).sum() / (double) repititions;
				return id + ": " + ac + ", Hit: " + hitPercent;
			});
		}

		return result;
	}

	public static double doCalc(IntSupplier throwInt, int repititions) {
		return IntStream.generate(throwInt).limit(repititions).average().orElse(0);
	}

	public static int advantageOfDisadvantage() {
		return Math.max(throwDisadvantage(), throwDisadvantage());
	}

	public static int disadvantageOfAdvatage() {
		return Math.min(throwAdvantage(), throwAdvantage());
	}

	public static int throwAdvantage() {
		var d20 = Roll.parseRoll("d20");
		return Math.max(d20.doThrow().getTotal(), d20.doThrow().getTotal());
	}

	public static int throwDisadvantage() {
		var d20 = Roll.parseRoll("d20");
		return Math.min(d20.doThrow().getTotal(), d20.doThrow().getTotal());
	}

	private static String[] doCalc(Roll attack, Roll damage, int ac, int repetitions) {
		var sim = new Simulator(attack, damage, ac, 19);

		double aveDamage = Calculator.calculateAverageDamage(sim, repetitions);

		String[] results = { attack.getName() + ":" + damage.getName(), String.format("%.03f", aveDamage),
				String.valueOf(ac), String.valueOf(repetitions) };
		return results;
	}

	private static Stream<String[]> doHistogram(Roll attack, Roll damage, int ac, int repetitions) {
		var sim = new Simulator(attack, damage, ac, 19);

		var histogram = Calculator.calculateDamageHistogram(sim, repetitions);

		return histogram.entrySet().stream().map(e -> new String[] { attack.getName() + ":" + damage.getName(),
				String.valueOf(e.getKey()), String.format("%.02f", e.getValue()), String.valueOf(ac) });
	}

}
