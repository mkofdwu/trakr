package com.example.trakr.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trakr.R
import com.example.trakr.adapters.LegendItemRecyclerViewAdapter
import com.example.trakr.databinding.FragmentAnalyticsBinding
import com.example.trakr.models.LegendItem
import com.example.trakr.models.TimeEntry
import com.example.trakr.ui.misc.GridSpacingItemDecoration
import com.example.trakr.ui.misc.UnscrollableGridLayoutManager
import com.example.trakr.utils.Format
import com.example.trakr.viewmodels.DbViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.ListenerRegistration
import java.time.LocalDateTime

class AnalyticsFragment : Fragment() {
    private lateinit var binding: FragmentAnalyticsBinding
    private val dbViewModel: DbViewModel by activityViewModels()

    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_analytics, null, false)
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // setup charts
        listenerRegistration = dbViewModel.listenToTimeEntriesToday { timeEntries, _ ->
            if (timeEntries == null || timeEntries.isEmpty()) {
                binding.todayPieChart.root.visibility = View.GONE
                updatePlaceholderVisibility()
            } else {
                setupPieChart(timeEntries)
            }
        }
        dbViewModel.mostTimeSpent(LocalDateTime.now().minusWeeks(1), 10) {
            if (it.isEmpty()) {
                binding.mostTimeSpentBarChart.root.visibility = View.GONE
                updatePlaceholderVisibility()
            } else {
                setupBarChart(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    fun back() {
        findNavController().navigateUp()
    }

    fun goToHistory() {
        findNavController().navigate(R.id.action_analyticsFragment_to_historyFragment)
    }

    private fun updatePlaceholderVisibility() {
        if (binding.todayPieChart.root.visibility == View.GONE && binding.mostTimeSpentBarChart.root.visibility == View.GONE) {
            binding.placeholderContainer.visibility = View.VISIBLE
        } else {
            binding.placeholderContainer.visibility = View.GONE
        }
    }

    private fun generateColors(num: Int): List<Int> {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.primary)
        val colors = mutableListOf<Int>()
        for (i in 0.until(num)) {
            val hsv = FloatArray(3)
            Color.colorToHSV(primaryColor, hsv)
            hsv[0] = hsv[0] - 14 * i
            colors.add(Color.HSVToColor(hsv))
        }
        return colors
    }

    private fun setupPieChart(timeEntries: List<TimeEntry>) {
        val pieEntries = mutableListOf<PieEntry>()
        val colors = generateColors(timeEntries.size)
        val legendItems = mutableListOf<LegendItem>()
        val totalDuration = timeEntries.sumBy { it.duration.inSeconds.toInt() }
        for (i in 0.until(timeEntries.size)) {
            val timeEntry = timeEntries[i]
            val pieEntry = PieEntry(timeEntry.duration.inSeconds.toFloat(), timeEntry.title)
            pieEntries.add(pieEntry)
            val percentageStr =
                (100 * timeEntry.duration.inSeconds / totalDuration).toInt().toString() + "%"
            legendItems.add(LegendItem(colors[i], timeEntry.title, percentageStr))
        }
        binding.todayPieChart.chart.apply {
            description.isEnabled = false
            setDrawEntryLabels(false)
            holeRadius = 80f
            transparentCircleRadius = 80f
            legend.isEnabled = false
            val dataSet = PieDataSet(pieEntries, "")
            dataSet.setDrawValues(false)
            dataSet.colors = colors
            data = PieData(dataSet)
            invalidate()
            animateX(1000, Easing.EaseInOutCirc)
        }
        binding.todayPieChart.totalTimeTodayText.text = Format.abbrDuration(totalDuration.toLong())
        binding.todayPieChart.legend.apply {
            adapter = LegendItemRecyclerViewAdapter(legendItems)
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupBarChart(titlesToDuration: Map<String, Long>) {
        val barEntries = mutableListOf<BarEntry>()
        val colors = generateColors(titlesToDuration.size)
        val legendItems = mutableListOf<LegendItem>()
        for (i in 0.until(titlesToDuration.size)) {
            val title = titlesToDuration.keys.elementAt(i)
            val duration = titlesToDuration[title]!!
            val barEntry = BarEntry(i.toFloat(), duration.toFloat())
            barEntries.add(barEntry)
            legendItems.add(
                LegendItem(
                    colors[i],
                    title,
                    Format.abbrDuration(duration)
                )
            )
        }
        binding.mostTimeSpentBarChart.chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.spaceBottom = 0f
            xAxis.axisLineColor = Color.BLACK
            xAxis.axisLineWidth = 2f
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisLeft.setDrawAxisLine(false)
            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return Format.abbrDuration(value.toLong())
                }
            }
            axisRight.isEnabled = false
            val dataSet = BarDataSet(barEntries, "")
            dataSet.setDrawValues(false)
            dataSet.colors = colors
            data = BarData(dataSet)
            invalidate()
            animateY(1000, Easing.EaseInOutCirc)
        }
        binding.mostTimeSpentBarChart.legend.apply {
            adapter = LegendItemRecyclerViewAdapter(legendItems)
            layoutManager = UnscrollableGridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(2, 30, false))
        }
    }
}