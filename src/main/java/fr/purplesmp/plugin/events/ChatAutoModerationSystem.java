package fr.purplesmp.plugin.events;

import fr.purplesmp.plugin.Main;
import fr.purplesmp.plugin.annotations.EventHandlerInfo;
import fr.purplesmp.plugin.util.Colors;
import fr.purplesmp.plugin.util.PerspectiveAPILink;
import fr.purplesmp.plugin.util.ToxicityAttributes;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

@EventHandlerInfo
public class ChatAutoModerationSystem implements Listener {

    @EventHandler
    public static void OnPlayerJoinEvent(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        sendInfoMessage(p);
    }

    @EventHandler
    public static void AsyncPlayerChatSendEvent(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();

        String message = event.getMessage();
        PerspectiveAPILink.ToxicityScores percentage;
        try {
            percentage = PerspectiveAPILink.check(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(percentage == null) {
            return;
        }

        ToxicityAttributes attribute = null;

        if(percentage.getSevereToxicity() < 40) {
            if(percentage.getToxicity() < 40) {
                if(percentage.getIdentityAttack() < 40) {
                    if(percentage.getInsult() < 40) {
                        if(percentage.getProfanity() < 40) {
                            if(percentage.calculateAverageScore() > 40) {
                                attribute = ToxicityAttributes.AVERAGE_SCORE;
                            } else {
                                event.setMessage("§8[§6" + percentage.calculateAverageScore() + "%§8] §r" + message);
                            }
                        } else {
                            attribute = ToxicityAttributes.PROFANITY;
                        }
                    } else {
                        attribute = ToxicityAttributes.INSULT;
                    }
                } else {
                    attribute = ToxicityAttributes.IDENTITY_ATTACK;
                }
            } else {
                attribute = ToxicityAttributes.TOXICITY;
            }
        } else {
            attribute = ToxicityAttributes.SEVERE_TOXICITY;
        }

        if (attribute != null &! p.hasPermission("bypass.automod")) {
            event.setMessage("§4§l§k" + event.getMessage() + "§r §c(" + attribute + "-" + percentage.get(attribute) + "%)");
            sendInfoMessage(p);
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempmute " + p.getName() + " 5m Détection Automatique KiwiAntiCheat");});

        } else {
            event.setMessage("§8[§6" + percentage.calculateAverageScore() + "%§8] §r" + message);
        }
    }

    public static void sendInfoMessage(Player p) {
        p.sendMessage(Main.prefix + "§8» "+ Colors.COLOR_800 + "§7Information importante !");
        p.sendMessage("§l§6⚠ §r§eLe chat général est désormais sous contrôle par " + ChatColor.of("#58D68D") + "§lK" + ChatColor.of("#2ECC71") + "§lI" + ChatColor.of("#28B463") + "§lW"  + ChatColor.of("#1D8348") + "§lI "  + ChatColor.of("#D1F2EB") + "§lA"  + ChatColor.of("#A3E4D7") + "§lN"  + ChatColor.of("#76D7C4") + "§lT"  + ChatColor.of("#48C9B0") + "§lI"  + ChatColor.of("#1ABC9C") + "§lC"  + ChatColor.of("#17A589") + "§lH"  + ChatColor.of("#148F77") + "§lE"  + ChatColor.of("#117864") + "§lA"  + ChatColor.of("#0E6251") + "§lT§r§e !");
        p.sendMessage("§eIl est surveillé en direct par une IA tierce (§7PerspectiveAPI§e) pour détecter les propos offensants.");
        p.sendMessage("§c⚠ Cette IA peut se tromper et est très sensible !");
        p.sendMessage("§aNous vous demandons donc de rester respectueux.");
    }
}
