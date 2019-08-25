package dndcalculator.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import dndcalculator.model.RollResult;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RollControllerTest {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void doRollTest_NoCount() {
		
		var response = restTemplate.getForEntity("http://localhost:" + port + "/roll/1d8", RollResult.class);
		var rollResult = response.getBody();
		
		assertAll(
			() -> assertThat(rollResult.getName(), is("d8")),
			() -> assertThat(rollResult.getRolls(), hasSize(1)),
			() -> assertThat(rollResult.getRolls(), everyItem(both(greaterThanOrEqualTo(1)).and(lessThanOrEqualTo(8)))),
			() -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
			() -> assertThat(response.getHeaders().getCacheControl(), containsString("no-store"))
		);
	}

	@Test
	public void doRollTest_WithCount() {
		
		var response = restTemplate.getForEntity("http://localhost:" + port + "/roll/1d8/2", RollResult.class);
		var rollResult = response.getBody();
		
		assertAll(
			() -> assertThat(rollResult.getName(), is("d8")),
			() -> assertThat(rollResult.getRolls(), hasSize(2)),
			() -> assertThat(rollResult.getRolls(), everyItem(both(greaterThanOrEqualTo(1)).and(lessThanOrEqualTo(8)))),
			() -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
			() -> assertThat(response.getHeaders().getCacheControl(), containsString("no-store"))
		);
	}
	
}
