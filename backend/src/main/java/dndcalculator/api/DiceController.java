package dndcalculator.api;

import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dndcalculator.api.result.DieResult;
import dndcalculator.db.DiceRepository;
import dndcalculator.model.Mesh;
import dndcalculator.model.TextureCoordinates;

@RestController
public class DiceController {

	private DiceRepository repository;
	
	@Autowired
	public DiceController(DiceRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/dice/")
	@Transactional(readOnly = true)
	public ResponseEntity<String[]> getDice() {
		String[] allDice;
		try (var stream = repository.getDiceNames()) {
			allDice = stream.toArray(s -> new String[s]);
		}
		
		return ResponseEntity.ok(allDice);
	}

	@GetMapping("/dice/{name}")
	@Transactional(readOnly = true)
	public ResponseEntity<DieResult> loadDie(@PathVariable String name) {
		var die = repository.findByName(name);
		
		if (die == null) {
			return ResponseEntity.notFound().build();
		}
		
		var mesh = Mesh.parse(die.getObj());
		var vertices = mesh.getFaces()
				.flatMap(f -> f.getVertices())
				.flatMapToDouble(v -> DoubleStream.of(v.getX(), v.getY(), v.getZ()))
				.toArray();
		
		var coords = TextureCoordinates.parseCoords(die.getTextureCoords());
		
		return ResponseEntity.ok(new DieResult(die.getPrimitiveType(), vertices, die.getTexture(), coords));
	}

}
