package com.ti.neurow.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

import com.ti.neurow.R

class PostWorkoutActivity : AppCompatActivity() {

    // on below line we are creating
    // variables for our graph view
    lateinit var lineGraphView: GraphView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_workout)

        // on below line we are initializing
        // our variable with their ids.
        lineGraphView = findViewById(R.id.Power_vs_Time)

        // on below line we are adding data to our graph view.
        //two example arraylists to mirror power and time ones that will be sent as parameters
        val x = ArrayList<Int>()
        x.add(1)
        x.add(2)
        x.add(3)
        x.add(4)
        println(x)
        val y = ArrayList<Int>()
        y.add(1)
        y.add(2)
        y.add(3)
        y.add(4)
        println(y)
        val length = x.size
        val dp = arrayOfNulls<DataPoint>(length)
        //TODO i cant assume that power and time are the same size??
        for (i in 0 until length) {
            dp[i] = DataPoint(x[i].toDouble(), y[i].toDouble())
            println(dp[i])
        }
        val series = LineGraphSeries(dp)

        // on below line adding animation
        lineGraphView.animate()

        // on below line we are setting scrollable
        // for point graph view
        lineGraphView.viewport.isScrollable = true

        // on below line we are setting scalable.
        lineGraphView.viewport.isScalable = true

        // on below line we are setting scalable y
        lineGraphView.viewport.setScalableY(true)

        // on below line we are setting scrollable y
        lineGraphView.viewport.setScrollableY(true)

        // on below line we are setting color for series.
        series.color = R.color.purple_200

        // on below line we are adding
        // data series to our graph view.
        lineGraphView.addSeries(series)
    }
}