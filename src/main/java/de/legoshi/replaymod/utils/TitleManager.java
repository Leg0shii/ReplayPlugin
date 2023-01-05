package de.legoshi.replaymod.utils;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleManager {
    @SuppressWarnings("rawtypes")
    public static void sendTitle(Player player, String msgTitle, String msgSubTitle, int ticks) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a((String)("{\"text\": \"" + msgTitle + "\"}"));
        IChatBaseComponent chatSubTitle = IChatBaseComponent.ChatSerializer.a((String)("{\"text\": \"" + msgSubTitle + "\"}"));
        PacketPlayOutTitle p = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle p2 = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(p);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((p2));
        TitleManager.sendTime(player, ticks);
    }

    @SuppressWarnings("rawtypes")
    private static void sendTime(Player player, int ticks) {
        PacketPlayOutTitle p = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, 20, ticks, 20);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((p));
    }

    @SuppressWarnings("rawtypes")
    public static void sendActionBar(Player player, String message) {
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a(("{\"text\": \"" + message + "\"}"));
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(ppoc);
    }
}
