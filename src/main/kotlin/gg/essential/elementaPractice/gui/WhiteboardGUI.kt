package gg.essential.elementaPractice.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementaPractice.events.WhiteboardEvent
import gg.essential.elementaPractice.gui.components.ColorToolComponent
import gg.essential.elementaPractice.gui.components.WhiteboardComponent
import gg.essential.universal.UMinecraft
import net.minecraftforge.common.MinecraftForge
import java.awt.Color
import java.awt.Image

class WhiteboardGUI : WindowScreen(ElementaVersion.V1) {

    private val whiteboardEvent = WhiteboardEvent(this)
    private var open = false

    init {

        // Button to close the whiteboard
        val closeButton = UIBlock(Color(207, 207, 196)).constrain {
            x = 2.pixels()
            y = 2.pixels()
            width = ChildBasedMaxSizeConstraint() + 2.pixels()
            height = ChildBasedMaxSizeConstraint() + 2.pixels()
        }.onMouseClick {
            // Close the whiteboard
            toggle()
        }.onMouseEnter {
            // Change color on mouse hover
            this.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 100).toConstraint())
            }
        }.onMouseLeave {
            // Change color back to original when not hovering
            this.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(207, 207, 196).toConstraint())
            }
        } childOf window

        // Text for close button
        UIText("X", shadow = false).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = Color.BLACK.toConstraint()
        } childOf closeButton

        val whiteboard = WhiteboardComponent().constrain {
            x = 10.percent()
            y = 10.percent()
            width = 80.percent()
            height = 80.percent()
        } childOf window

        // Marker tray
        val tray = UIRoundedRectangle(2f).constrain {
            x = CenterConstraint()
            y = 90.percent()
            color = Color(75, 75, 75).toConstraint()
            width = 176.pixels()
            height = 17.pixels()
        }.onMouseClick { event ->
            // Reset all tool effects except the clicked tool
            if (event.target is ColorToolComponent) {
                children.filter { it != event.target }.forEach {
                    it.setWidth(30.pixels())
                    it.removeEffect<OutlineEffect>()
                }
                whiteboard.setColor((event.target as ColorToolComponent).getToolColor())
            }
        } childOf window

        // Markers and Eraser
        for ((index, colorTool) in enumValues<ColorTools>().withIndex()) {
            val tool = ColorToolComponent(colorTool).constrain {
                // Shift by 34 pixels (tool width + 4) for each tool and add 4 pixel padding
                x = (index * 34 + 4).pixels()
                y = CenterConstraint()
            } childOf tray
            // Highlight the first marker
            if (index == 0) {
                tool.setWidth(32.pixels())
                tool.setHeight(ImageAspectConstraint())
                tool effect tool.outline
            }
        }
    }

    /**
     * Toggle the whiteboard by registering/unregistering the event
     */
    fun toggle() {
        // Unregister event if whiteboard is open to stop rendering
        if (open) {
            MinecraftForge.EVENT_BUS.unregister(whiteboardEvent)
            UMinecraft.getMinecraft().displayGuiScreen(null)
        }
        // Otherwise, register the event
        else {
            MinecraftForge.EVENT_BUS.register(whiteboardEvent)
        }
        // Toggle open status
        open = !open
    }

    /**
     * Enum class containing the various color-changing tools
     * @param color: Color - Color the tool should generate
     * @param fileName: String - Name of the tool's icon image, including the file extension
     */
    enum class ColorTools(val color: Color, val fileName: String) {
        BlackMarker(Color.BLACK,"BlackMarker.png"),
        RedMarker(Color.RED,"RedMarker.png"),
        GreenMarker(Color.GREEN,"GreenMarker.png"),
        BlueMarker(Color.BLUE,"BlueMarker.png"),
        Eraser(Color.WHITE,"DryEraser.png")
    }

}