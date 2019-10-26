package dndcalculator.api;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dndcalculator.model.Roll;
import dndcalculator.model.RollResult;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class RollController {
	
	@GetMapping({"/roll/{dice}", "/roll/{dice}/{count}"})
	public ResponseEntity<RollResult> doRoll(@PathVariable String dice,
			@PathVariable(required = false) Optional<Integer> count) {
		
		var roll = Roll.parseRoll(dice);
		var rolls = Stream.generate(() -> roll.throwDice()).limit(count.orElse(1)).collect(Collectors.toList());

		return ResponseEntity.ok()
				.cacheControl(CacheControl.noStore())
				.body(new RollResult(roll.getName(), rolls));
	}

}
