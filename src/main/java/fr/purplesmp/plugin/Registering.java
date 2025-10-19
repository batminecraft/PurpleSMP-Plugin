package fr.purplesmp.plugin;

import fr.purplesmp.plugin.annotations.*;
import fr.purplesmp.plugin.util.CommandRegisterUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Set;

public class Registering {

    private final JavaPlugin plugin;
    private final CommandRegisterUtil commandUtil;

    public Registering(JavaPlugin plugin) {
        this.plugin = plugin;
        this.commandUtil = new CommandRegisterUtil(plugin);
    }

    public void registerAll() {
        // ------------------- COMMANDES -------------------
        Reflections commandRef = new Reflections("fr.purplesmp.plugin.commands");
        Set<Class<?>> commandClassesSingle = commandRef.getTypesAnnotatedWith(CommandInfo.class);
        Set<Class<?>> commandClassesMultiple = commandRef.getTypesAnnotatedWith(CommandsInfos.class);

        // Une seule commande
        for (Class<?> clazz : commandClassesSingle) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (instance instanceof CommandExecutor) {
                    CommandInfo info = clazz.getAnnotation(CommandInfo.class);
                    commandUtil.registerCommand(info, (CommandExecutor) instance);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        // Plusieurs commandes
        for (Class<?> clazz : commandClassesMultiple) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (instance instanceof CommandExecutor) {
                    CommandsInfos infos = clazz.getAnnotation(CommandsInfos.class);
                    for (CommandInfo info : infos.value()) {
                        commandUtil.registerCommand(info, (CommandExecutor) instance);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        // ------------------- TAB COMPLETERS -------------------
        Set<Class<?>> tabClasses = commandRef.getTypesAnnotatedWith(TabCompleterInfo.class);
        for (Class<?> clazz : tabClasses) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (instance instanceof org.bukkit.command.TabCompleter) {
                    TabCompleterInfo info = clazz.getAnnotation(TabCompleterInfo.class);
                    PluginCommand cmd = plugin.getCommand(info.command());
                    if (cmd != null) cmd.setTabCompleter((org.bukkit.command.TabCompleter) instance);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        // ------------------- EVENTS -------------------
        Reflections eventRef = new Reflections("fr.purplesmp.plugin.events");
        Set<Class<?>> eventClasses = eventRef.getTypesAnnotatedWith(EventHandlerInfo.class);
        for (Class<?> clazz : eventClasses) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (instance instanceof Listener) {
                    Bukkit.getPluginManager().registerEvents((Listener) instance, plugin);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
