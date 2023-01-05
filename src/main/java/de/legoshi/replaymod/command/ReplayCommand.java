package de.legoshi.replaymod.command;

import com.comphenix.protocol.PacketType;
import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.replay.Replay;
import de.legoshi.replaymod.utils.ChatHelper;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ReplayCommand implements CommandExecutor {

    private final Replay replay;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Main.running) {
            commandSender.sendMessage("Plugin is currently not running. (Database?)");
            return false;
        }

        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (!player.hasPermission("replay")) return false;

        try {
            int i = Integer.parseInt(strings[0]);
            replay.playReplay(player, i);
        } catch (NumberFormatException nf) {
            commandSender.sendMessage(ChatHelper.PREFIX_ERR + "Please enter a valid clip id.");
            return false;
        }

        return false;
    }

}
