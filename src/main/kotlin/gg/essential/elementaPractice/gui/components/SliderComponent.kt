package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import java.awt.Color
import kotlin.math.roundToInt

/**
 * Component that generates a slider with specified number of values
 *
 * @param numValues: Int - Number of values the slider should contain
 * @param currentValue: Int - The default (current) value that should be set
 * @param oneBased: Boolean - True to start values at one, false to start values at zero
 */
public class SliderComponent constructor(
    private var numValues: Int = 2,
    private var currentValue: Int = 1,
    private var oneBased: Boolean = false
) : UIComponent() {

    init {

        if (numValues <= 1) throw IllegalArgumentException("numValues must be greater than one")

        // Background bar
        val bar = UIBlock(Color(80, 80, 80).toConstraint()).constrain {
            y = 2.pixels()
            width = 20.pixels()
            height = 4.pixels()
        } childOf this

        // Slider bar/knob
        val slider = UIBlock(Color(150, 150, 150).toConstraint()).constrain {
            x = (getSegmentPosition(currentValue - oneBased.toInt()) - 1.5).pixels()
            width = 3.pixels()
            height = 8.pixels()
        } childOf this

        // Value indicator
        val indicator = UIText(currentValue.toString()).constrain {
            x = 23.pixels()
            y = CenterConstraint()
            color = Color.WHITE.toConstraint()
            textScale = 0.5.pixels()
        } childOf bar

        onMouseDrag { mouseX, mouseY, mouseButton ->
            if (mouseButton != 0) return@onMouseDrag
            if (mouseX !in 0f..bar.getWidth() || mouseY !in 0f..slider.getHeight()) return@onMouseDrag

            // Calculate the width of each segment between values
            val segWidth = bar.getWidth() / (numValues - 1)
            // Round the x position to the nearest segment edge
            val segRounded = (mouseX / segWidth).roundToInt() * segWidth

            slider.setX((segRounded - 1.5).pixels())

            // Set the current value to the index of the segment edge
            currentValue = (segRounded / segWidth).roundToInt() + oneBased.toInt()

            // Update indicator text
            indicator.setText(currentValue.toString())
        }
    }

    private fun getSegmentPosition(value: Int): Float {
        // Calculate the width of each segment between values
        val segWidth = 20f / (numValues - 1)
        return value * segWidth
    }

    // Simple function to convert a boolean to an int
    private fun Boolean.toInt() = if (this) 1 else 0

    /**
     * Get the current value the slider is set to
     *
     * @return Int - The value the slider is set to, from 1 to numValues
     */
    public fun getValue(): Int = currentValue

}