package de.legoshi.replaymod.command;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.database.AsyncMySQL;
import de.legoshi.replaymod.inventory.ReplayGUI;
import de.legoshi.replaymod.inventory.ReplayGUIPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ReplayGUICommand implements CommandExecutor {

    private final AsyncMySQL mySQL;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        if (!player.hasPermission("replay")) return false;

        if (strings.length != 1) {
            commandNoName(((Player) commandSender).getPlayer());
            return false;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            ReplayGUIPlayer replayGUIPlayer = new ReplayGUIPlayer(mySQL);
            OfflinePlayer op = Bukkit.getOfflinePlayer(strings[0]);
            String uuid = op.getUniqueId().toString();
            replayGUIPlayer.guiOpen(player, uuid, true, false, false, "1", 1);
        });

        return false;
    }

    private void commandNoName(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            ReplayGUI replayGUI = new ReplayGUI();
            replayGUI.guiOpen(player);
        });
    }

}
