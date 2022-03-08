package gg.essential.elementaPractice.events

import gg.essential.elementaPractice.WhiteboardGUI
import gg.essential.universal.UMinecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

class WhiteboardEvent(private val whiteboard: WhiteboardGUI) {

    // When main menu is pulled up, we need to display the GUI each tick, or it won't render properly
    @SubscribeEvent
    fun tick(event: TickEvent.ClientTickEvent) {
        // TO-DO: Find better way to store and display whiteboard instance
        UMinecraft.getMinecraft().displayGuiScreen(whiteboard)
    }
}