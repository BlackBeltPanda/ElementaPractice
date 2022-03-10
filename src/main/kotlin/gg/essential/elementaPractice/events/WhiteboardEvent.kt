package gg.essential.elementaPractice.events

import gg.essential.elementaPractice.gui.WhiteboardGUI
import gg.essential.universal.UMinecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

public class WhiteboardEvent(private val whiteboard: WhiteboardGUI) {

  // When main menu is pulled up, we need to display the GUI each tick, or it won't render properly
  @SubscribeEvent
  public fun tick(event: TickEvent.ClientTickEvent) {
    if (event.phase == TickEvent.Phase.START) {
      UMinecraft.getMinecraft().displayGuiScreen(whiteboard)
    }
  }
}