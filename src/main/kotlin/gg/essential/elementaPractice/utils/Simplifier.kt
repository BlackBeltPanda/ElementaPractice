package gg.essential.elementaPractice.utils

import gg.essential.elementa.components.UIPoint
import kotlin.math.hypot

class Simplifier {

    private fun perpendicularDistance(pt: UIPoint, lineStart: UIPoint, lineEnd: UIPoint): Float {
        var dx = lineEnd.relativeX - lineStart.relativeX
        var dy = lineEnd.relativeY - lineStart.relativeY

        // Normalize
        val mag = hypot(dx, dy)
        if (mag > 0.0) { dx /= mag; dy /= mag }
        val pvx = pt.relativeX - lineStart.relativeX
        val pvy = pt.relativeY - lineStart.relativeY

        // Get dot product (project pv onto normalized direction)
        val pvdot = dx * pvx + dy * pvy

        // Scale line direction vector and substract it from pv
        val ax = pvx - pvdot * dx
        val ay = pvy - pvdot * dy

        return hypot(ax, ay)
    }

    private fun ramerDouglasPeucker(pointList: List<UIPoint>, epsilon: Double, out: MutableList<UIPoint>) {
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
            val recResults1 = mutableListOf<UIPoint>()
            val recResults2 = mutableListOf<UIPoint>()
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

    fun simplify(points: List<UIPoint>): List<UIPoint> {
        val pointListOut = mutableListOf<UIPoint>()
        ramerDouglasPeucker(points, 0.7, pointListOut)
        return pointListOut
    }

}