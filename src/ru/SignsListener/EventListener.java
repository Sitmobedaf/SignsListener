package ru.SignsListener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ru.SignsListener.Main;

public class EventListener implements Listener {
	Main plugin = Main.inst;
	String path = plugin.path;

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e) throws FileNotFoundException {
		if (!e.isCancelled() && e.getPlayer() != null) {
			Player p = e.getPlayer();
			if (!e.getLine(0).isEmpty() || !e.getLine(1).isEmpty() || !e.getLine(2).isEmpty() || !e.getLine(3).isEmpty()) {
				if (plugin.conf.getBoolean("SignsListener.Enabled")) {
					String line1 = e.getLine(0);
					String line2 = e.getLine(1);
					String line3 = e.getLine(2);
					String line4 = e.getLine(3);
					StringBuilder builder = new StringBuilder();
					builder.append(line1).append(plugin.conf.getString("SignsListener.Separator")).append(line2).append(plugin.conf.getString("SignsListener.Separator")).append(line3).append(plugin.conf.getString("SignsListener.Separator")).append(line4);
					String text = builder.toString();
					String x = String.valueOf(p.getLocation().getBlockX());
					String y = String.valueOf(p.getLocation().getBlockY());
					String z = String.valueOf(p.getLocation().getBlockZ());
					String w = p.getWorld().getName();
					if (plugin.conf.getBoolean("SignsListener.BlockFormatting")) {
						if (!p.hasPermission("SignsListener.signs.useFormatting")) {
							if (text.contains("§")) {
								p.sendMessage(plugin.conf.getString("SignsListener.FormattingBlocked").replaceAll("&", "§"));
								e.setCancelled(true);
								return;
							}
						}
					}
					String signsFormat = null;
					try {
						signsFormat = plugin.conf.getString("SignsListener.OutputFormat").replace("<player>", p.getName()).replace("<world>", w).replace("<x>", x).replaceAll("<y>", y).replaceAll("<z>", z).replaceAll("<text>", Matcher.quoteReplacement(text)).replaceAll("&", "§");
					} catch (Exception ex) {
						p.sendMessage(plugin.conf.getString("SignsListener.IllegalSymbols").replaceAll("&", "§"));
						e.setCancelled(true);
						return;
					}
					if (plugin.conf.getBoolean("SignsListener.StaffNotice")) {
						for (Player op : Bukkit.getOnlinePlayers()) {
							if (op.hasPermission("SignsListener.signs.notice")) {
								op.sendMessage(signsFormat);
							}
						}
					}
					if (plugin.conf.getBoolean("SignsListener.UseDifferentFile")) {
						try (FileWriter signLog = new FileWriter(path + "signs.txt", true); BufferedWriter signBuff = new BufferedWriter(signLog); PrintWriter outSign = new PrintWriter(signBuff)) {
							outSign.println(signsFormat);
						} catch (NullPointerException | IOException ex) {
							Bukkit.getLogger().warning("Could not write to file. Error message: " + ex.getMessage());
						}
					} else if (!e.isCancelled()) {
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
				if (plugin.conf.getBoolean("BooksListener.Enabled")) {
					String title = plugin.conf.getString("BooksListener.WithoutTitle");
					if (e.getNewBookMeta().getTitle() != null) {
						title = e.getNewBookMeta().getTitle();
					}
					List<String> pages = e.getNewBookMeta().getPages();
					String text = null;
					StringBuilder builder = new StringBuilder();
					for (String onePage : pages) {
						builder.append(onePage);
						text = builder.toString().replaceAll("\n", plugin.conf.getString("BooksListener.Separator")).replaceAll("§0", "");
					}
					String x = String.valueOf(p.getLocation().getBlockX());
					String y = String.valueOf(p.getLocation().getBlockY());
					String z = String.valueOf(p.getLocation().getBlockZ());
					String w = p.getWorld().getName();
					if (plugin.conf.getBoolean("BooksListener.BlockFormatting")) {
						if (!p.hasPermission("SignsListener.books.useFormatting")) {
							if (text.contains("§")) {
								p.sendMessage(plugin.conf.getString("BooksListener.FormattingBlocked").replaceAll("&", "§"));
								e.setCancelled(true);
								return;
							}
						}
					}
					String booksFormat = null;
					try {
						booksFormat = plugin.conf.getString("BooksListener.OutputFormat").replace("<player>", p.getName()).replace("<world>", w).replace("<x>", x).replaceAll("<y>", y).replaceAll("<z>", z).replaceAll("<title>", title).replaceAll("<text>", Matcher.quoteReplacement(text)).replaceAll("&", "§");
					} catch (Exception ex) {
						p.sendMessage(plugin.conf.getString("BooksListener.IllegalSymbols").replaceAll("&", "§"));
						e.setCancelled(true);
						return;
					}
					if (plugin.conf.getBoolean("BooksListener.StaffNotice")) {
						for (Player op : Bukkit.getOnlinePlayers()) {
							if (op.hasPermission("SignsListener.books.notice")) {
								op.sendMessage(booksFormat);
							}
						}
					}
					if (!e.isCancelled() && plugin.conf.getBoolean("BooksListener.UseDifferentFile")) {
						try (FileWriter bookLog = new FileWriter(path + "books.txt", true); BufferedWriter bookBuff = new BufferedWriter(bookLog); PrintWriter outBook = new PrintWriter(bookBuff)) {
							outBook.println(booksFormat);
						} catch (NullPointerException | IOException ex) {
							Bukkit.getLogger().warning("Could not write to file. Error message: " + ex.getMessage());
						}
					} else if (!e.isCancelled()) {
						Bukkit.getLogger().info(booksFormat);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e) {
		if (plugin.conf.getBoolean("ItemsListener.Enabled")) {
			HumanEntity ent = e.getWhoClicked();
			if (ent instanceof Player) {
				Player p = (Player) ent;
				Inventory inv = e.getInventory();
				if (inv instanceof AnvilInventory) {
					InventoryView view = e.getView();
					int rawSlot = e.getRawSlot();
					if (rawSlot == 2 && rawSlot == view.convertSlot(rawSlot)) {
						ItemStack item = e.getCurrentItem();
						if (item != null) {
							ItemMeta meta = item.getItemMeta();
							if (meta != null) {
								if (meta.hasDisplayName()) {
									String text = meta.getDisplayName();
									String x = String.valueOf(p.getLocation().getBlockX());
									String y = String.valueOf(p.getLocation().getBlockY());
									String z = String.valueOf(p.getLocation().getBlockZ());
									String w = p.getWorld().getName();
									String itemsFormat = null;
									try {
										itemsFormat = plugin.conf.getString("ItemsListener.OutputFormat").replace("<player>", p.getName()).replace("<world>", w).replace("<x>", x).replaceAll("<y>", y).replaceAll("<z>", z).replaceAll("<text>", Matcher.quoteReplacement(text)).replaceAll("&", "§");
									} catch (Exception ex) {
										p.sendMessage(plugin.conf.getString("ItemsListener.IllegalSymbols").replaceAll("&", "§"));
										e.setCancelled(true);
										return;
									}
									if (plugin.conf.getBoolean("ItemsListener.StaffNotice")) {
										for (Player op : Bukkit.getOnlinePlayers()) {
											if (op.hasPermission("SignsListener.items.notice")) {
												op.sendMessage(itemsFormat);
											}
										}
									}
									if (!e.isCancelled() && plugin.conf.getBoolean("ItemsListener.UseDifferentFile")) {
										try (FileWriter itemLog = new FileWriter(path + "items.txt", true); BufferedWriter itemBuff = new BufferedWriter(itemLog); PrintWriter outItem = new PrintWriter(itemBuff)) {
											outItem.println(itemsFormat);
										} catch (NullPointerException | IOException ex) {
											Bukkit.getLogger().warning("Could not write to file. Error message: " + ex.getMessage());
										}
									} else if (!e.isCancelled()) {
										Bukkit.getLogger().info(itemsFormat);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
