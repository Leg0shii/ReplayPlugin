package de.legoshi.replaymod.inventory;

import de.legoshi.replaymod.Main;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReplayGUI {

    private String[] guiSetup = {
            "         ",
            "  a b c  ",
            "    d    ",
    };

    public void guiOpen(Player player) {
        InventoryGui gui = new InventoryGui(Main.getInstance(), player, "Replay Menu", guiSetup);
        ItemStack newReplays = new ItemStack(Material.INK_SACK, 1, (short) 10);
        ItemStack oldReplays = new ItemStack(Material.INK_SACK, 1, (short) 8);
        ItemStack savedReplays = new ItemStack(Material.NETHER_STAR);
        ItemStack flyReplays = new ItemStack(Material.REDSTONE);

        StaticGuiElement newRP = new StaticGuiElement('a', newReplays, click -> {
            ReplayGUIPlayer replayGUIPlayer = new ReplayGUIPlayer(Main.getInstance().mySQL);
            replayGUIPlayer.guiOpen(player, "", false, false, false, "1",1);
            return true;
        }, "&a&lNew Replays");

        StaticGuiElement oldRP = new StaticGuiElement('b', oldReplays, click -> {
            ReplayGUIPlayer replayGUIPlayer = new ReplayGUIPlayer(Main.getInstance().mySQL);
            replayGUIPlayer.guiOpen(player, "", false, true, false, "1",1);
            return true;
        }, "&7&lViewed Replays");

        StaticGuiElement saveRP = new StaticGuiElement('c', savedReplays, click -> {
            ReplayGUIPlayer replayGUIPlayer = new ReplayGUIPlayer(Main.getInstance().mySQL);
            replayGUIPlayer.guiOpen(player, "", false, true, true, "1",1);
            return true;
        }, "&b&lSaved Replays");

        StaticGuiElement flyRP = new StaticGuiElement('d', flyReplays, click -> {
            ReplayGUIPlayer replayGUIPlayer = new ReplayGUIPlayer(Main.getInstance().mySQL);
            replayGUIPlayer.guiOpen(player, "", true, false, false, "2",1);
            return true;
        }, "&c&lFly Replays");

        gui.addElements(newRP, oldRP, saveRP, flyRP);

        player.closeInventory();
        gui.show(player);
    }

}
