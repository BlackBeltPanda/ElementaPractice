package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementaPractice.utils.Interpolator
import gg.essential.elementaPractice.utils.Simplifier
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import java.awt.Color
import kotlin.math.sqrt

class SplineComponent constructor(
    private val lineWidth: Float = 3f
) : UIRoundedRectangle(8f) {

    private val lines = mutableListOf<List<Line>>()
    private var oldWidth: Float = 0f
    private var oldHeight: Float = 0f
    private var lineColor: Color = Color.BLACK

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        super.draw(matrixStack)

        // Keep track of the old width and height to generate the correct ratios for when the screen size changes
        if (oldWidth == 0f) oldWidth = getWidth()
        if (oldHeight == 0f) oldHeight = getHeight()

            for (line in lines) {
                for (pair in line) {
                    if (pair.first.size > 1) drawLineStrip(matrixStack, relatePoints(pair.first), pair.second)
                }
            }
    }

    // Create a list with points modified based on screen size and component location
    private fun relatePoints(pairs: List<Coordinates>): List<Coordinates> {
        val pointPairs = mutableListOf<Coordinates>()

        for (point in pairs) {
            // If component size changed, alter points based on their ratio to the original size
            val x =
                if (getWidth() == oldWidth) point.first.toFloat() else (point.first.toFloat() / oldWidth) * getWidth()
            val y =
                if (getHeight() == oldHeight) point.second.toFloat() else (point.second.toFloat() / oldHeight) * getHeight()

            // Make points relative by adding component top-left coords
            pointPairs.add(Coordinates(x + this@SplineComponent.getLeft(), y + this@SplineComponent.getTop()))
        }

        return pointPairs
    }

    // Draws a line from one coordinate to the next from a list of coordinates
    private fun drawLineStrip(matrixStack: UMatrixStack, pointPairs: List<Coordinates>, color: Color) {
        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.beginWithDefaultShader(UGraphics.DrawMode.TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)

        pointPairs.forEachIndexed { index, curr ->
            val (x, y) = curr
            val prev = pointPairs.getOrNull(index - 1)
            val next = pointPairs.getOrNull(index + 1)
            val (dx, dy) = when {
                prev == null -> next!!.sub(curr)
                next == null -> curr.sub(prev)
                else -> next.sub(prev)
            }
            val dLen = sqrt(dx * dx + dy * dy)
            val nx = dx / dLen * lineWidth / 2
            val ny = dy / dLen * lineWidth / 2
            worldRenderer.pos(matrixStack, x.toDouble() + ny, y.toDouble() - nx, 1.0)
                .color(color)
                .endVertex()
            worldRenderer.pos(matrixStack, x.toDouble() - ny, y.toDouble() + nx, 1.0)
                .color(color)
                .endVertex()
        }

        worldRenderer.drawDirect()
    }

    // Subtracts one coordinate from another
    private fun Coordinates.sub(other: Coordinates) = Pair(this.first.toDouble() - other.first.toDouble(), this.second.toDouble() - other.second.toDouble())

    // Run the simplifier and interpolator on the last line drawn
    fun interpolateLast() {
        val lastLine = lines.last().last()
        if (lastLine.first.size > 2) {
            val interped = Interpolator().interpolate(Simplifier().simplify(lastLine.first))
            lastLine.first.clear()
            lastLine.first.addAll(interped)
        }
    }

    // Add a new coordinate to the last drawn line
    fun addPoint(point: Coordinates) {
        if (lines.isEmpty()) {
            lines.add(listOf(Pair(mutableListOf(), lineColor)))
        }
        lines.last().last().first.add(point)
    }

    // Add a new empty line with the given color
    fun newLine(color: Color) {
        lineColor = color
        lines.add(listOf(Pair(mutableListOf(), lineColor)))
    }
}

typealias Coordinates = Pair<Number, Number>
typealias Line = Pair<MutableList<Coordinates>, Color>