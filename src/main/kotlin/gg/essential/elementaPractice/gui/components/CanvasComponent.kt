package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementaPractice.utils.Interpolator
import gg.essential.elementaPractice.utils.Simplifier
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import java.awt.Color
import kotlin.math.sqrt

public class CanvasComponent : UIRoundedRectangle(8f) {

    // TODO Find a way to actually remove points from a line instead of just drawing over it with white

    private val lines = mutableListOf<Line>()
    private var oldWidth: Float = 0f
    private var oldHeight: Float = 0f
    public var lineColor: Color = Color.BLACK
    public var lineWidth: Float = 2f
    public var smoothing: Int = 5

    override fun draw(matrixStack: UMatrixStack) {
        beforeDrawCompat(matrixStack)

        super.draw(matrixStack)

        // Keep track of the old width and height to generate the correct ratios for when the screen size changes
        if (oldWidth == 0f) oldWidth = this@CanvasComponent.getWidth()
        if (oldHeight == 0f) oldHeight = this@CanvasComponent.getHeight()

        for (line in lines) {
            if (line.coordinates.size > 1) {
                line.draw(matrixStack, relatePoints(line))
            }
        }
    }

    // Update Line coordinates based on screen size, component location, and smoothing
    private fun relatePoints(line: Line): List<Coordinates> {
        val pointPairs = mutableListOf<Coordinates>()

        for (coord in line.coordinates) {
            // If component size changed, alter points based on their ratio to the original size
            val x =
                if (this@CanvasComponent.getWidth() == oldWidth) coord.first.toFloat() else (coord.first.toFloat() / oldWidth) * this@CanvasComponent.getWidth()
            val y =
                if (this@CanvasComponent.getHeight() == oldHeight) coord.second.toFloat() else (coord.second.toFloat() / oldHeight) * this@CanvasComponent.getHeight()

            // Make points relative by adding component top-left coords
            pointPairs.add(Coordinates(x, y))
        }

        // Smooth the coordinates
        return if (line.smoothing != 0 && pointPairs.size > 4) {
            Interpolator().interpolate(Simplifier().simplify(pointPairs), line.smoothing)
        } else {
            pointPairs
        }
    }

    // Draws a line from one coordinate to the next from a list of coordinates
    private fun Line.draw(matrixStack: UMatrixStack, coords: List<Coordinates>) {
        val worldRenderer = UGraphics.getFromTessellator()
        worldRenderer.beginWithDefaultShader(UGraphics.DrawMode.TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)

        coords.forEachIndexed { index, curr ->
            val (x, y) = curr
            val prev = coords.getOrNull(index - 1)
            val next = coords.getOrNull(index + 1)
            val (dx, dy) = when {
                prev == null -> next!!.sub(curr)
                next == null -> curr.sub(prev)
                else -> next.sub(prev)
            }
            val dLen = sqrt(dx * dx + dy * dy)
            val nx = dx / dLen * width / 2
            val ny = dy / dLen * width / 2
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
    private fun Coordinates.sub(other: Coordinates) =
        Pair(this.first.toDouble() - other.first.toDouble(), this.second.toDouble() - other.second.toDouble())

    /**
     * Add a new point to the last line
     *
     * @param coord: [Coordinates] - The coordinate point to add to the last line
     */
    public fun addPoint(coord: Coordinates) {
        // Start the first line if it hasn't been created, yet
        if (lines.isEmpty()) {
            lines.add(Line(mutableListOf(), lineColor, lineWidth, smoothing))
        }

        lines.last().coordinates.add(coord)
    }

    /**
     * Start a new line with the given parameters
     */
    public fun newLine() {
        // Create a new line if the last line is drawn
        if (lines.isEmpty() || lines.last().coordinates.isNotEmpty()) {
            lines.add(Line(mutableListOf(), lineColor, lineWidth, smoothing))
        }
        // Otherwise, modify the last line's properties, so it can be reused
        else {
            lines.last().color = lineColor
            lines.last().width = lineWidth
            lines.last().smoothing = smoothing
        }
    }

    /**
     * Clear the canvas
     */
    public fun clear(): Unit = lines.clear()

    public data class Line(
        val coordinates: MutableList<Coordinates>,
        var color: Color,
        var width: Float,
        var smoothing: Int
    )
}

public typealias Coordinates = Pair<Number, Number>