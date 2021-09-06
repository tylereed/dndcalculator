const baseUrl = "http://localhost:8081";

export default {
	execute(url) {
		return fetch(url).then(r => r.json());
	},

	getAverageDamage(attack, damage) {
		return this.execute(`${baseUrl}/calculate/${attack}/${damage}`);
	},

	getDamageHistogram(attack, damage, ac) {
		return this.execute(`${baseUrl}/histogram/${attack}/${damage}/${ac}`);
	},

	getRoundHistogram(attack, damage, ac, health) {
		return this.execute(`${baseUrl}/rounds/${attack}/${damage}/${ac}/${health}`);
	}
}