package de.legoshi.replaymod.command;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.replay.Replay;
import de.legoshi.replaymod.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EndReplayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (!player.hasPermission("replay")) return false;

        Replay.endRP.add(player.getUniqueId().toString());

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            Replay.endRP.remove(player.getUniqueId().toString());
        }, 5L);

        return false;
    }
}
