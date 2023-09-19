package de.legoshi.replaymod.listener;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PlayerManager playerManager;

    public JoinListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    private void onJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (Main.isRunning()) {
            playerManager.playerJoin(player);
        }
    }

}
