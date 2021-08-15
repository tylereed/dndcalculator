<template>
  <v-card>
    <line-chart :chart-data="chartData" :options="options" />
  </v-card>
</template>

<script>
import LineChart from "./LineChart.js";

export default {
  name: "AverageDamage",
  components: { LineChart },
  props: {
    chartData: Object,
  },
  data() {
    return {
      options: { maintainAspectRatio: false },
    };
  },
};
</script>

<style scoped>
</style>
