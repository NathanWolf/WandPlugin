import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PlayerWand {
	private List<String> wandCommands;
	private List<Tier> tiers;
	private String player;
	int selectedSpell;
	private int selectedTier;

	public PlayerWand(String playerName) {
		tiers = new ArrayList<Tier>();
		this.player = playerName;
		selectedSpell = 0;
	}

	public List<String> getCommands() {
		return wandCommands;
	}

	public String getPlayer() {
		return player;
	}

	public void bind(String command) {
		wandCommands.add(command);
		selectedSpell = wandCommands.size() - 1;
	}

	public void unbind(String command) {
		wandCommands.remove(command);
		selectedSpell = 0;
	}

	public int getSelectedSpell() {
		return selectedSpell;
	}

	public void cycleSpells() {
		if (wandCommands.size() == 0)
			return;
		selectedSpell = (selectedSpell + 1) % wandCommands.size();
	}

	public void selectSpell(int spell) {
		if (spell >= 0 && spell < wandCommands.size()) {
			selectedSpell = spell;
		}
	}

	public String getCurrentCommand() {
		if (tiers.size() == 0 || wandCommands.size() == 0 || selectedSpell < 0
				|| selectedSpell >= wandCommands.size()) {
			return "";
		}
		return wandCommands.get(selectedSpell);
	}

	public boolean newTier(String name) {
		Tier tier = getTierByName(name);
		if (tier != null)
			return false;
		tier = new Tier(name);
		wandCommands = tier.getCommands();
		tiers.add(tier);
		selectedTier = tiers.size() - 1;
		wandCommands = tier.getCommands();
		selectedSpell = 0;
		return true;
	}

	public void selectTier(int selectedTier) {
		if (selectedTier < 0 || selectedTier >= tiers.size())
			this.selectedTier = 0;
		else
			this.selectedTier = selectedTier;
		wandCommands = tiers.get(this.selectedTier).getCommands();

	}

	public void nextTier() {
		selectedTier++;
		if (selectedTier >= tiers.size())
			selectedTier = 0;
		wandCommands = tiers.get(selectedTier).getCommands();
	}

	public void prevTier() {
		selectedTier--;
		if (selectedTier < 0)
			selectedTier = tiers.size();
		wandCommands = tiers.get(selectedTier).getCommands();
	}

	public String getTierName() {
		return tiers.get(selectedTier).getName();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(player + ":" + selectedTier + ","
				+ selectedSpell + ":");
		for (Tier t : tiers) {
			sb.append(t.getName() + ":");
			for (String spell : t.getCommands())
				sb.append(spell + ",");
			int length = sb.length();
			if (sb.charAt(length - 1) == ',')
				sb.replace(length - 1, length, ":");
		}
		return sb.toString();
	}

	public boolean removeTier(String name) {
		Tier tier = getTierByName(name);
		if (tier == null)
			return false;
		tiers.remove(tier);
		selectedTier = selectedSpell = 0;
		if (tiers.size() > 0)
			wandCommands = tiers.get(0).getCommands();
		return true;
	}

	public boolean changeTierTo(String name) {
		ListIterator<Tier> i = tiers.listIterator();
		int l = 0;
		while (i.hasNext()) {
			if (i.next().getName().equalsIgnoreCase(name)) {
				selectedTier = l;
				wandCommands = tiers.get(selectedTier).getCommands();
				return true;
			}
			l++;
		}
		return false;
	}

	private Tier getTierByName(String name) {
		for (Tier tier : tiers)
			if (tier.getName().equals(name))
				return tier;
		return null;
	}

	public boolean renameTier(String oldName, String newName) {
		Tier tier = getTierByName(oldName);
		if (tier == null)
			return false;
		tier.setName(newName);
		return true;

	}
}
