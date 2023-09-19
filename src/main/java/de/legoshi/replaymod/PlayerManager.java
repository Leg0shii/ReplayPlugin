package de.legoshi.replaymod;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerManager {

    public HashMap<Player, PlayerObject> playerHashMap = new HashMap<>();

    public void playerJoin(Player player) {
        playerHashMap.put(player, new PlayerObject(player));
    }

    public void playerQuit(Player player) {
        playerHashMap.remove(player);
    }

}
