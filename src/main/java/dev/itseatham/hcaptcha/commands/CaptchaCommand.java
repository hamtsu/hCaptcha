package dev.itseatham.hcaptcha.commands;

import dev.itseatham.hcaptcha.hCaptcha;
import dev.itseatham.hcaptcha.util.ConfigManager;
import dev.itseatham.hcaptcha.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CaptchaCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        if (args.length == 0) {
            sender.sendMessage(CC.translate("&8---------------------------"));
            sender.sendMessage(CC.translate("&c&lhCaptcha &7- &c&o" + hCaptcha.getInstance().getDescription().getVersion()));
            sender.sendMessage(CC.translate("&cSupport &7» &c&odiscord.itseatham.dev"));
            sender.sendMessage(CC.translate("&7&oUse /captcha help for help"));
            sender.sendMessage(CC.translate("&8---------------------------"));
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("captcha.reload") || sender.isOp()) {
                sender.sendMessage(ConfigManager.CONFIG_RELOAD);
                ConfigManager.refreshConfig();
                return true;
            }
            sender.sendMessage(ConfigManager.NO_PERMISSIONS);
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            for (int i = 0; i < ConfigManager.HELP.size(); i++) {
                player.sendMessage(CC.translate((ConfigManager.HELP).get(i).toString()));
            }

            return true;
        }
        if (args[0].equalsIgnoreCase("check")) {
            if (sender.hasPermission("captcha.check") || sender.isOp()) {
                try {
                    String string2 = String.valueOf(args[1]);
                    hCaptcha.getInstance().attemptToRead(player, string2);
                }
                catch (Exception exception) {
                    player.sendMessage(CC.translate("&cAn error has occured whilst fetching player data."));
                }
                return true;
            }
            sender.sendMessage(ConfigManager.NO_PERMISSIONS);
            return true;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (sender.hasPermission("captcha.remove") || sender.isOp()) {
                if (args[0] == null) {
                    player.sendMessage(CC.translate(ConfigManager.PROVIDE_PLAYER));
                    return true;
                } else {
                    File file = new File(hCaptcha.getInstance().getDataFolder() + "/database/" + args[1] + ".yml");
                    if (!file.exists()) {
                        player.sendMessage(CC.translate(ConfigManager.PLAYER_NOT_AUTH).replaceAll("%player%", args[1]));
                        return true;
                    } else {
                        file.delete();
                        ConfigManager.refreshConfig();
                        player.sendMessage(CC.translate(ConfigManager.PLAYER_REMOVED).replaceAll("%player%", args[1]));

                    }
                    return true;
                }
            }
            sender.sendMessage(ConfigManager.NO_PERMISSIONS);
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (sender.hasPermission("captcha.add") || sender.isOp()) {
                if (args[0] == null) {
                    player.sendMessage(CC.translate(ConfigManager.PROVIDE_PLAYER));
                    return true;
                } else {
                    File file = new File(hCaptcha.getInstance().getDataFolder() + "/database/" + args[1] + ".yml");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        }
                        catch (IOException iOException) {
                            iOException.printStackTrace();
                            System.out.println("[hCaptcha] Failed trying to create authentication file.");
                        }
                        try {
                            FileWriter fileWriter = new FileWriter(file, true);
                            PrintWriter printWriter = new PrintWriter(fileWriter);
                            printWriter.println(args[1] + ": Has been Authorized");
                            printWriter.close();
                        }
                        catch (IOException iOException) {
                            iOException.printStackTrace();
                        }
                        ConfigManager.refreshConfig();
                        player.sendMessage(CC.translate(ConfigManager.PLAYER_ADDED).replaceAll("%player%", args[1]));
                    } else {
                        player.sendMessage(CC.translate(ConfigManager.PLAYER_ALREADY_AUTH).replaceAll("%player%", args[1]));
                        return true;

                    }
                    return true;
                }
            }
        }
        return true;
    }
}

