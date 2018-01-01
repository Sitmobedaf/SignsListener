package ru.SignsListener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
	Main plugin = Main.inst;
	String ver = plugin.getDescription().getVersion();
	String unknownArgument = "§cInvalid command argument. Try /signslistener to help receive.";
	String notHavePermissions = "§cYou do not have permissions to use this command.";
	String confReloaded = "§2Configuration has been reloaded.";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("signslistener")) {
			if (sender.hasPermission("SignsListener.admin")) {
				if (args.length < 1) {
					sender.sendMessage("§c---======= §6SignsListener §e" + ver + " §c=======---");
					sender.sendMessage("§a/" + label + " reload - §7Reload plugin confiuration.");
					return true;
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("reload")) {
						plugin.loadConfiguration();
						sender.sendMessage(confReloaded);
						return true;
					} else {
						sender.sendMessage(unknownArgument);
						return true;
					}
				} else {
					sender.sendMessage(unknownArgument);
					return true;
				}
			} else {
				sender.sendMessage(notHavePermissions);
			}
		}
		return false;
	}
}
