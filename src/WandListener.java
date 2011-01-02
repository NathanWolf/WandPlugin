import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WandListener extends PluginListener {
	private static final Logger log = Logger.getLogger("Minecraft");

	private String commandFile;
	private HashMap<String, PlayerWand> playerWands = new HashMap<String, PlayerWand>();
	private ArrayList<String> defaultCommands;

	private static String STR_WAND_USAGE = "Use:@/wand bind <command> : bind a command to your wand.@/wand unbind <command> : unbind a command from your wand.@/wand list : list bound commands.";
	private static String STR_WAND_BOUND = "Wand bound to ";
	private static String STR_WAND_UNBOUND = "Wand unbound from ";
	private static String STR_WAND_ENCHANTED = "Wand enchanted with ";
	private static String STR_WAND_NO_SPELLS = "Your wand is not enchanted";
	private static String STR_WAND_CHANGE_TIER = "You changed your tier to ";
	private static String STR_WAND_RENAME_TIER = "You renamed your tier ";
	private static String STR_WAND_RENAME_TIER_2 = " to ";
	private static String STR_WAND_MISSING_TIER = "You don't have a tier named ";
	private static String STR_WAND_CREATE_TIER = "You created a tier ";
	private static String STR_WAND_REMOVE_TIER = "You removed tier ";

	private static int WAND_ITEM_ID;

	public void enable() {
		PropertiesFile properties = new PropertiesFile("wand.properties");
		STR_WAND_USAGE = properties
				.getString("text-wand-usage", STR_WAND_USAGE);
		STR_WAND_BOUND = properties
				.getString("text-wand-bound", STR_WAND_BOUND);
		STR_WAND_UNBOUND = properties.getString("text-wand-unbound",
				STR_WAND_UNBOUND);
		STR_WAND_ENCHANTED = properties.getString("text-wand-enchanted",
				STR_WAND_ENCHANTED);
		STR_WAND_NO_SPELLS = properties.getString("text-wand-nospells",
				STR_WAND_NO_SPELLS);
		STR_WAND_CHANGE_TIER = properties.getString("text-tier-change",
				STR_WAND_CHANGE_TIER);
		STR_WAND_RENAME_TIER = properties.getString("text-tier-rename",
				STR_WAND_RENAME_TIER);
		STR_WAND_RENAME_TIER_2 = properties.getString("text-tier-rename-2",
				STR_WAND_RENAME_TIER_2);
		STR_WAND_MISSING_TIER = properties.getString("text-tier-missing",
				STR_WAND_MISSING_TIER);
		STR_WAND_CREATE_TIER = properties.getString("text-tier-create",
				STR_WAND_CREATE_TIER);
		STR_WAND_REMOVE_TIER = properties.getString("text-tier-remove",
				STR_WAND_REMOVE_TIER);
		WAND_ITEM_ID = properties.getInt("wand-item-id", 280);
		commandFile = "wand-commands.txt";
		playerWands = new HashMap<String, PlayerWand>();
		defaultCommands = new ArrayList<String>();

		parseDefaultCommands(properties.getString("wand-default-commands", ""));
		load();
	}

	private void parseDefaultCommands(String commandString) {
		String[] commands = commandString.split(",");
		for (int i = 0; i < commands.length; i++) {
			defaultCommands.add(commands[i]);
		}
	}

	public void save() {
		BufferedWriter writer = null;
		try {
			log.info("Saving " + commandFile);
			writer = new BufferedWriter(new FileWriter(commandFile));
			writer.write("# " + commandFile);
			writer.newLine();
			for (PlayerWand wand : playerWands.values()) {
				writer.write(wand.toString());
				writer.newLine();
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while creating " + commandFile, e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "Exception while closing " + commandFile,
						e);
			}
		}
	}

	public void load() {
		if (!new File(commandFile).exists()) {
			log.info("File does not exist " + commandFile);
			return;
		}
		try {
			Scanner scanner = new Scanner(new File(commandFile));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#") || line.equals(""))
					continue;
				String[] split = line.split(":");

				if (split.length < 2) {
					log.log(Level.SEVERE, "Malformed line (" + line + ") in "
							+ commandFile);
					continue;
				}

				String playerName = split[0];
				String[] split2 = split[1].split(",");
				int selectedTier = Integer.parseInt(split2[0]);
				int selectedSpell = Integer.parseInt(split2[1]);

				PlayerWand wand = new PlayerWand(playerName);
				playerWands.put(playerName, wand);
				boolean isName = true;
				for (int i = 2; i < split.length; i++, isName = !isName) {
					if (isName)
						wand.newTier(split[i]);
					else {
						split2 = split[i].split(",");
						for (int j = 0; j < split2.length; j++)
							wand.bind(split2[j]);
					}
				}
				wand.selectTier(selectedTier);
				wand.selectSpell(selectedSpell);
			}
			scanner.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while reading " + commandFile, e);
		}
	}

	public void disable() {
		save();
	}

	public PlayerWand createWand(Player player) {
		PlayerWand wand = new PlayerWand(player.getName());
		wand.newTier("Default");
		for (int i = 0; i < defaultCommands.size(); i++) {
			if (defaultCommands.get(i).length() > 0) {
				wand.bind(defaultCommands.get(i));
			}
			wand.selectSpell(0);
		}
		playerWands.put(player.getName(), wand);
		log.log(Level.INFO, "Created wand for player " + player.getName());
		save();
		return wand;
	}

	public void onLogin(Player player) {
		PlayerWand wand = playerWands.get(player.getName());
		if (wand == null) {
			createWand(player);
		} else {
			log.log(Level.INFO, "Player " + player.getName() + " wand loaded");
		}
	}

	public void onDisconnect(Player player) {
		save();
	}

	public PlayerWand getWand(Player player) {
		PlayerWand wand = playerWands.get(player.getName());
		if (wand == null) {
			wand = createWand(player);
		}
		return wand;
	}

	public boolean onCommand(Player player, String[] command) {
		if (command[0].equalsIgnoreCase("/reload")) {
			disable();
			enable();
			return false;
		}

		PlayerWand wand = getWand(player);
		if (command[0].equalsIgnoreCase("/wand")
				&& player.canUseCommand("/wand")) {
			if (command.length == 2) {
				if (command[1].equalsIgnoreCase("list")) {
					if (wand.getCommands().size() <= 0) {
						player.sendMessage(Colors.Red + STR_WAND_NO_SPELLS);
					}
					for (int i = 0; i < wand.getCommands().size(); i++) {
						String isCurrent = " ";
						if (i == wand.getSelectedSpell()) {
							isCurrent = "*";
						}
						player.sendMessage(Colors.Green + isCurrent
								+ wand.getCommands().get(i));
					}
					return true;
				}
				if (command[1].equalsIgnoreCase("nextTier")) {
					wand.nextTier();
					player.sendMessage(Colors.Green + STR_WAND_CHANGE_TIER
							+ wand.getTierName());
					return true;
				}
				if (command[1].equalsIgnoreCase("prevTier")) {
					wand.prevTier();
					player.sendMessage(Colors.Green + STR_WAND_CHANGE_TIER
							+ wand.getTierName());
					return true;
				}

			}
			if (command.length < 3
					|| !(command[1].equalsIgnoreCase("bind")
							|| command[1].equalsIgnoreCase("unbind")
							|| command[1].equalsIgnoreCase("newTier")
							|| command[1].equalsIgnoreCase("removeTier")
							|| (command[1].equalsIgnoreCase("renameTier") && command.length >= 4) || command[1]
							.equalsIgnoreCase("tier"))) {
				// no params - show help
				String[] castUsage = STR_WAND_USAGE.split("@");
				for (int i = 0; i < castUsage.length; i++) {
					player.sendMessage(Colors.White + castUsage[i]);
				}
			} else {
				String wandCommand = "";
				for (int i = 2; i < command.length; i++) {
					wandCommand += command[i];
					if (i != command.length - 1)
						wandCommand += " ";
				}
				if (command[1].equalsIgnoreCase("bind")) {
					wand.bind(wandCommand);
					player.sendMessage(Colors.Green + STR_WAND_BOUND + " '"
							+ wandCommand + "'");
					save();
				}
				if (command[1].equalsIgnoreCase("unbind")) {
					wand.unbind(wandCommand);
					player.sendMessage(Colors.Green + STR_WAND_UNBOUND + " '"
							+ wandCommand + "'");
					save();
				}
				if (command[1].equalsIgnoreCase("newTier")) {
					wand.newTier(command[2]);
					player.sendMessage(Colors.Green + STR_WAND_CREATE_TIER
							+ command[2]);
					save();
				}
				if (command[1].equalsIgnoreCase("removeTier")) {
					if (wand.removeTier(command[2]))
						player.sendMessage(Colors.Green + STR_WAND_REMOVE_TIER
								+ command[2]);
					else
						player.sendMessage(Colors.Green + STR_WAND_MISSING_TIER
								+ command[2]);

					save();
				}
				if (command[1].equalsIgnoreCase("tier")) {
					if (wand.changeTierTo(command[2]))
						player.sendMessage(Colors.Green + STR_WAND_CHANGE_TIER
								+ command[2]);
					else
						player.sendMessage(Colors.Green + STR_WAND_MISSING_TIER
								+ command[2]);
				}
				if (command[1].equalsIgnoreCase("renameTier")) {
					if (wand.renameTier(command[2], command[3]))
						player.sendMessage(Colors.Green + STR_WAND_RENAME_TIER
								+ command[2] + STR_WAND_RENAME_TIER_2
								+ command[3]);
					else
						player.sendMessage(Colors.Green + STR_WAND_MISSING_TIER
								+ command[2]);
				}
			}
			return true;
		}
		return false;
	}

	public void onArmSwing(Player player) {
		if (player.getItemInHand() == WAND_ITEM_ID) {
			PlayerWand wand = getWand(player);
			String command = wand.getCurrentCommand();
			if (command.equals(""))
				player.sendMessage(STR_WAND_NO_SPELLS);
			else
				player.chat("/" + command);
		}
	}

	public boolean onItemUse(Player player, Block placed, Block clicked,
			Item item) {
		if (item.getItemId() == WAND_ITEM_ID) {
			PlayerWand wand = getWand(player);
			wand.cycleSpells();
			player.sendMessage(Colors.Green + STR_WAND_ENCHANTED + " '"
					+ wand.getCurrentCommand() + "'");
			return true;
		}
		return false;
	}

}
