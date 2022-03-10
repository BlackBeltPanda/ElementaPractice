package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.dsl.*
import gg.essential.elementaPractice.WhiteboardPalette
import java.awt.Color

public class WhiteboardComponent : UIComponent() {

    public val drawArea: CanvasComponent

    init {

        setColor(Color.BLACK)

        // Whiteboard Background/Outline
        val border = UIRoundedRectangle(8f).constrain {
            width = 100.percent()
            height = 100.percent()
            color = WhiteboardPalette.WHITEBOARD_OUTLINE.toConstraint()
        } childOf this

        drawArea = CanvasComponent().constrain {
            x = 3.pixels()
            y = 3.pixels()
            width = 100.percent() - 6.pixels()
            height = 100.percent() - 6.pixels()
            color = WhiteboardPalette.WHITEBOARD_BACKGROUND.toConstraint()
        } childOf border

        drawArea.onMouseClick {
            // Generate a new line with color after clicking
            drawArea.newLine()
        }.onMouseDrag { mouseX, mouseY, mouseButton ->
            // Only draw on left click
            if (mouseButton != 0) return@onMouseDrag
            // Make sure the dragged point is within the whiteboard area
            if (!isPointInside(mouseX + getLeft(), mouseY + getTop())) return@onMouseDrag

            drawArea.addPoint(Pair(mouseX + getLeft(), mouseY + getTop()))
        }
    }

}