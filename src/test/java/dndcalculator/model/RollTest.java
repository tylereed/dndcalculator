package dndcalculator.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class RollTest {

	@ParameterizedTest(name = "{0}")
	@MethodSource("parseRollTestSource")
	public void parseRollTest(String description, Roll expected) {
		Roll actual = Roll.parseRoll(description);
		assertThat(actual, is(expected));
	}

	static Stream<Arguments> parseRollTestSource() {
		return Stream.of(
			Arguments.of("d3+0", new Roll(1, 3, 0)),
			Arguments.of("d2-0", new Roll(1, 2, 0)),
			Arguments.of("d8", new Roll(1, 8, 0)),
			Arguments.of("d10+1", new Roll(1, 10, 1)),
			Arguments.of("d20-3", new Roll(1, 20, -3)),
			Arguments.of("1d4", new Roll(1, 4, 0)),
			Arguments.of("2d6+10", new Roll(2, 6, 10)),
			Arguments.of("3d12-1", new Roll(3, 12, -1))
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("getNameTestSource")
	public void getNameTest(String expected, Roll target) {
		var actual = target.getName();
		assertThat(actual, is(expected));
	}

	static Stream<Arguments> getNameTestSource() {
		return Stream.of(
			Arguments.of("d8", new Roll(1, 8, 0)),
			Arguments.of("d10+1", new Roll(1, 10, 1)),
			Arguments.of("d20-3", new Roll(1, 20, -3)),
			Arguments.of("d4", new Roll(1, 4, 0)),
			Arguments.of("2d6+10", new Roll(2, 6, 10)),
			Arguments.of("3d12-1", new Roll(3, 12, -1))
		);
	}
	
	@ParameterizedTest(name = "{0} generates between {1} and {2}, with a mean of {3}")
	@MethodSource("throwDiceTestSource")  
	public void throwDiceTest(Roll target, int minimum, int maximum, double mean) {
		final int repititions = 10000;
		final double tolerance = .5;
		
		var actual = Stream.generate(() -> target.throwDice()).limit(repititions).collect(Collectors.toList());
		var actualMean = actual.stream().mapToInt(Integer::intValue).average().orElseThrow();
		
		assertAll(
			() -> assertThat(actual, everyItem(both(greaterThanOrEqualTo(minimum)).and(lessThanOrEqualTo(maximum)))),
			() -> assertThat(actualMean, closeTo(mean, tolerance))
		);
	}

	static Stream<Arguments> throwDiceTestSource() {
		return Stream.of(
			Arguments.of(new Roll(1, 8, 0), 1, 8, 4.5),
			Arguments.of(new Roll(1, 10, 1), 2, 11, 6.5),
			Arguments.of(new Roll(1, 20, -3), -2, 17, 7.5),
			Arguments.of(new Roll(1, 4, 0), 1, 4, 2.5),
			Arguments.of(new Roll(2, 6, 10), 12, 22, 17),
			Arguments.of(new Roll(3, 12, -1), 2, 35, 18.5)
		);
	}
	
	@Test
	public void uniformDistributionTest() {
		Roll target = new Roll(1, 8, 0);
		final int repititions = 10000;
		
		double[] expectedCounts = DoubleStream.generate(() -> repititions / 8d).limit(8).toArray();
		
		long[] actualCounts = createCounts(target, 1, 8, repititions);
		
		var chi = new ChiSquareTest();
		double pvalue = chi.chiSquareTest(expectedCounts, actualCounts);
		
		assertThat(pvalue, greaterThan(.05));
	}
	
	@Test
	public void binomialDistributionTest() {
		Roll target = new Roll(2, 6, 0);
		final int repititions = 10000;
		
		var probabilities = DoubleStream.of(1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1);
		double[] expectedCounts = probabilities.map(p -> (p / 36d) * repititions).toArray();
		
		long[] actualCounts = createCounts(target, 2, 12, repititions);
		
		var chi = new ChiSquareTest();
		double pvalue = chi.chiSquareTest(expectedCounts, actualCounts);
		
		assertThat(pvalue, greaterThan(.05));
	}
	
	private long[] createCounts(Roll roll, int min, int max, int repititions) {
		Map<Integer, Long> actualCounts = Stream.generate(() -> roll.throwDice())
			.limit(repititions)
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		return IntStream.rangeClosed(min, max)
			.mapToLong(i -> actualCounts.getOrDefault(i, 0L))
			.toArray();
	}

}
