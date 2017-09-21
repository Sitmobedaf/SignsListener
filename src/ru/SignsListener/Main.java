package ru.SignsListener;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.SignsListener.EventListeler;

public class Main extends JavaPlugin {
	public static Main inst;
	public Main() {
	super();
	}
	
	public FileConfiguration conf = this.getConfig();
	public String path = this.getDataFolder() + File.separator;

	public void onEnable() {
		inst = this;
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new EventListeler(this), this);
		
		this.saveDefaultConfig();
		
		File books = new File(path + "books.txt");
		File signs = new File(path + "signs.txt");
		
		if (conf.getBoolean("BooksListener.Enabled") && conf.getBoolean("BooksListener.UseDifferentFile")) {
			if (!books.exists()) {
				try {
					books.createNewFile();
				} catch (IOException e) {
					this.getLogger().warning("Could not create a new file: " + path + "books.txt. Error message: " + e.getMessage());
				}
			}
		}
		
		if (conf.getBoolean("SignsListener.Enabled") && conf.getBoolean("SignsListener.UseDifferentFile")) {
			if (!signs.exists()) {
				try {
					signs.createNewFile();
				} catch (IOException e) {
					this.getLogger().warning("Could not create a new file. Error message: " + e.getMessage());
				}
			}
		}
		
		this.getLogger().info("SignsListener has been Enabled.");
	}

	public void onDisable() {
		this.getLogger().info("SignsListener has been Disabled.");
	}
}