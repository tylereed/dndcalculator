var squareRotation = 0.0;

async function main() {
	
	const canvas = document.querySelector("#diceCanvas");
	const g = canvas.getContext("webgl");

	if (!g) {
		alert("Unable to initialize WebGL.");
		return;
	}
	
	const vsSource = `
		attribute vec4 aVertexPosition;
		attribute vec2 aTextureCoord;
		
		uniform mat4 uModelViewMatrix;
		uniform mat4 uProjectionMatrix;
		
		varying highp vec2 vTextureCoord;
		
		void main() {
			gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;
			vTextureCoord = aTextureCoord;
		}
	`;

	const fsSource = `
		varying highp vec2 vTextureCoord;
		
		uniform sampler2D uSampler;
		
		void main() {
			gl_FragColor = texture2D(uSampler, vTextureCoord);
		}
	`;

	const shaderProgram = initShaderProgram(g, vsSource, fsSource);
	const programInfo = {
		program: shaderProgram,
		attribLocations: {
			vertexPositions: g.getAttribLocation(shaderProgram, "aVertexPosition"),
			textureCoord: g.getAttribLocation(shaderProgram, "aTextureCoord")
		},
		uniformLocations: {
			projectionMatrix: g.getUniformLocation(shaderProgram, "uProjectionMatrix"),
			modelViewMatrix: g.getUniformLocation(shaderProgram, "uModelViewMatrix"),
			uSampler: g.getUniformLocation(shaderProgram, "uSampler")
		}
	}

	const buffers = await initBuffers(g);
	const texture = await initTexture(g, "d4.png");
	
	var then = 0;
	
	function render(now) {
		now *= .001;
		const deltaTime = now - then;
		then = now;
		
		drawScene(g, programInfo, buffers, texture, deltaTime);
		
		requestAnimationFrame(render);
	}
	
	requestAnimationFrame(render);
}

async function initBuffers(g) {
	
	const textureCoordBuffer = g.createBuffer();
	g.bindBuffer(g.ARRAY_BUFFER, textureCoordBuffer);
	
	const textureCoordinates = [
		//1,2,3
		0.0, 0.5,
		0.5, 0.5,
		0.25, 0.0,
		//1,2,4
		0.5, 0.5,
		1.0, 0.5,
		.75, 0.0,
		//1,3,4
		0.0, 1.0,
		0.5, 1.0,
		.25, .5,
		//4,3,2
		0.5, 1.0,
		1.0, 1.0,
		.75, .5
	];
	
	g.bufferData(g.ARRAY_BUFFER, new Float32Array(textureCoordinates), g.STATIC_DRAW);

	const positionBuffer = g.createBuffer();
	g.bindBuffer(g.ARRAY_BUFFER, positionBuffer);

	const mesh = await fetch("/mesh/d4").then(r => r.json());
	g.bufferData(g.ARRAY_BUFFER, new Float32Array(mesh.vertices), g.STATIC_DRAW);

	return {
		position: positionBuffer,
		textureCoord: textureCoordBuffer,
		mesh: mesh
	};
}

async function initTexture(g, url) {
	const image = await loadImage(url);
	
	const texture = g.createTexture();
	g.bindTexture(g.TEXTURE_2D, texture);
		
	g.texImage2D(g.TEXTURE_2D, 0, g.RGBA, g.RGBA, g.UNSIGNED_BYTE, image);
	
	g.texParameteri(g.TEXTURE_2D, g.TEXTURE_WRAP_S, g.CLAMP_TO_EDGE);
	g.texParameteri(g.TEXTURE_2D, g.TEXTURE_WRAP_T, g.CLAMP_TO_EDGE);
	g.texParameteri(g.TEXTURE_2D, g.TEXTURE_MIN_FILTER, g.LINEAR);
	
	return texture;
}

function drawScene(g, programInfo, buffers, texture, deltaTime) {
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
	
	mat4.rotate(modelViewMatrix,
			modelViewMatrix,
			squareRotation,
			[1, 0, 0]);
	
	mat4.rotate(modelViewMatrix,
			modelViewMatrix,
			squareRotation * 0.7,
			[0, 1, 0]);

	{
		const numComponents = 3;
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
	
	{
		const num = 2;
		const type = g.FLOAT;
		const normalize = false;
		const stride = 0;
		const offset = 0;
		g.bindBuffer(g.ARRAY_BUFFER, buffers.textureCoord);
		g.vertexAttribPointer(programInfo.attribLocations.textureCoord, num, type, normalize, stride, offset);
		g.enableVertexAttribArray(programInfo.attribLocations.textureCoord);
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
		g.activeTexture(g.TEXTURE0);
		g.bindTexture(g.TEXTURE_2D, texture);
		g.uniform1i(programInfo.uniformLocations.uSampler, 0);
		
		const primitive = g[buffers.mesh.primitiveType];
		const offset = 0;
		const vertexCount = buffers.mesh.vertexCount;
		g.drawArrays(primitive, offset, vertexCount);
	}
	squareRotation += deltaTime;
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