package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementaPractice.WhiteboardPalette

public class ButtonComponent(text: String, private val scale: Float = 1f) : UIComponent() {

    init {
        constrain {
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint()
        }
    }

    // Button with hover effects
    private val button = UIBlock(WhiteboardPalette.BUTTON_BACKGROUND).constrain {
        width = ChildBasedSizeConstraint() + 2.pixels()
        height = ChildBasedSizeConstraint() + 2.pixels()
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
    } childOf this

    // Text for button
    private val buttonText = UIText(text, shadow = false).constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        textScale = scale.pixels()
        color = WhiteboardPalette.BUTTON_TEXT.toConstraint()
    } childOf button
}