package dndcalculator.api;

import java.io.IOException;
import java.util.stream.DoubleStream;

import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dndcalculator.model.Mesh;
import dndcalculator.model.MeshResult;

@RestController
public class MeshController {

	@GetMapping("/mesh/{name}")
	public ResponseEntity<MeshResult> loadMesh(@PathVariable String name) throws IOException {
		var file = ResourceUtils.getFile("classpath:" + name + ".obj");
		var mesh = Mesh.parseFile(file.toPath());
		
		var vertices = mesh.getFaces()
				.flatMap(f -> f.getVerticesWithNormals())
				.flatMapToDouble(vn -> {
					var v = vn.getVertex();
					//var n = vn.getNormal();
					
					return DoubleStream.of(v.getX(), v.getY(), v.getZ());
					//,							n.getA(), n.getB(), n.getC());
				})
				.toArray();
		
		return ResponseEntity.ok(new MeshResult(vertices));
	}

}
