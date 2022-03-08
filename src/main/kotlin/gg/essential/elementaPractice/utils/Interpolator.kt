package gg.essential.elementaPractice.utils

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIPoint
import gg.essential.elementa.dsl.childOf

class Interpolator {

    fun interpolate(points: MutableList<UIPoint>, whiteboard: UIComponent): MutableList<UIPoint> {
        val start = points[1]
        val end = points[points.size - 2]

        points.add(0, start)
        points.add(end)

        val result = mutableListOf<UIPoint>()
        for (i in 0 until points.size - 3) {
            val intPoints = interpolate(points, i, whiteboard)
            if (result.size > 0) {
                intPoints.removeAt(0)
            }
            result.addAll(intPoints)
        }

        return result
    }

    private fun interpolate(points: MutableList<UIPoint>, index: Int, whiteboard: UIComponent): MutableList<UIPoint> {
        val result = mutableListOf<UIPoint>()
        val x = Array(4) { Double.NaN }
        val y = Array(4) { Double.NaN }
        val time = Array(4) { Double.NaN }
        for (i in 0 until 4) {
            x[i] = (points[index + i].absoluteX - whiteboard.getLeft()).toDouble()
            y[i] = (points[index + i].absoluteY - whiteboard.getTop()).toDouble()
            time[i] = i.toDouble()
        }

        val tstart = 1.0
        val tend = 2.0
        val segments = 5
        result.add(points[index + 1])
        for (i in 1 until segments) {
            val xi = interpolate(x, time, tstart + (i * (tend - tstart)) / segments)
            val yi = interpolate(y, time, tstart + (i * (tend - tstart)) / segments)
            result.add(UIPoint(xi, yi) childOf whiteboard)
        }
        result.add(points[index + 2])
        return result
    }

    private fun interpolate(p: Array<Double>, time: Array<Double>, t: Double): Double {
        val l01 = p[0] * (time[1] - t) / (time[1] - time[0]) + p[1] * (t - time[0]) / (time[1] - time[0])
        val l12 = p[1] * (time[2] - t) / (time[2] - time[1]) + p[2] * (t - time[1]) / (time[2] - time[1])
        val l23 = p[2] * (time[3] - t) / (time[3] - time[2]) + p[3] * (t - time[2]) / (time[3] - time[2])
        val l012 = l01 * (time[2] - t) / (time[2] - time[0]) + l12 * (t - time[0]) / (time[2] - time[0])
        val l123 = l12 * (time[3] - t) / (time[3] - time[1]) + l23 * (t - time[1]) / (time[3] - time[1])
        return l012 * (time[2] - t) / (time[2] - time[1]) + l123 * (t - time[1]) / (time[2] - time[1])
    }
}