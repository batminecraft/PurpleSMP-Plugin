package fr.purplesmp.plugin.util;

import fr.purplesmp.plugin.annotations.CommandInfo;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class CommandRegisterUtil {

    private final JavaPlugin plugin;

    public CommandRegisterUtil(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommand(CommandInfo info, CommandExecutor executor) {
        PluginCommand cmd = plugin.getCommand(info.name());
        if (cmd == null) {
            System.out.println("⚠️ Commande " + info.name() + " non déclarée dans plugin.yml !");
            return;
        }

        cmd.setExecutor(executor);

        // Usage
        if (!info.usage().isEmpty()) cmd.setUsage(info.usage());

        // Aliases
        if (info.aliases().length > 0) cmd.setAliases(Arrays.asList(info.aliases()));

        // Permissions
        if (!info.permission().isEmpty()) cmd.setPermission(info.permission());
        if (!info.permissionMessage().isEmpty()) cmd.setPermissionMessage(info.permissionMessage());

        System.out.println("Commande " + info.name() + " enregistrée ✅");
    }
}

