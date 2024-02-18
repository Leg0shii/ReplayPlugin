package de.legoshi.replaymod.listener;

import de.legoshi.replaymod.PlayerManager;
import de.legoshi.replaymod.PlayerObject;
import de.legoshi.replaymod.utils.CheatDetection;
import de.legoshi.replaymod.utils.PlayerMoveTick;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class MovementListener implements Listener {

    private final PlayerManager playerManager;
    private final CheatDetection cheatDetection;

    @EventHandler
    public void onMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerObject playerObject = playerManager.playerHashMap.get(player);
        if (playerObject == null) return;

        Location playerLocation = player.getLocation();
        String world = playerLocation.getWorld().getName();
        double x = playerLocation.getX();
        double y = playerLocation.getY();
        double z = playerLocation.getZ();
        float pitch = playerLocation.getPitch();
        float yaw = playerLocation.getYaw();
        boolean ground = player.isOnGround();
        boolean sneak = player.isSneaking();
        boolean prac = player.getInventory().contains(Material.SLIME_BALL);
        PlayerMoveTick playerMoveTick = new PlayerMoveTick(x, y, z ,yaw, pitch, sneak, ground, prac, world);
        playerManager.playerHashMap.get(player).addPlayerMoveTick(playerMoveTick);

        playerObject.updatePlayerVel(event.getTo().getY() - event.getFrom().getY());
        playerObject.updatePlayerY(event.getTo().getY());
        cheatDetection.checkPlayerMovement(player);
    }

}
