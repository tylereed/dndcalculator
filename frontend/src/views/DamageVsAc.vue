<template>
  <v-container>
    <v-container>
      <v-row>
        <v-col cols="3" v-for="(scenario, index) in scenarios" :key="index">
          <scenario
            :index="index"
            :scenario="scenario"
            :isLoading="loading[index]"
            width="75px"
            @change="recalc"
            @close="removeScenario"
          />
        </v-col>
      </v-row>
    </v-container>
    <v-container>
      <v-card>
        <v-btn @click="addScenario">Add</v-btn>
      </v-card>
    </v-container>
    <v-container>
      <v-card :loading="!!anyLoading">
        <average-damage :chart-data="lines" />
      </v-card>
    </v-container>
  </v-container>
</template>

<script>
import AverageDamage from "@/components/AverageDamage.vue";
import Scenario from "@/components/Scenario.vue";
import Calculator from "@/gateways/Calculator.js";
import generateColor from "@/gateways/colors.js";

export default {
  name: "DamageVsAc",
  components: {
    Scenario,
    AverageDamage,
  },
  data() {
    return {
      scenarios: [{ type: "AverageDamage", attack: "d20", damage: "d8" }],
      calculations: [],
      loading: [],
      anyLoading: 0,
      lines: {},
    };
  },
  methods: {
    addScenario() {
      this.scenarios.push({
        type: "AverageDamage",
        attack: "d20",
        damage: "d8",
      });
      this.recalc(this.scenarios.length - 1);
    },
    async recalc(index) {
      try {
        this.loading[index] = true;
        this.anyLoading++;
        const sim = await Calculator.getAverageDamage(
          this.scenarios[index].attack,
          this.scenarios[index].damage
        );
        this.calculations[index] = sim;
        this.lines = this.getLines();
      } finally {
        this.loading[index] = false;
        this.anyLoading--;
      }
    },
    removeScenario(index) {
      this.scenarios.splice(index, 1);
      this.calculations.splice(index, 1);
      this.loading.splice(index, 1);
      this.lines = this.getLines();
    },
    getLines() {
      const labels = Object.keys(this.calculations[0].results);
      return {
        labels: labels,
        datasets: this.calculations.map((c) => {
          const label = `${c.attack} / ${c.damage}`;
          const color = generateColor(label);
          return {
            data: labels.map((l) => c.results[l]),
            label: label,
            lineTension: 0,
            borderColor: color,
            backgroundColor: color + "4F",
          };
        }),
      };
    },
  },
  async beforeMount() {
    await this.recalc(0);
  },
};
</script>

<style scoped>
</style>
