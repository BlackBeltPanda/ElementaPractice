package gg.essential.elementaPractice

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementaPractice.events.WhiteboardEvent
import gg.essential.elementaPractice.utils.Interpolator
import gg.essential.elementaPractice.utils.Simplifier
import gg.essential.universal.UMinecraft
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import java.awt.Color
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO


/**
 * The main whiteboard GUI
 */
class WhiteboardGUI : WindowScreen(ElementaVersion.V1) {
    private val whiteboardEvent = WhiteboardEvent(this)
    private val points = mutableListOf<UIPoint>()
    private var open = false
    private var whiteboard: UIComponent
    private lateinit var tempSpline: Spline
    private var color = Color.BLACK
    private val splines = mutableListOf<Spline>()

    // Keep track of when the mouse is being dragged on the whiteboard GUI
    private var isDragging: Boolean = false

    init {
        // Button to close the whiteboard
        val closeButton = UIBlock(Color(207, 207, 196)).constrain {
            // Set width and height to child component with 4 pixel padding
            width = ChildBasedSizeConstraint() + 2.pixels()
            height = ChildBasedMaxSizeConstraint() + 2.pixels()

            // 2 pixel padding
            x = 2.pixels()
            y = 2.pixels()
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
            // 2 pixel padding and centered
            x = CenterConstraint()
            y = CenterConstraint()

            color = Color.BLACK.toConstraint()
        } childOf closeButton

        // Whiteboard container/outline
        val container = UIRoundedRectangle(8f).constrain {
            x = 5.percent()
            y = 5.percent()
            color = Color(105, 105, 105).toConstraint()
            width = 90.percent()
            height = 80.percent()
        } childOf window

        // Marker tray
        val tray = UIRoundedRectangle(2f).constrain {
            x = CenterConstraint()
            y = 100.percent()
            color = Color(75, 75, 75).toConstraint()
            width = 50.percent()
            height = 6.percent()
        } childOf container

        // Black Marker
        (UIImage(CompletableFuture.supplyAsync {
            ImageIO.read(this::class.java.getResourceAsStream("/assets/elementapractice/BlackMarker.png"))
        }).constrain {
            x = 1.percent()
            y = CenterConstraint() + 8.percent()
            width = 18.percent()
            height = ImageAspectConstraint()
        }.onMouseEnter {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 0.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 20.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseLeave {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 1.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 18.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseClick {
            color = Color.BLACK
        } childOf tray).setFloating(true)

        // Red Marker
        (UIImage(CompletableFuture.supplyAsync {
            ImageIO.read(this::class.java.getResourceAsStream("/assets/elementapractice/RedMarker.png"))
        }).constrain {
            x = 21.percent()
            y = CenterConstraint() + 8.percent()
            width = 18.percent()
            height = ImageAspectConstraint()
        }.onMouseEnter {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 20.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 20.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseLeave {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 21.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 18.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseClick {
            println("Red")
            color = Color.RED
        } childOf tray).setFloating(true)

        // Green Marker
        (UIImage(CompletableFuture.supplyAsync {
            ImageIO.read(this::class.java.getResourceAsStream("/assets/elementapractice/GreenMarker.png"))
        }).constrain {
            x = 41.percent()
            y = CenterConstraint() + 8.percent()
            width = 18.percent()
            height = ImageAspectConstraint()
        }.onMouseEnter {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 40.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 20.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseLeave {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 41.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 18.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseClick {
            color = Color.GREEN
        } childOf tray).setFloating(true)

        // Blue Marker
        (UIImage(CompletableFuture.supplyAsync {
            ImageIO.read(this::class.java.getResourceAsStream("/assets/elementapractice/BlueMarker.png"))
        }).constrain {
            x = 61.percent()
            y = CenterConstraint() + 8.percent()
            width = 18.percent()
            height = ImageAspectConstraint()
        }.onMouseEnter {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 60.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 20.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseLeave {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 61.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 18.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseClick {
            color = Color.BLUE
        } childOf tray).setFloating(true)

        // Eraser
        (UIImage(CompletableFuture.supplyAsync {
            ImageIO.read(this::class.java.getResourceAsStream("/assets/elementapractice/DryEraser.png"))
        }).constrain {
            x = 81.percent()
            y = CenterConstraint() - 5.percent()
            width = 15.percent()
            height = ImageAspectConstraint()
        }.onMouseEnter {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 80.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 20.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseLeave {
            this.animate {
                setXAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 81.percent())
                setWidthAnimation(Animations.IN_OUT_BOUNCE, 0.2f, 18.percent())
                setHeightAnimation(Animations.IN_OUT_BOUNCE, 0.2f, ImageAspectConstraint())
            }
        }.onMouseClick {
            color = Color.WHITE
        } childOf tray).setFloating(true)

        // Main Whiteboard
        whiteboard = UIRoundedRectangle(8f).constrain {
            x = 3.pixels()
            y = 3.pixels()
            color = Color(250, 249, 246).toConstraint()

            // Fill remaining screen space for the time being until buttons are implemented
            width = 100.percent() - 6.pixels()
            height = 100.percent() - 6.pixels()
        }.onMouseClick {
            // Dragging started
            isDragging = true
        }.onMouseRelease {
            // Dragging stopped
            isDragging = false
            points.clear()
        } childOf container

        whiteboard.apply {
            onMouseDrag { mouseX, mouseY, _ ->
                // If we're not dragging, then return so nothing is drawn
                if (!isDragging || !isPointInside(mouseX + getLeft(), mouseY + getTop())) return@onMouseDrag

                points.add(UIPoint(mouseX, mouseY) childOf whiteboard)

                paint()
            }
        }

        // Size Picker Button?
    }

    /**
     * Paints splines in real-time
     */
    private fun paint() {
        // Need at least two points to draw a line
        if (points.size < 2) {
            return
        }

        splines.add(Spline(points.toList(), color) childOf whiteboard)
    }

    /**
     * Replaces real-time splines with a simplified and interpolated spline
     */
    private fun stabilize(spline: Spline) {
        // Generate list of all points from all temporary splines
        var splinePoints = spline.getPoints()

        // Simplify the points, removing unnecessary ones
        splinePoints = Simplifier().simplify(splinePoints)

        // Remove the old spline so we can replace it with the stabilized spline
        if (::tempSpline.isInitialized) tempSpline.parent.removeChild(tempSpline)

        // Interpolate the points, generating a smooth spline
        Spline(
            Interpolator().interpolate(splinePoints as MutableList<UIPoint>, whiteboard),
            Color.BLACK
        ) childOf whiteboard
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
}