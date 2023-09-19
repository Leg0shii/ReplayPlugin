package de.legoshi.replaymod.inventory;

import de.legoshi.replaymod.Main;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReplayGUI {

    private final String[] guiSetup = {
            "         ",
            "  a   b  ",
            "         ",
    };

    public void guiOpen(Player player) {
        InventoryGui gui = new InventoryGui(Main.getInstance(), player, "Replay Menu", guiSetup);
        ItemStack newReplays = new ItemStack(Material.INK_SACK, 1, (short) 10);
        ItemStack flyReplays = new ItemStack(Material.REDSTONE);
        
        StaticGuiElement newRP = new StaticGuiElement('a', newReplays, click -> {
            ReplayGUIPlayer replayGUIPlayer = new ReplayGUIPlayer(Main.getInstance().mySQL);
            replayGUIPlayer.guiOpen(player, "", false, 1);
            return true;
        }, "&a&lNew Replays");

        StaticGuiElement savedRP = new StaticGuiElement('b', flyReplays, click -> {
            ReplayGUIPlayer replayGUIPlayer = new ReplayGUIPlayer(Main.getInstance().mySQL);
            replayGUIPlayer.guiOpen(player, "", true, 1);
            return true;
        }, "&c&lSaved Replays");

        gui.addElements(newRP, savedRP);
        gui.show(player);
    }

}
