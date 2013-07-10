package lunatrius.ingameinfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import java.io.File;
import java.util.EnumSet;

@Mod(modid = "InGameInfoXML")
public class InGameInfoXML {
	@Instance("InGameInfoXML")
	public static InGameInfoXML instance;

	private final InGameInfoCore core = InGameInfoCore.instance();
	private Minecraft minecraftClient = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.core.init(new File(event.getModConfigurationDirectory(), "InGameInfo.xml"));
		this.core.loadConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		TickRegistry.registerTickHandler(new Ticker(EnumSet.of(TickType.CLIENT, TickType.RENDER)), Side.CLIENT);

		this.core.setLogger(FMLCommonHandler.instance().getFMLLogger());
		this.core.setClient(this.minecraftClient = Minecraft.getMinecraft());
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		this.core.setServer(event.getServer());
	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		this.core.setServer(null);
	}

	public boolean onTick(TickType tick, boolean start) {
		if (start) {
			return true;
		}

		if (this.minecraftClient != null && this.minecraftClient.gameSettings != null && !this.minecraftClient.gameSettings.showDebugInfo) {
			if (this.minecraftClient.currentScreen == null || this.minecraftClient.currentScreen instanceof GuiChat) {
				if (tick == TickType.CLIENT) {
					this.core.onTickClient();
				} else if (tick == TickType.RENDER) {
					this.core.onTickRender();
				}
			}
		}

		return true;
	}
}