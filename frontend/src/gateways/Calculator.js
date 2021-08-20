const baseUrl = "http://localhost:8081";

export default {
	async getAverageDamage(attack, damage) {
		const request = `${baseUrl}/calculate/${attack}/${damage}`;

		const response = await fetch(request);
		const results = await response.json();

		return results;
	},

	async getDamageHistogram(attack, damage, ac) {
		const request = `${baseUrl}/histogram/${attack}/${damage}/${ac}`;

		const response = await fetch(request);
		const results = await response.json();

		return results;
	}
}