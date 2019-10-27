const baseUrl = "http://localhost:8081/calculate";

export default {
	async getAverageDamage(attack, damage) {
		const request = `${baseUrl}/${attack}/${damage}`;

		const response = await fetch(request);
		const results = await response.json();

		return results;
	}
}