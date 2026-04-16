<template>
  <div ref="chartRef" class="base-chart"></div>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { use, init, type ECharts, type EChartsCoreOption } from 'echarts/core'

use([LineChart, BarChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const props = defineProps<{
  option: EChartsCoreOption
}>()

const chartRef = ref<HTMLDivElement | null>(null)
let chart: ECharts | null = null

function renderChart() {
  if (!chartRef.value) return
  if (!chart) {
    chart = init(chartRef.value)
  }

  chart.setOption(props.option, true)
}

function handleResize() {
  chart?.resize()
}

onMounted(async () => {
  await nextTick()
  renderChart()
  window.addEventListener('resize', handleResize)
})

watch(
  () => props.option,
  () => {
    renderChart()
  },
  { deep: true },
)

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})
</script>

<style scoped>
.base-chart {
  width: 100%;
  height: 100%;
  min-height: 280px;
}
</style>
