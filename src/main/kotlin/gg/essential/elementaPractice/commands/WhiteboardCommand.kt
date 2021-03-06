package gg.essential.elementaPractice.commands

import gg.essential.elementaPractice.gui.WhiteboardGUI
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

public class WhiteboardCommand : CommandBase() {
    private val whiteboard = WhiteboardGUI()

    override fun getCommandName(): String {
        return "whiteboard"
    }

    override fun getCommandUsage(p0: ICommandSender?): String {
        return "/whiteboard"
    }

    override fun processCommand(sender: ICommandSender?, params: Array<out String>?) {
        whiteboard.toggle()
    }
}