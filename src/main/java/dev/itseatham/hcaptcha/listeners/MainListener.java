package dev.itseatham.hcaptcha.listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Random;
import dev.itseatham.hcaptcha.hCaptcha;
import dev.itseatham.hcaptcha.util.ConfigManager;
import dev.itseatham.hcaptcha.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class MainListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        addPlayer(player);
    }

    public void auth(final Player p) {
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            this.captcha(p);
        }
        new BukkitRunnable(){

            public void run() {
                if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p) && ConfigManager.FAIL_KICK_BOOLEAN) {
                    p.kickPlayer(ConfigManager.FAIL_KICK);
                }
            }
        }.runTaskLater(hCaptcha.getInstance(), 20L * (long)ConfigManager.KICK_AFTER);
    }

    @EventHandler
    public void onCloseCaptchaInventory(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            this.captcha(p);
        }
    }

    public void captcha(final Player p) {
        final int randomslot = MainListener.randomSlot(0, 8);
        if (hCaptcha.saved_captcha.get((p.getName()) + "1") != null) {
            String title = hCaptcha.saved_captcha_info.get((p.getName()) + "4");
            ItemStack item1 = hCaptcha.saved_captcha.get((p.getName()) + "1");
            ItemStack item2 = hCaptcha.saved_captcha.get((p.getName()) + "2");
            int slot = hCaptcha.saved_captcha_slot.get(p.getName() + "3");
            final Inventory savedcaptcha = Bukkit.createInventory(null, InventoryType.DISPENSER, title);
            for (int i = 0; i < 9; ++i) {
                savedcaptcha.setItem(i, new ItemStack(item2));
            }
            savedcaptcha.setItem(slot, new ItemStack(item1));
            new BukkitRunnable(){

                public void run() {
                    p.openInventory(savedcaptcha);
                }
            }.runTaskLater(hCaptcha.getInstance(), 1L);
            return;
        }

        ItemStack item1_raw = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemStack item2_raw = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
        if (!ConfigManager.USE_GLASS) {
            item1_raw = new ItemStack(Material.getMaterial(this.randomBlock()));
            if (item1_raw.equals((item2_raw = new ItemStack(Material.getMaterial(this.randomBlock()))))) {
                item1_raw = new ItemStack(Material.getMaterial(this.randomBlock()));
                item2_raw = new ItemStack(Material.getMaterial(this.randomBlock()));
            }
        }

        final ItemStack item3 = MainListener.setMeta(item1_raw, ConfigManager.CAPTCHA_REAL_ITEM);
        final ItemStack item4 = MainListener.setMeta(item2_raw, ConfigManager.CAPTCHA_FAKE_ITEM);

        //GUI Title
        String title;
        if (ConfigManager.USE_GLASS) {
            title = ConfigManager.CAPTCHA_GUI_NAME.replaceAll("%block%", "Green Pane");
        } else {
            title = ConfigManager.CAPTCHA_GUI_NAME.replaceAll("%block%", item3.getType().toString());
        }
        final String title2 = title;

        final Inventory captcha = Bukkit.createInventory(null, InventoryType.DISPENSER, title2);
        for (int j = 0; j < 9; ++j) {
            captcha.setItem(j, new ItemStack(item4));
        }
        captcha.setItem(randomslot, new ItemStack(item3));
        new BukkitRunnable(){

            public void run() {
                p.openInventory(captcha);
                hCaptcha.saved_captcha.put((p.getName()) + "1", item3);
                hCaptcha.saved_captcha.put((p.getName()) + "2", item4);
                hCaptcha.saved_captcha_slot.put((p.getName()) + "3", randomslot);
                hCaptcha.saved_captcha_info.put((p.getName()) + "4", title2);
            }
        }.runTaskLater(hCaptcha.getInstance(), 1L);
    }

    @EventHandler
    public void onRightClick(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        ItemStack o = e.getCurrentItem();
        InventoryView inv = e.getView();
        boolean captcha = e.getView().getTitle().equalsIgnoreCase(hCaptcha.saved_captcha_info.get(p.getName() + "4"));
        try {
            if (captcha) {
                e.setCancelled(true);
                if (o.equals(inv.getItem(hCaptcha.saved_captcha_slot.get((p.getName()) + "3").intValue()))) {
                    hCaptcha.authenticated.add(p);
                    hCaptcha.join.remove(p);
                    p.sendMessage(ConfigManager.SUCCESSFULLY_AUTHENTICATED);
                    p.closeInventory();
                    if (ConfigManager.USE_WELCOME_MSG) {
                        for (int i = 0; i < ConfigManager.WELCOME_MSG.size(); i++) {
                            p.sendMessage(CC.translate((ConfigManager.WELCOME_MSG).get(i).toString()));
                        }
                    }
                    this.dumpAuthentication(p, CC.translate(p.getName() + ": Has been Authorized"));
                } else if (ConfigManager.FAIL_KICK_BOOLEAN && e.getClickedInventory().getType() != InventoryType.PLAYER) {
                    p.kickPlayer(ConfigManager.FAIL_KICK);
                }
            }
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
    }

    public static ItemStack setMeta(ItemStack m, String name) {
        if (m.getType() != Material.AIR && m.getType() != null) {
            ItemMeta meta = m.getItemMeta();
            meta.setDisplayName(name);
            m.setItemMeta(meta);
        }
        return m;
    }

    public String randomBlock() {
        String[] arr = new String[]{ "EMERALD", "DIAMOND_BLOCK", "DIAMOND", "GOLD_INGOT", "IRON_BLOCK", "IRON_INGOT", "REDSTONE", "CHEST", "HOPPER", "BEACON", "PAPER", "BOOK", "ICE", "ARROW", "SUGAR", "STONE", "GRASS", "NETHERRACK", "BOOKSHELF"};
        Random r = new Random();
        int randomNumber = r.nextInt(arr.length);
        return arr[randomNumber];
    }

    public static int randomSlot(int min, int max) {
        SecureRandom rand = new SecureRandom();
        return rand.nextInt(max - min + 1) + min;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        hCaptcha.join.remove(p);
        hCaptcha.saved_captcha.remove(p.getName() + "1");
        hCaptcha.saved_captcha.remove(p.getName() + "2");
        hCaptcha.saved_captcha_slot.remove(p.getName() + "3");
        hCaptcha.authenticated.remove(p);
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            if (!p.isOnGround()) {
                p.setAllowFlight(false);
            } else {
                Location from = e.getFrom();
                from.setYaw(e.getTo().getYaw());
                from.setPitch(e.getTo().getPitch());
                e.setTo(from);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!hCaptcha.authenticated.contains(p) && hCaptcha.join.contains(p)) {
            e.setCancelled(true);
        }
    }

    private void dumpAuthentication(Player player, String string) {
        File file = new File(hCaptcha.getInstance().getDataFolder() + "/database/" + player.getName() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException iOException) {
                iOException.printStackTrace();
                System.out.println("[hCaptcha] Failed trying to create authentication file.");
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(string);
            printWriter.close();
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public void addPlayer(Player player) {
        File file = new File(hCaptcha.getInstance().getDataFolder() + "/database/" + player.getName() + ".yml");
        if (!file.exists()) {
            hCaptcha.join.add(player);
            auth(player);
        }
    }
}

