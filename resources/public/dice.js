
async function main() {
	const canvas = document.querySelector("#diceCanvas");
	const g = canvas.getContext("webgl");

	if (!g) {
		alert("Unable to initialize WebGL.");
		return;
	}
	
	const vsSource = `
		attribute vec4 aVertexPosition;
		
		uniform mat4 uModelViewMatrix;
		uniform mat4 uProjectionMatrix;
		
		void main() {
			gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;
		}
	`;

	const fsSource = `
		void main() {
			gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
		}
	`;

	const shaderProgram = initShaderProgram(g, vsSource, fsSource);
	const programInfo = {
		program: shaderProgram,
		attribLocations: {
			vertexPositions: g.getAttribLocation(shaderProgram, "aVertexPosition")
		},
		uniformLocations: {
			projectionMatrix: g.getUniformLocation(shaderProgram, "uProjectionMatrix"),
			modelViewMatrix: g.getUniformLocation(shaderProgram, "uModelViewMatrix")
		}
	}

	const buffers = await initBuffers(g);

	drawScene(g, programInfo, buffers);
}

async function initBuffers(g) {

	const positionBuffer = g.createBuffer();
	g.bindBuffer(g.ARRAY_BUFFER, positionBuffer);

	const mesh = await fetch("/mesh/d6").then(r => r.json());
	g.bufferData(g.ARRAY_BUFFER, new Float32Array(mesh.vertices), g.STATIC_DRAW);

	return {
		position: positionBuffer,
		mesh: mesh
	};
}

function drawScene(g, programInfo, buffers) {
	g.clearColor(0.0, 0.0, 0.0, 1.0);
	g.clearDepth(1.0);
	g.enable(g.DEPTH_TEST);
	g.depthFunc(g.LEQUAL);

	g.clear(g.COLOR_BUFFER_BIT | g.DEPTH_BUFFER_BIT);

	const fov = 45 * Math.PI / 180;
	const aspect = g.canvas.clientWidth / g.canvas.clientHeight;
	const zNear = .1;
	const zFar = 100;
	const projectionMatrix = mat4.create();
	mat4.perspective(projectionMatrix, fov, aspect, zNear, zFar);

	const modelViewMatrix = mat4.create();
	mat4.translate(modelViewMatrix, modelViewMatrix, [-0, 0, -3]);

	{
		const numComponents = 2;
		const type = g.FLOAT;
		const normalize = false;
		const stride = 0;
		const offset = 0;
		g.bindBuffer(g.ARRAY_BUFFER, buffers.position);
		g.vertexAttribPointer(
			programInfo.attribLocations.vertexPositions,
			numComponents,
			type,
			normalize,
			stride,
			offset
		);
		g.enableVertexAttribArray(
			programInfo.attribLocations.vertexPositions
		);
	}

	g.useProgram(programInfo.program);
	g.uniformMatrix4fv(
		programInfo.uniformLocations.projectionMatrix,
		false,
		projectionMatrix
	);
	g.uniformMatrix4fv(
		programInfo.uniformLocations.modelViewMatrix,
		false,
		modelViewMatrix
	);

	{
		const primitive = g[buffers.mesh.primitiveType];
		const offset = 0;
		const vertexCount = buffers.mesh.vertexCount;
		g.drawArrays(primitive, offset, vertexCount);
	}
}

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

function loadImage(path) {
	return new Promise((resolve, reject) => {
		const img = new Image();
		img.onload = () => resolve(img);
		img.onerror = reject;
		img.src = path;
	});
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

window.onload = main;