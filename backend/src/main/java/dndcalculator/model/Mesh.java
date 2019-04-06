package dndcalculator.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import lombok.Value;

@Value
public class Mesh {
		
	private final Face[] faces;
	
	public Stream<Face> getFaces() {
		return Arrays.stream(faces);
	}
	
	public static Mesh parseFile(String fileName) throws IOException {
		return parseFile(Paths.get(fileName));
	}
	
	public static Mesh parseFile(Path path) throws IOException {
		var lines = Files.readAllLines(path);
		
		var vertices = lines.stream().filter(l -> l.startsWith("v ")).map(Mesh::parseVertex).toArray(i -> new Vertex[i]);
		var normals = lines.stream().filter(l -> l.startsWith("vn")).map(Mesh::parseNormal).toArray(i -> new Normal[i]);
		
		var faces = lines.stream().filter(l -> l.startsWith("f")).map(l -> parseFace(l, vertices, normals)).toArray(i -> new Face[i]);
		
		return new Mesh(faces);
	}
	
	private static Vertex parseVertex(String l) {
		var columns = l.split(" ");
		var x = Double.parseDouble(columns[1]);
		var y = Double.parseDouble(columns[2]);
		var z = Double.parseDouble(columns[3]);
		return new Vertex(x, y, z);
	}
	
	private static Normal parseNormal(String l) {
		var columns = l.split(" ");
		var a = Double.parseDouble(columns[1]);
		var b = Double.parseDouble(columns[2]);
		var c = Double.parseDouble(columns[3]);
		return new Normal(a, b, c);
	}
	
	private static Face parseFace(String l, Vertex[] v, Normal[] n) {
		var pieces = l.split(" ");
		var vertices = new ArrayList<Vertex>();
		var normals = new ArrayList<Normal>();
		for (int i = 1; i < 4; i++) {
			var columns = pieces[i].split("/");
			int vi = Integer.parseInt(columns[0]) - 1;
			vertices.add(v[vi]);
			int ni = Integer.parseInt(columns[2]) - 1;
			normals.add(n[ni]);
		}
		
		return new Face(vertices.toArray(i -> new Vertex[i]), normals.toArray(i -> new Normal[i]));
	}
	
	@Value
	public static class Vertex {
		private final double x, y, z;
	}
	
	@Value
	public static class Normal {
		private final double a, b, c;
	}
	
	@Value
	public static class VertexWithNormal {
		private final Vertex vertex;
		private final Normal normal;
	}
	
	@Value
	public static class Face {
		private final Vertex[] vertices;
		private final Normal[] normals;
		
		public Stream<Vertex> getVertices() {
			return Arrays.stream(vertices);
		}
		
		public Stream<Normal> getNormals() {
			return Arrays.stream(normals);
		}
		
		public Stream<VertexWithNormal> getVerticesWithNormals() {
			var pairs = new ArrayList<VertexWithNormal>();
			for (int i = 0; i < vertices.length; i++) {
				pairs.add(new VertexWithNormal(vertices[i], normals[i]));
			}
			
			return pairs.stream();
		}
	}
}
