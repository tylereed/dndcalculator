'use strict';

var squareRotation = 0.0;

function init() {
	const g = document.querySelector("#diceCanvas").getContext("webgl");

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
	return {
		g: g,
		programInfo: {
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
	}
}

async function drawLoop(g, programInfo, dieType) {

	const die = await fetch("/dice/" + dieType).then(r => r.json());
	const image = await loadImage(die.texture);

	const buffers = initBuffers(g, die);
	const texture = initTexture(g, image);
	
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

function initBuffers(g, die) {
	const textureCoordBuffer = g.createBuffer();
	g.bindBuffer(g.ARRAY_BUFFER, textureCoordBuffer);
	
	g.bufferData(g.ARRAY_BUFFER, new Float32Array(die.textureCoords), g.STATIC_DRAW);

	const positionBuffer = g.createBuffer();
	g.bindBuffer(g.ARRAY_BUFFER, positionBuffer);

	g.bufferData(g.ARRAY_BUFFER, new Float32Array(die.vertices), g.STATIC_DRAW);

	return {
		position: positionBuffer,
		textureCoord: textureCoordBuffer,
		die: die
	};
}

function loadImage(path) {
	return new Promise((resolve, reject) => {
		const img = new Image();
		img.onload = () => resolve(img);
		img.onerror = reject;
		img.src = path;
	});
}

function initTexture(g, image) {	
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
		
		const primitive = g[buffers.die.primitiveType];
		const offset = 0;
		const vertexCount = buffers.die.vertexCount;
		g.drawArrays(primitive, offset, vertexCount);
	}
	squareRotation += deltaTime;
}