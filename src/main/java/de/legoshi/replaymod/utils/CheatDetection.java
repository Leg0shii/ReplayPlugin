package de.legoshi.replaymod.utils;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.PlayerManager;
import de.legoshi.replaymod.PlayerObject;
import de.legoshi.replaymod.database.DBManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
        addFlyingCount(playerObject);
        checkLadderExploit(player, playerObject);

        if (playerObject.getYVelCount() > 2 && !playerObject.isFlyRecording()) {
            playerObject.setLadderCount(0);
            playerObject.setYVelCount(0);
            if (!validateFly(player)) return;
            recordExploit(player, playerObject, "is flagged for flying", 2);
        }


        if(playerObject.getLadderCount() > 2 && !playerObject.isFlyRecording()) {
            playerObject.setLadderCount(0);
            playerObject.setYVelCount(0);
            recordExploit(player, playerObject, "abused the forge ladder hitbox exploit", 3);
        }
    }

    private void recordExploit(Player player, PlayerObject playerObject, String message, int type) {
        for(Player all : Bukkit.getOnlinePlayers()) {
            if (all.hasPermission("replay") || all.isOp()) {
                all.sendMessage("§6" + player.getDisplayName() + " §c" + message + " at §6("
                        + Math.round(player.getLocation().getX()) + ", "
                        + Math.round(player.getLocation().getY()) + ", "
                        + Math.round(player.getLocation().getZ()) + ") §c in §6 " + player.getLocation().getWorld().getName()
                );
            }
        }
        startRecordingTimer(playerObject, type);
    }

    private void checkLadderExploit(Player player, PlayerObject playerObject) {
        if(playerObject.isFlyRecording()) {
            return;
        }

        if(player.isOnGround()) {
            return;
        }

        Block block = player.getLocation().getBlock();
        Block blockAbove = block.getRelative(BlockFace.UP);

        if(block.getType().equals(Material.AIR) && (blockAbove.getType().equals(Material.LADDER) || blockAbove.getType().equals(Material.VINE))) {
                if(Double.compare(playerObject.getPrevY(), playerObject.getCurrentY()) == 0 || Double.compare(playerObject.getCurrentYVel(), 0.11760000228882461D) == 0) {
                    playerObject.setLadderCount(playerObject.getLadderCount()+1);
                    return;
                }
        }


//        if(block.getType().equals(Material.LADDER) || block.getType().equals(Material.VINE)) {
//            return;
//        }

//        Location playerLoc = player.getLocation();
//        List<Location> nearby = new ArrayList<>();
//        nearby.add(playerLoc.clone().add(.3D, 0, 0));
//        nearby.add(playerLoc.clone().add(-.3D, 0, 0));
//        nearby.add(playerLoc.clone().add(0, 0, .3D));
//        nearby.add(playerLoc.clone().add(0, 0, -.3D));
//
//        for(Location loc : nearby) {
//            if(loc.getBlock().getType().equals(Material.LADDER) || loc.getBlock().getType().equals(Material.VINE)) {
//                if(Double.compare(playerObject.getCurrentYVel(), 0.11760000228882461D) == 0) {
//                    player.sendMessage("X/Z exploit test");
//                    playerObject.setLadderCount(playerObject.getLadderCount()+1);
//                    return;
//                }
//
//            }
//        }
    }

    private void addFlyingCount(PlayerObject playerObject) {
        double currentYVel = Math.round(playerObject.getCurrentYVel() * 100.0) / 100.0;
        double prevYVel = Math.round(playerObject.getPrevYVel() * 100.0) / 100.0;
        if (currentYVel > 0 && currentYVel == prevYVel) {
            playerObject.setYVelCount(playerObject.getYVelCount()+1);
        }
    }


    public void startRecordingTimer(PlayerObject playerObject, int type) {
        playerObject.setFlyRecording(true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            dbManager.saveCurrentClip(playerObject, type +";true");
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
