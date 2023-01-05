package de.legoshi.replaymod.command;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.PlayerManager;
import de.legoshi.replaymod.PlayerObject;
import de.legoshi.replaymod.database.DBManager;
import de.legoshi.replaymod.utils.CheatDetection;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SaveReplayCommand implements CommandExecutor {

    private final DBManager dbManager;
    private final PlayerManager playerManager;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Main.running) {
            commandSender.sendMessage("Plugin is currently not running. (Database?)");
            return false;
        }

        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (!player.isOp()) return false;

        PlayerObject playerObject = playerManager.playerHashMap.get(Bukkit.getPlayer(strings[0]));
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            dbManager.saveCurrentClip(playerObject, "2;true");
            playerObject.setFlyRecording(false);
        }, 20L*Main.getInstance().joinRecTime-3);

        for(Player all : Bukkit.getOnlinePlayers()) {
            if(all.isOp()) all.sendMessage("§6" + strings[0] + "§c flagged by grim.");
        }

        return false;
    }

}
