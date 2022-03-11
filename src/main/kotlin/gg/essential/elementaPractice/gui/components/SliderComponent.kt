package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import gg.essential.elementa.state.BasicState
import gg.essential.elementaPractice.WhiteboardPalette
import kotlin.math.roundToInt

/**
 * Component that generates a slider with specified number of values
 *
 * @param numValues: Int - Number of values the slider should contain
 * @param currentValue: Int - The default (current) value that should be set
 * @param oneBased: Boolean - True to start values at one, false to start values at zero
 */
public class SliderComponent constructor(
    private var label: String?,
    private var numValues: Int = 2,
    private val currentValue: Int = 1,
    private var oneBased: Boolean = false
) : UIComponent() {

    public val currentState: BasicState<Int> = BasicState(currentValue)

    init {

        if (numValues <= 1) throw IllegalArgumentException("numValues must be greater than one")

        constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedSizeConstraint()
        }

        if (label?.isNotBlank() == true) {
            UIText(label!!, false).constrain {
                x = CenterConstraint()
                y = 0.pixels()
                textScale = 1.pixels()
                color = WhiteboardPalette.LABEL_TEXT.toConstraint()
            } childOf this
        }

        // Background bar
        val bar = UIBlock(WhiteboardPalette.SLIDER_BACKGROUND.toConstraint()).constrain {
            x = CenterConstraint()
            y = if (label?.isNotBlank() == true) 11.pixels() else 2.pixels()
            width = 20.pixels()
            height = 4.pixels()
        } childOf this

        // Slider bar/knob
        val slider = UIBlock(WhiteboardPalette.SLIDER_BUTTON.toConstraint()).constrain {
            x = (getSegmentPosition(currentState.get() - oneBased.toInt()) - 1).pixels()
            y = CenterConstraint()
            width = 3.pixels()
            height = 8.pixels()
        } childOf bar

        // Value indicator
        val indicator = UIText(currentState.get().toString()).constrain {
            x = 23.pixels()
            y = CenterConstraint()
            color = WhiteboardPalette.LABEL_TEXT.toConstraint()
            textScale = 1.pixels()
        } childOf bar

        onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton != 0) return@onMouseDrag
            val absoluteX = mouseX + getLeft()
            val absoluteY = mouseY + getTop()
            val relativeX = absoluteX - bar.getLeft()
            if (absoluteX !in bar.getLeft()..bar.getRight() || absoluteY !in bar.getTop()..bar.getBottom()) return@onMouseDrag

            // Calculate the width of each segment between values
            val segWidth = bar.getWidth() / (numValues - 1)
            // Round the x position to the nearest segment edge
            val segRounded = (relativeX / segWidth).roundToInt() * segWidth

            currentState.set( (segRounded / segWidth).roundToInt() + oneBased.toInt() )
        }

        // Update the value indicator and slider position when the value state changes
        currentState.onSetValue {
            slider.setX( (getSegmentPosition(currentState.get() - oneBased.toInt()) - 1 ).pixels())
            indicator.setText(it.toString())
        }
    }

    // Get the x-coordinate where the slider should be given a value
    private fun getSegmentPosition(value: Int): Float {
        // Calculate the width of each segment between values
        val segWidth = 20f / (numValues - 1)
        return value * segWidth
    }

    // Simple function to convert a boolean to an int
    private fun Boolean.toInt() = if (this) 1 else 0

}