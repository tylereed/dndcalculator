'use strict';

function initShaderProgram(g, vsSource, fsSource) {
	const vertexShader = loadShader(g, g.VERTEX_SHADER, vsSource);
	const fragmentShader = loadShader(g, g.FRAGMENT_SHADER, fsSource);

	const program = g.createProgram();
	g.attachShader(program, vertexShader);
	g.attachShader(program, fragmentShader);
	g.linkProgram(program);

	if (!g.getProgramParameter(program, g.LINK_STATUS)) {
		alert("Unable to initialize the shader program: " + g.getProgramInfoLog(program));
		return null;
	}

	return program;
}

function loadShader(g, type, source) {
	const shader = g.createShader(type);
	g.shaderSource(shader, source);
	g.compileShader(shader);
	if (!g.getShaderParameter(shader, g.COMPILE_STATUS)) {
		alert("An error occurred compiling the shaders: " + g.getShaderInfoLog(shader));
		g.deleteShader(shader);
		return null;
	}
	return shader;
}