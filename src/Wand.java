import java.util.logging.Logger;

/**
*
* @author NathanWolf
*/
public class Wand extends Plugin  
{
	private static final WandListener listener = new WandListener();
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private String name = "Wand";
	private String version = "0.02";
	
	public void enable() 
	{
		etc inst = etc.getInstance();
		inst.addCommand("/wand", "[bind|unbind|list] <command> - Control wand spells.");
		listener.enable();
	}
	
	public void disable() 
	{
		etc inst = etc.getInstance();
		inst.removeCommand("/cast");
		listener.disable();
	}

	public void initialize() 
	{
		etc.getLoader().addListener(PluginLoader.Hook.ARM_SWING, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener, this, PluginListener.Priority.MEDIUM);

		log.info(name + " " + version + " initialized");
	}
}
