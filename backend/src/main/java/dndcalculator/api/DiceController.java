package dndcalculator.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiceController {
	
	@GetMapping("/dice/")
	public ResponseEntity<String[]> getDice() {
		return ResponseEntity.ok(new String[] { "d4", "d6" });
	}

}
