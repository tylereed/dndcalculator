package dndcalculator.api.result;

import lombok.Value;

@Value
public class DieResult {

	private final String primitiveType;
	private final double[] vertices;
	private final String texture;
	private final double[] textureCoords;
	
	public int getVertexCount() {
		return vertices.length / 3;
	}
	
}
