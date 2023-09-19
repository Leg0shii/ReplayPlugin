package de.legoshi.replaymod.utils;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.PlayerManager;
import de.legoshi.replaymod.PlayerObject;
import de.legoshi.replaymod.database.DBManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class CheatDetection {

    private final PlayerManager playerManager;
    private final DBManager dbManager;

    public void checkPlayerMovement(Player player) {
        if (player.hasPermission("replay.ignore")
            || player.isOp()
            || player.getGameMode().equals(GameMode.CREATIVE)
            || player.getGameMode().equals(GameMode.SPECTATOR)
            || player.hasPermission("essentials.fly")
        ) return;

        PlayerObject playerObject = playerManager.playerHashMap.get(player);
        double currentYVel = Math.round(playerObject.getCurrentYVel() * 100.0) / 100.0;
        double prevYVel = Math.round(playerObject.getPrevYVel() * 100.0) / 100.0;
        if (currentYVel > 0 && currentYVel == prevYVel) {
            playerObject.setYVelCount(playerObject.getYVelCount()+1);
        }
        if (playerObject.getYVelCount() > 2 && !playerObject.isFlyRecording()) {
            playerObject.setYVelCount(0);
            if (!validateFly(player)) return;
            for(Player all : Bukkit.getOnlinePlayers()) {
                if (all.hasPermission("replay") || all.isOp()) {
                    all.sendMessage("§6" + player.getDisplayName() + " §cis flagged for flying at §6("
                            + Math.round(player.getLocation().getX()) + ", "
                            + Math.round(player.getLocation().getY()) + ", "
                            + Math.round(player.getLocation().getZ()) + ") §c in §6 " + player.getLocation().getWorld().getName()
                    );
                }
            }
            startRecordingTimer(playerObject);
        }
    }

    public void startRecordingTimer(PlayerObject playerObject) {
        playerObject.setFlyRecording(true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            dbManager.saveCurrentClip(playerObject, "2;true");
            playerObject.setFlyRecording(false);
        }, 20L*Main.getInstance().joinRecTime-3);
    }

    private boolean validateFly(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        for (int x = -1; x < 2; x++) {
            for (int y = -2; y < 3; y++) {
                for (int z = -1; z < 2; z++) {
                    Block blockToCheck = world.getBlockAt(location.clone().add(x, y, z));
                    if (blockToCheck.isLiquid()) return false;
                    if (blockToCheck.getType().toString().contains("STAIR")) return false;
                    if (blockToCheck.getType() == Material.LADDER) return false;
                    if (blockToCheck.getType() == Material.VINE) return false;
                    if (blockToCheck.getType() == Material.PISTON_BASE) return false;
                    if (blockToCheck.getType() == Material.ANVIL) return false;
                }
            }
        }
        if (Math.round(location.getY()) == location.getY() || Math.floor(location.getY())+0.5 == location.getY()) return false;

        return true;
    }

}
