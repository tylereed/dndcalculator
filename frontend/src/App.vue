<template>
  <div id="app">
    <scenario :scenario="scenario" @calc="doCalculation" />
    <average-damage ref="foo" :attack="scenario.attack" :damage="scenario.damage" />
  </div>
</template>

<script>
import AverageDamage from '@/components/AverageDamage.vue';
import Scenario from '@/components/Scenario.vue';

import Calculator from '@/gateways/Calculator.js';

export default {
  name: 'app',
  components: {
    Scenario,
    AverageDamage
  },
  data() {
    return {
      scenario: {
        attack: "d20",
        damage: "2d6",
        ac: 13,
        health: 25
      }
    }
  },
  methods: {
    doCalculation() {
      this.$refs.foo.loadChart();
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 30px;
}
</style>
