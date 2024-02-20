package de.legoshi.replaymod.inventory;

import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.database.AsyncMySQL;
import de.legoshi.replaymod.utils.ChatHelper;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
public class ReplayGUIPlayer {

    private final AsyncMySQL mySQL;
    private final int pageVolume = 40;

    private String[] guiSetup = {
            "ggggggggk",
            "ggggggggh",
            "ggggggggb",
            "ggggggggh",
            "ggggggggf"
    };

    // search = playeruuid
    public void guiOpen(Player player, String playerID, boolean all, boolean viewed, boolean saved, String type, int page) {
        InventoryGui gui = new InventoryGui(Main.getInstance(), player, "Replays", guiSetup);

        GuiElementGroup group = new GuiElementGroup('g');
        gui.addElement(
                new StaticGuiElement('h',
                    new ItemStack(Material.STAINED_GLASS_PANE, 1),
                    click -> true, " "
                )
        );

        gui.addElements(new StaticGuiElement('b', new ItemStack(Material.REDSTONE), click -> {
            ReplayGUI replayGUI = new ReplayGUI();
            replayGUI.guiOpen(player);
            return true;
        }));

        player.closeInventory();

        String playerSearchString = "";
        String addString = "";
        if (!playerID.equals("")) playerSearchString = "playerUUID = '" + playerID + "' AND ";
        if (!all) addString = "AND reviewed = " + viewed + " AND saved = " + saved + " ";

        ResultSet resultSet = mySQL.query("SELECT clipid, playerUUID, date, reviewed, saved, publicclip " +
                    "FROM playerclip WHERE " + playerSearchString + "playerjoin = " + type + " " + addString + "AND publicclip = true " +
                    "ORDER BY date DESC LIMIT " + (pageVolume * (page - 1)) + ", 40;");

        try {
            if (resultSet != null && resultSet.next()) {
                resultSet.last();
                gui.addElement(upElement(playerID, all, viewed, saved, type, page-1));
                gui.addElement(downElement(playerID, all, viewed, saved, type, resultSet.getRow(),  page+1));

                resultSet.beforeFirst();
                while (resultSet.next()) group.addElement(mapElements(resultSet));

                gui.addElement(group);
                gui.show(player);
            } else {
                player.sendMessage(ChatHelper.PREFIX_ERR + "No data found for that player...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(ChatHelper.PREFIX_ERR + "Something went wrong selecting player replay data from database...");
        }
    }

    public StaticGuiElement mapElements(ResultSet resultSet) throws SQLException {
        long time = resultSet.getLong("date");
        int id = resultSet.getInt("clipid");
        String uuid = resultSet.getString("playerUUID");
        Date currentDate = new Date(time);

        String playerName = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        ItemStack playerHead = getPlayerSkull(playerName);

        String viewed = resultSet.getBoolean("reviewed") ? "&aYes" : "&cNo";
        String saved = resultSet.getBoolean("saved") ? "&aYes" : "&cNo";

        StaticGuiElement staticGuiElement;
        staticGuiElement = new StaticGuiElement('g', playerHead, click -> {
            Player player = (Player) click.getEvent().getWhoClicked();
            if (click.getEvent().isLeftClick()) {
                Main.getInstance().replay.playReplay(player, id);
                mySQL.update("UPDATE playerclip SET reviewed = true WHERE clipid = " + id + ";");
                player.closeInventory();
                player.sendMessage(ChatHelper.PREFIX_SUCC + "Successfully started replay with id: §f" + id);
            } else {
                ReplayItemGUI replayItemGUI = new ReplayItemGUI();
                replayItemGUI.guiOpen(player, id);
            }
            return true; // returning true will cancel the click event and stop taking the item
        },
                "" + ChatColor.GRAY + ChatColor.BOLD + playerName + ChatColor.WHITE + " (" + id + ")",
                "" + ChatColor.RESET + ChatColor.GRAY + "Date: " + ChatColor.GOLD + currentDate + "\n" +
                "" + ChatColor.RESET + ChatColor.GRAY + "Saved: " + saved + "\n" +
                "" + ChatColor.RESET + ChatColor.GRAY + "Viewed: " + viewed + "\n" +
                "" + ChatColor.RESET + ChatColor.DARK_GRAY + "Left Click - Start replay \n" +
                "" + ChatColor.RESET + ChatColor.DARK_GRAY + "Right Click - Open Menu");
        return staticGuiElement;
    }

    private StaticGuiElement downElement(String search, boolean all, boolean viewed, boolean saved, String type, int size, int page) {
        StaticGuiElement staticGuiElement;
        staticGuiElement = new StaticGuiElement('f', new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2), click -> {
            if (size == pageVolume) {
                Player player = (Player) click.getEvent().getWhoClicked();
                guiOpen(player, search, all, viewed, saved, type, page);
            }

            return true;
        }, "DOWN");
        return staticGuiElement;
    }

    private StaticGuiElement upElement(String search, boolean all, boolean viewed, boolean saved, String type, int page) {
        StaticGuiElement staticGuiElement;
        staticGuiElement = new StaticGuiElement('k', new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 2),
                click -> {
                    Player player = (Player) click.getEvent().getWhoClicked();
                    if (page > 0) guiOpen(player, search, all, viewed, saved, type, page);

                    return true;
                }
                , "UP");

        return staticGuiElement;
    }

    private ItemStack getPlayerSkull(String playerName) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        meta.setOwner(playerName);
        meta.setDisplayName("&a&l" + playerName);
        skull.setItemMeta(meta);
        return skull;
    }

}
