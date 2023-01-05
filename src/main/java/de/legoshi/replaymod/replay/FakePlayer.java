package de.legoshi.replaymod.replay;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.legoshi.replaymod.utils.ChatHelper;
import de.legoshi.replaymod.utils.HTTPHelper;
import de.legoshi.replaymod.utils.PlayerMoveTick;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FakePlayer extends Reflections {

    public String name;

    private int entityID;
    private GameProfile gameProfile;
    private PlayerMoveTick playerMoveTick;
    @Getter
    private EntityPlayer npc;

    public String prevWorld;
    public double prevMoveX;
    public double prevMoveY;
    public double prevMoveZ;

    public FakePlayer(Player player, String worldName, String name, PlayerMoveTick playerMoveTick) {
        this.name = name;

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorld(worldName)).getHandle();
        this.gameProfile = new GameProfile(UUID.randomUUID(), name);
        this.npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        npc.setLocation(playerMoveTick.getX(), playerMoveTick.getY(), playerMoveTick.getZ(), playerMoveTick.getYaw(), playerMoveTick.getPitch());
        this.entityID = npc.getId();

        changeSkin(player, name);
        this.prevMoveX = playerMoveTick.getX();
        this.prevMoveY = playerMoveTick.getY();
        this.prevMoveZ = playerMoveTick.getZ();
        this.prevWorld = playerMoveTick.getWorld();
        this.playerMoveTick = playerMoveTick.clone();
    }

    public void changeSkin(Player player, String name) {
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            String uuid = op.getUniqueId().toString();
            String[] vals = HTTPHelper.getStrings(uuid);
            gameProfile.getProperties().put("textures", new Property("textures", vals[0], vals[1]));
        } catch (Exception e) {
            player.sendMessage(ChatHelper.PREFIX_ERR + "Couldn't fetch the player skin.");
        }
    }

    public void spawn(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
    }

    public void move(Player player, PlayerMoveTick playerMoveTick) {
        teleport(player, playerMoveTick);
        sneak(player, playerMoveTick);
        prac(player, playerMoveTick);
    }

    public void teleport(Player player, PlayerMoveTick playerMoveTick) {
        int moveX = getFixLocation(this.playerMoveTick.getX());
        int moveY = getFixLocation(this.playerMoveTick.getY());
        int moveZ = getFixLocation(this.playerMoveTick.getZ());
        float yaw = playerMoveTick.getYaw();
        float pitch  = playerMoveTick.getPitch();
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(
                entityID,
                moveX,
                moveY,
                moveZ,
                getFixRotation(yaw),
                getFixRotation(pitch),
                playerMoveTick.isOnGround());
        sendPacket(packet, player);
        headRotation(yaw, pitch, player);
        this.prevMoveX = playerMoveTick.getX();
        this.prevMoveY = playerMoveTick.getY();
        this.prevMoveZ = playerMoveTick.getZ();
        this.prevWorld = playerMoveTick.getWorld();
        this.playerMoveTick = playerMoveTick.clone();
    }

    public void sneak(Player player, PlayerMoveTick playerMoveTick) {
        DataWatcher rawDataWatcher = npc.getDataWatcher();
        rawDataWatcher.watch(0, playerMoveTick.isSneak() ? (byte) 2 : (byte) 0);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityID, rawDataWatcher, false);
        sendPacket(metadataPacket, player);
    }

    public void prac(Player player, PlayerMoveTick playerMoveTick) {
        PacketPlayOutEntityEquipment packet;
        if (playerMoveTick.isPrac()) packet = new PacketPlayOutEntityEquipment(entityID, (short) 4, CraftItemStack.asNMSCopy(new ItemStack(Material.DIAMOND_HELMET)));
        else  packet = new PacketPlayOutEntityEquipment(entityID, (short) 4, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
        sendPacket(packet, player);
    }

    public void headRotation(float yaw, float pitch, Player player) {
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));

        sendPacket(packet, player);
        sendPacket(packetHead, player);
    }

    public void destroy(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
        connection.sendPacket(new PacketPlayOutEntityDestroy(entityID));
    }

    public int getFixLocation(double pos) {
        return MathHelper.floor(pos * 32.0D);
    }

    public byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

}
