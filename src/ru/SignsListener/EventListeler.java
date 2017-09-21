package ru.SignsListener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEditBookEvent;

import ru.SignsListener.Main;

public class EventListeler implements Listener {
	public EventListeler(Main main) {
		super();
	}

	FileConfiguration conf = Main.inst.getConfig();
	String path = Main.inst.path;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) throws FileNotFoundException {
		if (e.getPlayer() != null) {
			Player p = e.getPlayer();
			if (!e.getLine(0).equals("") || !e.getLine(1).equals("") || !e.getLine(2).equals("") || !e.getLine(3).equals("")) {
				if (conf.getBoolean("SignsListener.Enabled")) {
					String l0 = e.getLine(0);
					String l1 = e.getLine(1);
					String l2 = e.getLine(2);
					String l3 = e.getLine(3);
					String x = "" + p.getLocation().getBlockX();
					String y = "" + p.getLocation().getBlockY();
					String z = "" + p.getLocation().getBlockZ();
					String w = p.getWorld().getName();
					String signsFormat = conf.getString("SignsListener.OutputFormat").replace("<player>", p.getName()).replace("<world>", w).replace("<x>", x).replaceAll("<y>", y).replaceAll("<z>", z).replaceAll("<line1>", l0).replaceAll("<line2>", l1).replaceAll("<line3>", l2).replaceAll("<line4>", l3);
					if (conf.getBoolean("SignsListener.UseDifferentFile")) {
						try (FileWriter signLog = new FileWriter(path + "signs.txt", true); BufferedWriter signBuff = new BufferedWriter(signLog); PrintWriter outSign = new PrintWriter(signBuff)) {
							outSign.println(signsFormat);
						} catch (NullPointerException | IOException ex) {
							Bukkit.getLogger().warning("Could not write to file. Error message: " + ex.getMessage());
						}
					}
					if (!conf.getBoolean("SignsListener.UseDifferentFile")) {
						Bukkit.getLogger().info(signsFormat);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBookChange(PlayerEditBookEvent e) {
		if (!e.isCancelled() && e.getPlayer() != null) {
			Player p = e.getPlayer();
			if (!e.getPreviousBookMeta().equals(e.getNewBookMeta())) {
				if (conf.getBoolean("BooksListener.Enabled")) {
					String title = conf.getString("BooksListener.WithoutTitle");
					if (e.getNewBookMeta().getTitle() != null) {
						title = e.getNewBookMeta().getTitle();
					}
					List<String> pages = e.getNewBookMeta().getPages();
					String text = pages.toString().replaceAll("\n", " + ");
					String x = "" + p.getLocation().getBlockX();
					String y = "" + p.getLocation().getBlockY();
					String z = "" + p.getLocation().getBlockZ();
					String w = p.getWorld().getName();
					if (!p.hasPermission("BooksListener.useFormatting") && conf.getBoolean("BooksListener.BlockFormatting")) {
						for (String text1 : e.getNewBookMeta().getPages()) {
							if (text1.contains("§")) {
								e.setCancelled(true);
								p.sendMessage(conf.getString("BooksListener.FormattingBlocked").replaceAll("&", "§"));
							}
						}
					}
					String booksFormat = null;
					try {
						booksFormat = conf.getString("BooksListener.OutputFormat").replace("<player>", p.getName()).replace("<world>", w).replace("<x>", x).replaceAll("<y>", y).replaceAll("<z>", z).replaceAll("<title>", title).replaceAll("<text>", Matcher.quoteReplacement(text.toString()));
					} catch (IllegalArgumentException ex) {
						p.sendMessage(conf.getString("BooksListener.IllegalSymbols").replaceAll("&", "§"));
						e.setCancelled(true);
					}
					if (!e.isCancelled() && conf.getBoolean("BooksListener.UseDifferentFile")) {
						try (FileWriter bookLog = new FileWriter(path + "books.txt", true); BufferedWriter bookBuff = new BufferedWriter(bookLog); PrintWriter outBook = new PrintWriter(bookBuff)) {
							outBook.println(booksFormat);
						} catch (NullPointerException | IOException ex) {
							Bukkit.getLogger().warning("Could not write to file. Error message: " + ex.getMessage());
						}
					}
					if (!e.isCancelled() && !conf.getBoolean("BooksListener.UseDifferentFile")) {
						Bukkit.getLogger().info(booksFormat);
					}
				}
			}
		}
	}
}
