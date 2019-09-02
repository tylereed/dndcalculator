package dndcalculator.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import dndcalculator.db.Dice;
import dndcalculator.db.DiceRepository;

public class DiceControllerTest {

	@Test
	public void getDiceTest() {
		var mockRepo = mock(DiceRepository.class);
		when(mockRepo.getDiceNames()).thenReturn(Stream.of("d4", "d6", "d8"));

		var controller = new DiceController(mockRepo);

		var response = controller.getDice();
		var actual = response.getBody();

		assertAll(() -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
				() -> assertThat(actual, arrayContainingInAnyOrder("d4", "d6", "d8")));

	}

	@Test
	public void loadDie_NullName() {
		var mockRepo = mock(DiceRepository.class);
		when(mockRepo.findByName("d7")).thenReturn(null);

		var controller = new DiceController(mockRepo);

		var response = controller.loadDie("d7");

		assertAll(() -> assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND)),
				() -> verify(mockRepo).findByName("d7"), () -> verifyNoMoreInteractions(mockRepo));
	}

	@Test
	public void loadDie_D6Name() {
		var mockRepo = mock(DiceRepository.class);
		var die = new Dice();
		die.setName("d6");
		die.setPrimitiveType("RECT");
		die.setTexture("d6.png");
		die.setObj(String.join("\n",
				"v -0.500000 -0.500000 0.500000",
				"v -0.500000 0.500000 0.500000",
				"v -0.500000 -0.500000 -0.500000",
				"vn 0 0 0",
				"vn 0 0 0",
				"vn 0 0 0",
				"f 2//1 3//1 1//1"));
		die.setTextureCoords(String.join("\n", "1/3, 0.5,", "0.0, 1.0,", "1/3, 1.0,"));

		when(mockRepo.findByName("d6")).thenReturn(die);

		var controller = new DiceController(mockRepo);

		var response = controller.loadDie("d6");
		var actual = response.getBody();
		double[] expectedTextureCoords = new double[] { .33333, .5, 0, 1, .33333, 1 };
		double[] expectedVertices = new double[] { -.5, .5, .5, -.5, -.5, -.5, -.5, -.5, .5 };

		assertAll(() -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
				() -> verify(mockRepo).findByName("d6"), () -> verifyNoMoreInteractions(mockRepo),
				() -> assertThat(actual.getPrimitiveType(), is("RECT")),
				() -> assertThat(actual.getTexture(), is("d6.png")),
				() -> assertArrayEquals(expectedTextureCoords, actual.getTextureCoords(), .00001),
				() -> assertThat(actual.getVertexCount(), is(3)),
				() -> assertArrayEquals(expectedVertices, actual.getVertices(), .00001));

	}

}
