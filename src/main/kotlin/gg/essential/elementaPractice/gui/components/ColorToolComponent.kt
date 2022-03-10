package gg.essential.elementaPractice.gui.components

import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.ImageAspectConstraint
import gg.essential.elementa.constraints.PixelConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.animate
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.effect
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementaPractice.gui.WhiteboardGUI
import java.awt.Color
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

public class ColorToolComponent constructor(
    private val colorTool: WhiteboardGUI.ColorTools
) : UIImage(CompletableFuture.supplyAsync {
    ImageIO.read(this::class.java.getResourceAsStream("/assets/elementapractice/${colorTool.fileName}"))
}) {

    public val outline: OutlineEffect =
        OutlineEffect(color = Color.WHITE, width = 1f, sides = setOf(OutlineEffect.Side.Bottom))

    public fun getToolColor(): Color = colorTool.color

    init {

        constrain {
            width = 30.pixels()
            height = ImageAspectConstraint()
        }

        onMouseClick {
            setWidth(32.pixels())
            setHeight(ImageAspectConstraint())
            this effect outline
        }

        onMouseEnter {
            hoverAnimate(32.pixels())
        }

        onMouseLeave {
            hoverAnimate(30.pixels())
        }

    }

    // Simply changes the size of the component with an animation
    private fun hoverAnimate(size: PixelConstraint) {
        if (!effects.contains(outline)) {
            this.animate {
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, size)
            }
        }
    }
}