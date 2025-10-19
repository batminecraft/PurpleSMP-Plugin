package fr.purplesmp.plugin.commands;

import fr.purplesmp.plugin.annotations.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import fr.purplesmp.plugin.util.Colors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandsInfos({
        @CommandInfo(
                name = "gmc",
                description = "Passe en mode créatif",
                aliases = {"gamemodec", "gamec", "gm1", "gamem1", "gamemode1", "gmcreative"},
                permission = "gmc.change",
                usage = "/gmc [joueur]"
        ),
        @CommandInfo(
                name = "gms",
                description = "Passe en mode survie",
                aliases = {"gamemodes", "games", "gm0", "gamem0", "gamemode0", "gmsurvival"},
                permission = "gms.change",
                usage = "/gms [joueur]"
        ),
        @CommandInfo(
                name = "gma",
                description = "Passe en mode aventure",
                aliases = {"gamemodea", "gamea", "gm2", "gamem2", "gamemode2", "gmadventure"},
                permission = "gma.change",
                usage = "/gma [joueur]"
        ),
        @CommandInfo(
                name = "gmsp",
                description = "Passe en mode spectateur",
                aliases = {"gamemodesp", "gamesp", "gm3", "gamem3", "gamemode3", "gmspectator"},
                permission = "gmsp.change",
                usage = "/gmsp [joueur]"
        ),
        @CommandInfo(
                name = "gm",
                description = "Change ton mode de jeu via un argument (0-3 ou nom, joueur optionnel)",
                aliases = {"gamemode"},
                permission = "gm.change",
                usage = "/gm <0|1|2|3|survival|creative|adventure|spectator> [joueur]"
        )
})
@TabCompleterInfo(command = "gm")
public class GameModeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        Player player = sender instanceof Player ? (Player) sender : null;
        Player targetPlayer = player;
        GameMode targetMode = null;

        // --- Détection du mode ---
        switch (cmd) {
            case "gmc": case "gamemodec": case "gamec": case "gm1": case "gamem1": case "gamemode1": case "gmcreative":
                targetMode = GameMode.CREATIVE;
                break;
            case "gms": case "gamemodes": case "games": case "gm0": case "gamem0": case "gamemode0": case "gmsurvival":
                targetMode = GameMode.SURVIVAL;
                break;
            case "gma": case "gamemodea": case "gamea": case "gm2": case "gamem2": case "gamemode2": case "gmadventure":
                targetMode = GameMode.ADVENTURE;
                break;
            case "gmsp": case "gamemodesp": case "gamesp": case "gm3": case "gamem3": case "gamemode3": case "gmspectator":
                targetMode = GameMode.SPECTATOR;
                break;
            case "gm": case "gamemode":
                if (args.length == 0) {
                    sender.sendMessage(Colors.COLOR_700 + "❌ Usage : §b" + Colors.COLOR_500 + "/gm <mode> [joueur]");
                    return true;
                }
                targetMode = getModeFromArg(args[0]);
                if (targetMode == null) {
                    sender.sendMessage(Colors.COLOR_700 + "❌ Mode inconnu : §b" + Colors.COLOR_500 + args[0]);
                    return true;
                }
                break;
        }

        if (targetMode == null) return true;

        // --- Détection du joueur cible ---
        if (args.length >= 1 && !cmd.equals("gm")) {
            Player possibleTarget = Bukkit.getPlayer(args[0]);
            if (possibleTarget != null) targetPlayer = possibleTarget;
        } else if (args.length >= 2 && cmd.equals("gm")) {
            Player possibleTarget = Bukkit.getPlayer(args[1]);
            if (possibleTarget != null) targetPlayer = possibleTarget;
        }

        if (targetPlayer == null) {
            sender.sendMessage(Colors.COLOR_700 + "❌ Joueur introuvable !");
            return true;
        }

        // --- Vérification des permissions ---
        boolean isSelf = targetPlayer.equals(player);
        String basePerm = getBasePermission(cmd);

        if (!hasPermission(sender, basePerm, isSelf)) {
            sender.sendMessage(Colors.COLOR_700 + "❌ Tu n’as pas la permission d’exécuter cette commande !");
            return true;
        }

        // --- Changement du mode ---
        targetPlayer.setGameMode(targetMode);

        // --- Messages ---
        if (isSelf) {
            sender.sendMessage(Colors.COLOR_500 + "✔ Ton mode de jeu est maintenant §b" +
                    Colors.COLOR_700 + targetMode.name().toLowerCase());
        } else {
            sender.sendMessage(Colors.COLOR_500 + "✔ Le mode de §b" + Colors.COLOR_700 + targetPlayer.getName() +
                    Colors.COLOR_500 + " est maintenant §b" + Colors.COLOR_700 + targetMode.name().toLowerCase());
            targetPlayer.sendMessage(Colors.COLOR_500 + "✔ Ton mode de jeu a été changé en §b" +
                    Colors.COLOR_700 + targetMode.name().toLowerCase() +
                    Colors.COLOR_500 + " par §b" + Colors.COLOR_700 + sender.getName());
        }

        return true;
    }

    private boolean hasPermission(CommandSender sender, String basePerm, boolean self) {
        if (sender.hasPermission(basePerm + ".*")) return true;
        return sender.hasPermission(basePerm + (self ? ".self" : ".others"));
    }

    private String getBasePermission(String cmd) {
        if (cmd.startsWith("gmc")) return "gmc.change";
        if (cmd.startsWith("gms")) return "gms.change";
        if (cmd.startsWith("gma")) return "gma.change";
        if (cmd.startsWith("gmsp")) return "gmsp.change";
        return "gm.change";
    }

    private GameMode getModeFromArg(String arg) {
        return switch (arg.toLowerCase()) {
            case "0", "survival" -> GameMode.SURVIVAL;
            case "1", "creative" -> GameMode.CREATIVE;
            case "2", "adventure" -> GameMode.ADVENTURE;
            case "3", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("gm") && !command.getName().equalsIgnoreCase("gamemode"))
            return null;

        if (args.length == 1) {
            List<String> modes = Arrays.asList("0", "1", "2", "3", "survival", "creative", "adventure", "spectator");
            List<String> completions = new ArrayList<>();
            for (String mode : modes) if (mode.startsWith(args[0].toLowerCase())) completions.add(mode);
            return completions;
        } else if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) if (p.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                completions.add(p.getName());
            return completions;
        }

        return null;
    }
}