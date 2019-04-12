package dndcalculator.api;

import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dndcalculator.db.DiceRepository;
import dndcalculator.model.Mesh;
import dndcalculator.model.MeshResult;

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
	public ResponseEntity<MeshResult> loadMesh(@PathVariable String name) {
		var result = repository.findByName(name);
		
		if (result == null) {
			return ResponseEntity.notFound().build();
		}
		
		var mesh = Mesh.parse(result.getObj());
		
		var vertices = mesh.getFaces()
				.flatMap(f -> f.getVerticesWithNormals())
				.flatMapToDouble(vn -> {
					var v = vn.getVertex();
					return DoubleStream.of(v.getX(), v.getY(), v.getZ());
				})
				.toArray();
		
		return ResponseEntity.ok(new MeshResult(vertices));
	}

}
