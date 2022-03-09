package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.dsl.*
import java.awt.Color

class WhiteboardComponent : UIComponent() {

    init {

        setColor(Color.BLACK)

        // Whiteboard Background/Outline
        val border = UIRoundedRectangle(8f).constrain {
            width = 100.percent()
            height = 100.percent()
            color = Color(100, 100, 100).toConstraint()
        } childOf this

        val drawArea = SplineComponent().constrain {
            x = 3.pixels()
            y = 3.pixels()
            width = 100.percent() - 6.pixels()
            height = 100.percent() - 6.pixels()
            color = Color.WHITE.toConstraint()
        } childOf border

        drawArea.onMouseClick {
            // Generate a new line with color after clicking
            drawArea.newLine(this@WhiteboardComponent.getColor())
        }.onMouseDrag { mouseX, mouseY, mouseButton ->
            // Make sure the dragged point is within the draw area
            if (!isPointInside(mouseX + getLeft(), mouseY + getTop())) return@onMouseDrag

            drawArea.addPoint(Pair(mouseX, mouseY))
        }.onMouseRelease {
            // Smooth (interpolate) the line after drawing
            drawArea.interpolateLast()
        }
    }

}