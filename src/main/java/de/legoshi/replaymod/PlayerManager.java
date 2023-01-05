package de.legoshi.replaymod;

import de.legoshi.replaymod.database.DBManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {

    public HashMap<Player, PlayerObject> playerHashMap = new HashMap<>();
    private final DBManager dbManager;

    public PlayerManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public void playerJoin(Player player) {
        PlayerObject playerObject = new PlayerObject(player);
        playerHashMap.put(player, playerObject);

        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            dbManager.saveCurrentClip(playerObject, "1");
        }, 20L*Main.getInstance().joinRecTime);
    }

    public void playerQuit(Player player) {
        PlayerObject playerObject = playerHashMap.get(player);
        dbManager.mySQL.update("UPDATE playerclip SET publicclip = true WHERE publicclip = false AND playerUUID =  '" + player.getUniqueId().toString() + "';");
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            dbManager.saveCurrentClip(playerObject, "0");
            playerHashMap.remove(player);
        }, 20L*Main.getInstance().joinRecTime);
    }

}
