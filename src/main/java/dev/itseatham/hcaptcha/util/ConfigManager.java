package dev.itseatham.hcaptcha.util;

import dev.itseatham.hcaptcha.hCaptcha;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {
    public static int KICK_AFTER = hCaptcha.getInstance().getConfig().getInt("kick-after");
    public static boolean FAIL_KICK_BOOLEAN = hCaptcha.getInstance().getConfig().getBoolean("kick-on-fail");
    public static boolean USE_GLASS = hCaptcha.getInstance().getConfig().getBoolean("use-glass");
    public static String LICENSE_KEY = hCaptcha.getInstance().getConfig().getString("license-key");
    public static String PLAYER_NOT_AUTH = hCaptcha.getInstance().getConfig().getString("player-not-auth-msg");
    public static boolean USE_WELCOME_MSG = hCaptcha.getInstance().getConfig().getBoolean("use-welcome-msg");
    public static List<String> WELCOME_MSG = hCaptcha.getInstance().getConfig().getStringList("welcome-msg");
    public static String PLAYER_REMOVED = hCaptcha.getInstance().getConfig().getString("player-removed-msg");
    public static String PLAYER_ADDED = hCaptcha.getInstance().getConfig().getString("player-added-msg");
    public static String PLAYER_ALREADY_AUTH = hCaptcha.getInstance().getConfig().getString("player-already-auth-msg");
    public static String PROVIDE_PLAYER = hCaptcha.getInstance().getConfig().getString("provide-player-msg");
    public static List<String> HELP = hCaptcha.getInstance().getConfig().getStringList("help-msg");
    public static String PLAYER_IS_AUTH = hCaptcha.getInstance().getConfig().getString("player-is-auth");
    public static String CAPTCHA_GUI_NAME = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("captcha-gui-name"));
    public static String CAPTCHA_REAL_ITEM = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("captcha-real-item"));
    public static String CAPTCHA_FAKE_ITEM = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("captcha-fake-item"));
    public static String SUCCESSFULLY_AUTHENTICATED = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("authentication-msg"));
    public static String FAIL_KICK = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("fail-kick-reason"));
    public static String CONFIG_RELOAD = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("config-reload-msg"));
    public static String NO_PERMISSIONS = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("no-permissions-msg"));
    public static FileConfiguration config = hCaptcha.getInstance().getConfig();

    public static void refreshConfig() {
        hCaptcha.getInstance().saveDefaultConfig();
        hCaptcha.getInstance().reloadConfig();
        config = hCaptcha.getInstance().getConfig();
        USE_GLASS = hCaptcha.getInstance().getConfig().getBoolean("use-glass");
        KICK_AFTER = hCaptcha.getInstance().getConfig().getInt("kick-after");
        FAIL_KICK_BOOLEAN = hCaptcha.getInstance().getConfig().getBoolean("kick-on-fail");
        PLAYER_NOT_AUTH = hCaptcha.getInstance().getConfig().getString("player-not-auth-msg");
        USE_WELCOME_MSG = hCaptcha.getInstance().getConfig().getBoolean("use-welcome-msg");
        WELCOME_MSG = hCaptcha.getInstance().getConfig().getStringList("welcome-msg");
        PLAYER_REMOVED = hCaptcha.getInstance().getConfig().getString("player-removed-msg");
        PLAYER_ADDED = hCaptcha.getInstance().getConfig().getString("player-added-msg");
        PLAYER_ALREADY_AUTH = hCaptcha.getInstance().getConfig().getString("player-already-auth-msg");
        PROVIDE_PLAYER = hCaptcha.getInstance().getConfig().getString("provide-player-msg");
        HELP = hCaptcha.getInstance().getConfig().getStringList("help-msg");
        CAPTCHA_GUI_NAME = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("captcha-gui-name"));
        CAPTCHA_REAL_ITEM = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("captcha-real-item"));
        CAPTCHA_FAKE_ITEM = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("captcha-fake-item"));
        SUCCESSFULLY_AUTHENTICATED = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("authentication-msg"));
        FAIL_KICK = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("fail-kick-reason"));
        CONFIG_RELOAD = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("config-reload-msg"));
        NO_PERMISSIONS = ChatColor.translateAlternateColorCodes('&', hCaptcha.getInstance().getConfig().getString("no-permissions-msg"));
    }
}

