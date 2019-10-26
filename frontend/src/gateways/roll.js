export default {
	name: 'roll',
	getThrows: async function* (diceName, number) {
		const response = await fetch("http://localhost:8081/roll/" + diceName + "/" + number);
		const dice = await response.json();

		for (let r of dice.rolls) {
			yield r;
		}
	}
}