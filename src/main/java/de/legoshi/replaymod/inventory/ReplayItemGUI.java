package de.legoshi.replaymod.inventory;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.utils.ChatHelper;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReplayItemGUI {

    private String[] guiSetup = {
            " a  b  c ",
    };

    public void guiOpen(Player player, int clipID) {
        InventoryGui gui = new InventoryGui(Main.getInstance(), player, "Replay Menu", guiSetup);
        ItemStack saveReplay = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemStack deleteReplay = new ItemStack(Material.REDSTONE_BLOCK, 1);
        ItemStack deleteAllReplay = new ItemStack(Material.BARRIER, 1);

        StaticGuiElement saveRP = new StaticGuiElement('a', saveReplay, click -> {
            Main.getInstance().mySQL.update("UPDATE playerclip SET reviewed = true, saved = true WHERE clipid = " + clipID + ";");
            player.sendMessage(ChatHelper.PREFIX_SUCC + "Successfully saved replay clip.");
            player.closeInventory();
            return true;
        }, "&a&lSave Replay");

        StaticGuiElement deleteRP = new StaticGuiElement('b', deleteReplay, click -> {
            Main.getInstance().mySQL.update("DELETE FROM playerclip WHERE clipid = " + clipID + ";");
            player.sendMessage(ChatHelper.PREFIX_SUCC + "Successfully deleted replay clip.");
            player.closeInventory();
            return true;
        }, "&c&lDelete Replay");

        StaticGuiElement deleteAllRP = new StaticGuiElement('c', deleteAllReplay, click -> {
            ResultSet resultSet = Main.getInstance().mySQL.query("SELECT userid FROM playerclip WHERE clipid = " + clipID + ";");
            try {
                if (resultSet.next()) {
                    String playerUUID = resultSet.getString("userid");
                    Main.getInstance().mySQL.update("DELETE FROM playerclip WHERE userid = '" + playerUUID + "';");
                    player.closeInventory();
                    player.sendMessage(ChatHelper.PREFIX_SUCC + "Successfully deleted all replay clips from player.");
                } else player.sendMessage(ChatHelper.PREFIX_ERR + "No entry found...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }, "&c&lDelete ALL");

        gui.addElements(saveRP, deleteRP, deleteAllRP);

        player.closeInventory();
        gui.show(player);
    }
}
