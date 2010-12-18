import java.util.ArrayList;
import java.util.List;

public class PlayerWand 
{
	private List<String> wandCommands;
	private String player;
	int selectedSpell;
	
	public PlayerWand(String playerName)
	{
		wandCommands = new ArrayList<String>();
		this.player = playerName;
		selectedSpell = 0;
	}
	
	public List<String> getCommands()
	{
		return wandCommands;
	}
	
	public String getPlayer()
	{
		return player;
	}
	
	public void bind(String command)
	{
		wandCommands.add(command);
	}
	
	public void unbind(String command)
	{
		wandCommands.remove(command);
	}
	
	public int getSelectedSpell()
	{
		return selectedSpell;
	}
	
	public void cycleSpells()
	{
		if (wandCommands.size() == 0) return;
		selectedSpell = (selectedSpell + 1) % wandCommands.size();
	}
	
	public void selectSpell(int spell)
	{
		selectedSpell = spell;
	}
	
	public String getCurrentCommand()
	{
		if (wandCommands.size() == 0 || selectedSpell < 0 || selectedSpell >= wandCommands.size())
		{
			return "";
		}
		return wandCommands.get(selectedSpell);
	}
}
