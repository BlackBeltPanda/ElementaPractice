package gg.essential.elementaPractice

import gg.essential.elementa.effects.StencilEffect
import gg.essential.elementaPractice.commands.WhiteboardCommand
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent

private const val NAME = "ElementaPractice"
private const val VERSION = "1.0.0"
private const val ID = "elementapractice"
private const val ADAPTER = "gg.essential.elementaPractice.adapter.KotlinLanguageAdapter"

@Mod(name = NAME, modid = ID, version = VERSION, modLanguageAdapter = ADAPTER)
class ElementaPractice {

    // Enable stencil and register the events on initialization
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        StencilEffect.enableStencil()
        MinecraftForge.EVENT_BUS.register(this)
    }

    // Register whiteboard command
    @Mod.EventHandler
    fun init(event: FMLServerStartingEvent) {
        event.registerServerCommand(WhiteboardCommand())
    }
}