package dndcalculator.model;

import lombok.Value;

@Value
public class MeshResult {

	private final String primitiveType;
	private final double[] vertices;
	
	public MeshResult(double[] vertices) {
		this.primitiveType = "TRIANGLES";
		this.vertices = vertices;
	}
	
	public int getVertexCount() {
		return vertices.length / 3;
	}
	
}
