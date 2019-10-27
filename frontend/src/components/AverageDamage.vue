<template>
	<div class="container">
		<loading :active.sync="isLoading"></loading>
		<line-chart
			v-if="loaded"
			:chart-data="chartData"
			:options="options" />
	</div>
</template>

<script>
import LineChart from './LineChart.js';
import Calculator from '@/gateways/Calculator.js';
import Loading from 'vue-loading-overlay';
import 'vue-loading-overlay/dist/vue-loading.css';

function convert(calculation) {
	const labels = Object.keys(calculation.results);
	return {
		labels: labels,
		datasets:[{
			data: labels.map(l => calculation.results[l]),
			label: `${calculation.attack} / ${calculation.damage}`,
			lineTension: 0,
			color: "#ff0000"
		}]
	};
}

export default {
	name: 'AverageDamage',
	components: { LineChart, Loading },
	props: {
		attack: String,
		damage: String
	},
	data() {
    return {
			loaded: false,
			isLoading: false,
			chartData: null,
			options: { maintainAspectRatio: false }
    }
  },
	async mounted() {
		await this.loadChart();
	},
	methods: {		
		async loadChart() {
			this.loaded = false;
			try {
				this.isLoading = true;
				const chart = await Calculator.getAverageDamage(this.attack, this.damage);
				this.chartData = convert(chart);
				this.loaded = true;
			} catch (e) {
				console.error(e);
			} finally {
				this.isLoading = false;
			}
		}
	}
}
</script>

<style scoped>
/*#container {
	max-height: 723px;
}*/
</style>
