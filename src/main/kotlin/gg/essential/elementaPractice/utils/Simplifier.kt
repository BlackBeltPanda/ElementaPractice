package gg.essential.elementaPractice.utils

import gg.essential.elementaPractice.gui.components.Coordinates
import kotlin.math.hypot

class Simplifier {

    /**
     * Simplify the points, removing excess points to create a simpler spline
     * @param points: MutableList<Coordinates> - Coordinates (Pair<Number, Number>) to simplify
     * @return MutableList<Coordinates> - List of simplified coordinates
     */
    fun simplify(points: List<Coordinates>): MutableList<Coordinates> {
        val pointListOut = mutableListOf<Coordinates>()
        ramerDouglasPeucker(points, 0.7, pointListOut)
        return pointListOut
    }

    private fun perpendicularDistance(pt: Coordinates, lineStart: Coordinates, lineEnd: Coordinates): Float {
        var dx = lineEnd.first.toFloat() - lineStart.first.toFloat()
        var dy = lineEnd.second.toFloat() - lineStart.second.toFloat()

        // Normalize
        val mag = hypot(dx, dy)
        if (mag > 0.0) { dx /= mag; dy /= mag }
        val pvx = pt.first.toFloat() - lineStart.first.toFloat()
        val pvy = pt.second.toFloat() - lineStart.second.toFloat()

        // Get dot product (project pv onto normalized direction)
        val pvdot = dx * pvx + dy * pvy

        // Scale line direction vector and substract it from pv
        val ax = pvx - pvdot * dx
        val ay = pvy - pvdot * dy

        return hypot(ax, ay)
    }

    private fun ramerDouglasPeucker(pointList: List<Coordinates>, epsilon: Double, out: MutableList<Coordinates>) {
        if (pointList.size < 2) throw IllegalArgumentException("Not enough points to simplify")

        // Find the point with the maximum distance from line between start and end
        var dmax = 0f
        var index = 0
        val end = pointList.size - 1
        for (i in 1 until end) {
            val d = perpendicularDistance(pointList[i], pointList[0], pointList[end])
            if (d > dmax) { index = i; dmax = d }
        }

        // If max distance is greater than epsilon, recursively simplify
        if (dmax > epsilon) {
            val recResults1 = mutableListOf<Coordinates>()
            val recResults2 = mutableListOf<Coordinates>()
            val firstLine = pointList.take(index + 1)
            val lastLine  = pointList.drop(index)
            ramerDouglasPeucker(firstLine, epsilon, recResults1)
            ramerDouglasPeucker(lastLine, epsilon, recResults2)

            // build the result list
            out.addAll(recResults1.take(recResults1.size - 1))
            out.addAll(recResults2)
            if (out.size < 2) throw RuntimeException("Problem assembling output")
        }
        else {
            // Just return start and end points
            out.clear()
            out.add(pointList.first())
            out.add(pointList.last())
        }
    }

}