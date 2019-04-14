package dndcalculator.model;

import java.util.Arrays;

public class TextureCoordinates {
	
	public static double[] parseCoords(String body) {
		var lines = Arrays.stream(body.split("\n"));
		
		return lines.filter(l -> !l.startsWith("//"))
			.flatMap(l -> Arrays.stream(l.split(",")))
			.filter(t -> !t.isBlank())
			.mapToDouble(t -> {
				if (t.contains("/")) {
					var c = t.split("/");
					return Double.parseDouble(c[0]) / Double.parseDouble(c[1]);
				} else {
					return Double.parseDouble(t);
				}
			})
			.toArray();
	}

}
