package gg.essential.elementaPractice.utils

import gg.essential.elementaPractice.gui.components.Coordinate
import kotlin.math.hypot

public class Simplifier {

    /**
     * Simplify the points, removing excess points to create a simpler spline
     *
     * @param coords: MutableList<[Coordinate]> - Coordinates (Pair<Number, Number>) to simplify
     * @param epsilon: Double - The epsilon (amount of simplification) to apply. A higher epsilon creates more simplification
     * @return MutableList<[Coordinate]> - List of simplified coordinates
     */
    public fun simplify(coords: List<Coordinate>, epsilon: Double = 0.8): MutableList<Coordinate> {
        val pointListOut = mutableListOf<Coordinate>()
        ramerDouglasPeucker(coords, epsilon, pointListOut)
        return pointListOut
    }

    private fun perpendicularDistance(pt: Coordinate, lineStart: Coordinate, lineEnd: Coordinate): Float {
        var dx = lineEnd.first.toFloat() - lineStart.first.toFloat()
        var dy = lineEnd.second.toFloat() - lineStart.second.toFloat()

        // Normalize
        val mag = hypot(dx, dy)
        if (mag > 0.0) {
            dx /= mag; dy /= mag
        }
        val pvx = pt.first.toFloat() - lineStart.first.toFloat()
        val pvy = pt.second.toFloat() - lineStart.second.toFloat()

        // Get dot product (project pv onto normalized direction)
        val pvdot = dx * pvx + dy * pvy

        // Scale line direction vector and substract it from pv
        val ax = pvx - pvdot * dx
        val ay = pvy - pvdot * dy

        return hypot(ax, ay)
    }

    private fun ramerDouglasPeucker(coordList: List<Coordinate>, epsilon: Double, out: MutableList<Coordinate>) {
        if (coordList.size < 2) throw IllegalArgumentException("Not enough points to simplify")

        // Find the point with the maximum distance from line between start and end
        var dmax = 0f
        var index = 0
        val end = coordList.size - 1
        for (i in 1 until end) {
            val d = perpendicularDistance(coordList[i], coordList[0], coordList[end])
            if (d > dmax) {
                index = i; dmax = d
            }
        }

        // If max distance is greater than epsilon, recursively simplify
        if (dmax > epsilon) {
            val recResults1 = mutableListOf<Coordinate>()
            val recResults2 = mutableListOf<Coordinate>()
            val firstLine = coordList.take(index + 1)
            val lastLine = coordList.drop(index)
            ramerDouglasPeucker(firstLine, epsilon, recResults1)
            ramerDouglasPeucker(lastLine, epsilon, recResults2)

            // build the result list
            out.addAll(recResults1.take(recResults1.size - 1))
            out.addAll(recResults2)
            if (out.size < 2) throw RuntimeException("Problem assembling output")
        } else {
            // Just return start and end points
            out.clear()
            out.add(coordList.first())
            out.add(coordList.last())
        }
    }

}