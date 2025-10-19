package fr.purplesmp.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    public static String prefix = "§8[§x§F§0§8§C§F§D§lP§x§E§4§7§B§F§1§lu§x§D§8§6§A§E§6§lr§x§C§B§5§9§D§A§lp§x§B§F§4§8§C§F§ll§x§B§3§3§6§C§3§le§x§A§7§2§5§B§7§lS§x§9§A§1§4§A§C§lM§x§8§E§0§3§A§0§lP§8] §r";

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info(prefix + "§7» ᴀᴄᴛɪᴠᴀᴛɪᴏɴ ᴅᴜ ᴘʟᴜɢɪɴ ᴇɴ ᴄᴏᴜʀꜱ");
        getLogger().info("📚 ᴇɴʀᴇɢɪꜱᴛʀᴇᴍᴇɴᴛꜱ ᴇɴ ᴄᴏᴜʀꜱ...");
        new Registering(this).registerAll();
        getLogger().info("✅ ᴘʟᴜɢɪɴ ᴀᴄᴛɪᴠᴇ ᴇᴛ ᴇɴʀᴇɢɪꜱᴛʀᴇᴍᴇɴᴛꜱ ᴇꜰꜰᴇᴄᴛᴜᴇꜱ");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ ᴘʟᴜɢɪɴ ᴅᴇꜱᴀᴄᴛɪᴠᴇ");
    }

    public static Main getInstance() {
        return instance;
    }
}