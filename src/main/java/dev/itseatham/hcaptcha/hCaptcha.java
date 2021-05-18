package dev.itseatham.hcaptcha;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import dev.itseatham.hcaptcha.commands.CaptchaCommand;
import dev.itseatham.hcaptcha.util.ConfigManager;
import dev.itseatham.hcaptcha.license.License;
import dev.itseatham.hcaptcha.util.CC;
import dev.itseatham.hcaptcha.listeners.MainListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class hCaptcha
extends JavaPlugin {
    public static hCaptcha instance;
    public static PluginManager plugin;
    public static Set<Player> authenticated;
    public static Set<Player> join;
    public static HashMap<String, ItemStack> saved_captcha;
    public static HashMap<String, Integer> saved_captcha_slot;
    public static HashMap<String, String> saved_captcha_info;

    public void onEnable() {
        instance = this; //Avoid NPE when getting main instance
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        getCommand("captcha").setExecutor(new CaptchaCommand());
        plugin.registerEvents(new MainListener(), this);
        File file = new File(this.getDataFolder() + "/database");
        file.mkdirs();

        if(!new License(this, ConfigManager.LICENSE_KEY, "https://panel.itseatham.dev/api/v1", "7efc7ba773653d6f6cece6b00f31a4bf25df8576").verify()) {
            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.getScheduler().cancelTasks(this);
        } else {
            getServer().getConsoleSender().sendMessage(CC.translate("&8----------------------------"));
            getServer().getConsoleSender().sendMessage(CC.translate("&8[&chCaptcha&8]"));
            getServer().getConsoleSender().sendMessage(CC.translate("&7Has now been &aENABLED&7!"));
            getServer().getConsoleSender().sendMessage(CC.translate("&7Thanks for Purchasing!"));
            getServer().getConsoleSender().sendMessage(CC.translate("&8----------------------------"));
        }
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(CC.translate("&8----------------------------"));
        getServer().getConsoleSender().sendMessage(CC.translate("&8[&chCaptcha&8]"));
        getServer().getConsoleSender().sendMessage(CC.translate("&7Has now been &4DISABLED&7!"));
        getServer().getConsoleSender().sendMessage(CC.translate("&8----------------------------"));
    }

    public static hCaptcha getInstance() {
        return instance;
    }

    public void attemptToRead(Player player, String string) {
        File file = new File(this.getDataFolder() + "/database/" + string + ".yml");
        if (!file.exists()) {
            player.sendMessage(CC.translate(ConfigManager.PLAYER_NOT_AUTH).replaceAll("%player%", string));
        }
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            try {
                String string2;
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                bufferedReader = new BufferedReader(new FileReader(file));
                while ((string2 = bufferedReader.readLine()) != null) {
                    player.sendMessage(CC.translate(ConfigManager.PLAYER_IS_AUTH).replaceAll("%player%", string));
                }
            }
            catch (IOException iOException) {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (fileReader != null) {
                        fileReader.close();
                    }
                }
                catch (IOException iOException2) {
                    player.sendMessage(CC.translate(ConfigManager.PLAYER_NOT_AUTH).replaceAll("%player%", string));
                }
            }
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (IOException iOException) {
                player.sendMessage(CC.translate(ConfigManager.PLAYER_NOT_AUTH).replaceAll("%player%", string));
            }
        }
    }

    static {
        plugin = Bukkit.getServer().getPluginManager();
        authenticated = new HashSet<Player>();
        join = new HashSet<Player>();
        saved_captcha = new HashMap();
        saved_captcha_slot = new HashMap();
        saved_captcha_info = new HashMap();
    }
}

