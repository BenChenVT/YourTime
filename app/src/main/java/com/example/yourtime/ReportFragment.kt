package com.example.yourtime

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate


/**
 * A simple [Fragment] subclass.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportFragment : Fragment() {

    //pie chart view
    private lateinit var reportPieChart: PieChart
    private lateinit var dateSelector: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        reportPieChart = view.findViewById(R.id.reportPieChart)
        dateSelector = view.findViewById(R.id.dataSelector)
        setupPieChart()
        loadPieChart()
        return view
    }

    private fun setupPieChart() {
        reportPieChart.setDrawHoleEnabled(true)
        reportPieChart.setUsePercentValues(true)
        reportPieChart.setEntryLabelTextSize(12F)
        reportPieChart.setEntryLabelColor(Color.BLACK)
        reportPieChart.setCenterText("Your Time")
        reportPieChart.setCenterTextSize(24F)
        reportPieChart.getDescription().setEnabled(false)
        val l: Legend = reportPieChart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = true
    }

    fun loadPieChart() {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(0.25f, "activity1"))
        entries.add(PieEntry(0.33f, "activity2"))
        entries.add(PieEntry(0.25f, "activity3"))
        entries.add(PieEntry(0.17f, "activity4"))

        val colors = ArrayList<Int>()
        for (i in ColorTemplate.MATERIAL_COLORS) {
            colors.add(i)
        }
        val dataSet = PieDataSet(entries, "time")
        dataSet.setColors(colors)

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(reportPieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        reportPieChart.setData(data);
        reportPieChart.invalidate();

        reportPieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}