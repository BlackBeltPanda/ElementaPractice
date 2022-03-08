package gg.essential.elementaPractice

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIPoint
import gg.essential.elementa.constraints.WidthConstraint
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import java.awt.Color
import kotlin.math.sqrt

class Spline @JvmOverloads constructor(
    private val points: List<UIPoint>,
    lineColor: Color = Color.BLACK,
    width: WidthConstraint = 3f.pixels()
) : UIComponent() {

    init {
        setColor(lineColor.toConstraint())
        setWidth(width)
    }

    fun getPoints() : List<UIPoint> = points

    private fun drawLineStrip(matrixStack: UMatrixStack, pointPairs: List<Pair<Number, Number>>) {
        beforeDrawCompat(matrixStack)
        UGraphics.enableBlend()

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
            val nx = dx / dLen * getWidth() / 2
            val ny = dy / dLen * getWidth() / 2
            worldRenderer.pos(matrixStack, x.toDouble() + ny, y.toDouble() - nx, 0.0)
                .color(getColor())
                .endVertex()
            worldRenderer.pos(matrixStack, x.toDouble() - ny, y.toDouble() + nx, 0.0)
                .color(getColor())
                .endVertex()
        }
        worldRenderer.drawDirect()

        UGraphics.disableBlend()
    }

    private fun Pair<Number, Number>.sub(other: Pair<Number, Number>): Pair<Double, Double> {
        val (x1, y1) = this
        val (x2, y2) = other
        return Pair(x1.toDouble() - x2.toDouble(), y1.toDouble() - y2.toDouble())
    }

    override fun draw(matrixStack: UMatrixStack) {
        // Convert list of UIPoints to Pairs of X,Y coordinates
        val pointPairs = mutableListOf<Pair<Number, Number>>()
        for (point in points) {
            pointPairs.add(Pair(point.relativeX, point.relativeY))
        }

        //LineUtils.drawLineStrip(matrixStack, pointPairs, getColor(), getWidth())

        drawLineStrip(matrixStack, pointPairs)

        super.draw(matrixStack)
    }
}