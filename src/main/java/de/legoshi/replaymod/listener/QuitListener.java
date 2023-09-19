package de.legoshi.replaymod.listener;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    private final PlayerManager playerManager;

    public QuitListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    private void onQuitEvent(PlayerQuitEvent event) {
        playerQuit(event.getPlayer());
    }

    @EventHandler
    private void onKickEvent(PlayerKickEvent event) {
        playerQuit(event.getPlayer());
    }

    private void playerQuit(Player player) {
        if (Main.isRunning()) {
            playerManager.playerQuit(player);
        }
    }

}
