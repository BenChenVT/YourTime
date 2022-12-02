package com.example.yourtime

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportFragment : Fragment() {

    //pie chart view
    private lateinit var reportPieChart: PieChart
    private lateinit var text: TextView
    private lateinit var button: Button

    var cal = Calendar.getInstance()

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
        button = view.findViewById(R.id.button)
        text = view.findViewById(R.id.input)

        var cal = Calendar.getInstance()

        val model = TimeViewModel()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            text.text = sdf.format(cal.time)

        }

        button.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

            val entriesForOneDay = ArrayList<Event>()
            
            loadPieChart(calculateTime(model.allEvents.value!!))
        }

        setupPieChart()

        return view
    }

    private fun calculateTime(events: List<Event>) : FloatArray {
        var work: Float = 0.1f
        var exercise: Float = 0.2f
        var restanrant: Float = 0.3f
        var other: Float = 0.4f
        var sum: Float = 0f




        return floatArrayOf(work, exercise, restanrant, other)
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

    fun loadPieChart(arr: FloatArray) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(arr[0], "work"))
        entries.add(PieEntry(arr[1], "exercise"))
        entries.add(PieEntry(arr[2], "restaurant"))
        entries.add(PieEntry(arr[3], "other"))

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