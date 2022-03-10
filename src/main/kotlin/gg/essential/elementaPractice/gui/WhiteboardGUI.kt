package gg.essential.elementaPractice.gui

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementaPractice.WhiteboardPalette
import gg.essential.elementaPractice.events.WhiteboardEvent
import gg.essential.elementaPractice.gui.components.ColorToolComponent
import gg.essential.elementaPractice.gui.components.SliderComponent
import gg.essential.elementaPractice.gui.components.WhiteboardComponent
import gg.essential.universal.UMinecraft
import net.minecraftforge.common.MinecraftForge
import java.awt.Color

public class WhiteboardGUI : WindowScreen(ElementaVersion.V1) {

    private val whiteboardEvent = WhiteboardEvent(this)
    private var open = false

    init {

        // TODO Save/Load buttons?

        // Button to close the whiteboard
        val closeButton = UIBlock(WhiteboardPalette.BUTTON_BACKGROUND).constrain {
            x = 2.percent()
            y = 2.percent()
            width = ChildBasedMaxSizeConstraint() + 2.pixels()
            height = ChildBasedMaxSizeConstraint() + 2.pixels()
        }.onMouseClick {
            // Close the whiteboard
            toggle()
        }.onMouseEnter {
            // Change color on mouse hover
            this.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, WhiteboardPalette.BUTTON_HIGHLIGHT.toConstraint())
            }
        }.onMouseLeave {
            // Change color back to original when not hovering
            this.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, WhiteboardPalette.BUTTON_BACKGROUND.toConstraint())
            }
        } childOf window

        // Text for close button
        UIText("X", shadow = false).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 1.pixels()
            color = WhiteboardPalette.BUTTON_TEXT.toConstraint()
        } childOf closeButton

        val whiteboard = WhiteboardComponent().constrain {
            x = 5.percent()
            y = 5.percent()
            width = 90.percent()
            height = 80.percent()
        } childOf window

        // Marker tray
        val tray = UIRoundedRectangle(2f).constrain {
            x = CenterConstraint()
            y = SiblingConstraint()
            color = WhiteboardPalette.WHITEBOARD_OUTLINE.toConstraint()
            width = 176.pixels()
            height = 17.pixels()
        }.onMouseClick { event ->
            // Reset all tool effects except the clicked tool
            if (event.target is ColorToolComponent) {
                children.filter { it != event.target }.forEach {
                    it.setWidth(30.pixels())
                    it.removeEffect<OutlineEffect>()
                }
                whiteboard.drawArea.lineColor = (event.target as ColorToolComponent).getToolColor()
            }
        } childOf window

        // Markers and Eraser
        for ((index, colorTool) in enumValues<ColorTools>().withIndex()) {
            val tool = ColorToolComponent(colorTool).constrain {
                // Shift by 34 pixels (tool width + 4) for each tool and add 4 pixel padding
                x = SiblingConstraint(padding = 2f) + 2.pixels()
                y = CenterConstraint()
            } childOf tray
            // Highlight the first marker
            if (index == 0) {
                tool.setWidth(32.pixels())
                tool.setHeight(ImageAspectConstraint())
                tool effect tool.outline
            }
        }

        // Size slider
        val sizeSlider = SliderComponent("Marker Size", 12, 2, true).constrain {
            x = 6.percent()
            y = 89.percent()
        } childOf window
        sizeSlider.currentState.onSetValue {
            whiteboard.drawArea.lineWidth = it.toFloat()
        }

        // Smoothing slider
        val smoothingSlider = SliderComponent("Smoothing", 11, 5).constrain {
            x = SiblingConstraint(padding = 5f)
            y = 89.percent()
        } childOf window
        smoothingSlider.currentState.onSetValue {
            whiteboard.drawArea.smoothing = it
        }

        // Clear Whiteboard button
        val clearButton = UIBlock(WhiteboardPalette.BUTTON_BACKGROUND).constrain {
            x = 74.percent()
            y = 89.percent()
            width = ChildBasedMaxSizeConstraint() + 2.pixels()
            height = ChildBasedMaxSizeConstraint() + 2.pixels()
        }.onMouseClick {
            whiteboard.drawArea.clear()
        }.onMouseEnter {
            // Change color on mouse hover
            this.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, WhiteboardPalette.BUTTON_HIGHLIGHT.toConstraint())
            }
        }.onMouseLeave {
            // Change color back to original when not hovering
            this.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, WhiteboardPalette.BUTTON_BACKGROUND.toConstraint())
            }
        } childOf window

        // Clear Whiteboard Label
        UIText("Clear Whiteboard", shadow = false).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            textScale = 1.pixels()
            color = WhiteboardPalette.BUTTON_TEXT.toConstraint()
        } childOf clearButton

        // For some reason escape doesn't close the GUI, perhaps one of the components is preventing the key propagation or holding on to window focus?
        // Not sure, so here's a workaround
        window.onKeyType { _, keyCode ->
            if (keyCode == 1) toggle()
        }
    }

    /**
     * Toggle the whiteboard by registering/unregistering the event
     */
    public fun toggle() {
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
     *
     * @param color: Color - Color the tool should generate
     * @param resource: Resource - Name of the tool's icon image, including the file extension
     */
    public enum class ColorTools(public val color: Color, public val resource: String) {
        BlackMarker(WhiteboardPalette.MARKER_BLACK_COLOR, WhiteboardPalette.MARKER_BLACK_ICON),
        RedMarker(WhiteboardPalette.MARKER_RED_COLOR, WhiteboardPalette.MARKER_RED_ICON),
        GreenMarker(WhiteboardPalette.MARKER_GREEN_COLOR, WhiteboardPalette.MARKER_GREEN_ICON),
        BlueMarker(WhiteboardPalette.MARKER_BLUE_COLOR, WhiteboardPalette.MARKER_BLUE_ICON),
        Eraser(WhiteboardPalette.DRY_ERASER_COLOR, WhiteboardPalette.DRY_ERASER_ICON)
    }

}