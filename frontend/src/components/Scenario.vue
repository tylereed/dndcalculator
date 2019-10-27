<template>
	<div id="scenario">
		<label for="attack">Attack Dice:</label> 
		<input type="text" id="attack" v-model="scenario.attack" />

		<label for="damage">Damage Dice:</label>
		<input type="text" id="damage" v-model="scenario.damage" />

		<!--
		<label for="ac">Enemy AC:</label>         
		<input type="text" id="ac" v-model="scenario.ac" />

		<label for="health">Enemy Health:</label>
		<input type="text" id="health" v-model="scenario.health" />
		-->

		<input type="button" id="calc" @click="$emit('calc')" value="Calculate" />
	</div>
</template>

<script>
export default {
	name: 'scenario',
	props: {
		scenario: Object
	}
}
</script>

<style scoped>
	#scenario {
		display: grid;
		grid-template-columns: 2fr 1fr 2fr 2fr;
		grid-gap: .67em;
	}

	label {
		text-align: right;
		grid-column: 2 / 3;
	}

	input {
		grid-column: 3 / 4;
	}
</style>