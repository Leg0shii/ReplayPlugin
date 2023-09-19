package de.legoshi.replaymod.replay;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.database.AsyncMySQL;
import de.legoshi.replaymod.utils.ChatHelper;
import de.legoshi.replaymod.utils.PlayerMoveTick;
import de.legoshi.replaymod.utils.TitleManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor
public class Replay {

    private final AsyncMySQL mySQL;

    public void playReplay(Player player, int clipID) {
        ResultSet resultSet = mySQL.query("SELECT * FROM playerclip WHERE clipid = '" + clipID + "';");
        ArrayList<PlayerMoveTick> playerMoveTickArrayList = new ArrayList<>();
        String worldName;
        String playerName;

        final int[] startSize = {0};
        int endSize;

        try {
            if (resultSet.next()) {
                Blob blob = resultSet.getBlob("clip");
                String s = new String(blob.getBytes(1L, (int) blob.length()));
                String[] args = s.split(":");
                worldName = resultSet.getString("world");
                String playerUUID = resultSet.getString("userid");
                playerName = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName();

                for (String moveString : args) {
                    PlayerMoveTick playerMoveTick = new PlayerMoveTick(moveString);
                    playerMoveTickArrayList.add(playerMoveTick);
                }

                endSize = playerMoveTickArrayList.size();
            } else {
                player.sendMessage(ChatHelper.PREFIX_ERR + "Something went wrong while trying to load the replay...");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        PlayerMoveTick pMT = playerMoveTickArrayList.get(playerMoveTickArrayList.size() - 1);
        player.teleport(new Location(Bukkit.getWorld(worldName), pMT.getX(), pMT.getY(), pMT.getZ()));
        final FakePlayer[] fakePlayer = {new FakePlayer(player, worldName, playerName, pMT)};
        fakePlayer[0].spawn(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                TitleManager.sendActionBar(player, "§6 Time: §7(§f" + (int) Math.floor(startSize[0]++ / 20) + "s§7/§f" + (int) Math.floor(endSize / 20) + "s§7)");
                if (playerMoveTickArrayList.size() == 0) {
                    fakePlayer[0].destroy(player);
                    cancel();
                } else {
                    PlayerMoveTick playerMoveTick = playerMoveTickArrayList.get(playerMoveTickArrayList.size() - 1);
                    handlePlayer(player, fakePlayer[0], playerMoveTick);
                    fakePlayer[0] = handleFakePlayerWorldChange(player, fakePlayer[0], playerMoveTick);
                    fakePlayer[0].move(player, playerMoveTick);
                    playerMoveTickArrayList.remove(playerMoveTickArrayList.size() - 1);
                }
            }
        }.runTaskTimer(Main.getInstance(), 40L, 1);
    }

    private void handlePlayer(Player player, FakePlayer fakePlayer, PlayerMoveTick playerMoveTick) {
        if (Math.abs(fakePlayer.prevMoveX - playerMoveTick.getX()) > 20 ||
                Math.abs(fakePlayer.prevMoveY - playerMoveTick.getY()) > 20 ||
                Math.abs(fakePlayer.prevMoveZ - playerMoveTick.getZ()) > 20) {
            Location nextLocation = new Location(Bukkit.getWorld(playerMoveTick.getWorld()),  playerMoveTick.getX(),  playerMoveTick.getY(),  playerMoveTick.getZ());
            player.teleport(nextLocation);
        }

    }

    private FakePlayer handleFakePlayerWorldChange(Player player, FakePlayer fakePlayer, PlayerMoveTick playerMoveTick) {
        if (!fakePlayer.prevWorld.equals(playerMoveTick.getWorld())) {
            fakePlayer.destroy(player);
            FakePlayer newFakePlayer = new FakePlayer(player, playerMoveTick.getWorld(), fakePlayer.name, playerMoveTick);
            Location nextLocation = new Location(Bukkit.getWorld(playerMoveTick.getWorld()),  playerMoveTick.getX(),  playerMoveTick.getY(),  playerMoveTick.getZ());
            newFakePlayer.spawn(player);
            player.teleport(nextLocation);
            return newFakePlayer;
        }
        return fakePlayer;
    }

}
