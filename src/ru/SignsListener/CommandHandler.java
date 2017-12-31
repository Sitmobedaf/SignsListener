package ru.SignsListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandHandler implements CommandExecutor {

	static FileConfiguration conf = Main.inst.getConfig();
	String ver = Main.inst.getDescription().getVersion();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("signslistener")) {
			if (sender.hasPermission("SignsListener.admin")) {
				if (args.length < 1) {
					sender.sendMessage("§c---======= §6SignsListener §e" + ver + " §c=======---");
					sender.sendMessage("§a/" + label + " reload - §7Reload plugin confiuration.");
				}
				if (args.length == 1) {
					if (args[1].equalsIgnoreCase("reload")) {
						Main.inst.reloadConfig();
						sender.sendMessage("§2Configuration has been reloaded.");
					}
				} else {
					sender.sendMessage("Invalid command argument. Try /signslistener to receive help.");
				}
				if (args.length > 1) {
					sender.sendMessage("You have too many arguments. Try /signslistener to receive help.");
				}
				return true;
			} else {
				sender.sendMessage("You do not have permissions to use this command.");
			}
		}
		return false;
	}
}
