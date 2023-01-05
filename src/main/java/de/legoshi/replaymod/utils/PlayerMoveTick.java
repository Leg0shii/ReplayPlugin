package de.legoshi.replaymod.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PlayerMoveTick {

    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;

    private boolean sneak;
    private boolean onGround;
    private boolean prac;

    private String world;

    public PlayerMoveTick() {

    }

    public PlayerMoveTick(String string) {
        String[] args = string.split(";");
        this.x = Double.parseDouble(args[0]);
        this.y = Double.parseDouble(args[1]);
        this.z = Double.parseDouble(args[2]);

        this.yaw = Float.parseFloat(args[3]);
        this.pitch = Float.parseFloat(args[4]);

        this.sneak = args[5].equals("1");
        this.onGround = args[6].equals("1");
        this.prac = args[7].equals("1");
        this.world = args[8];
    }

    @Override
    public String toString() {
        String s = (Math.floor(x * 1000) / 1000) + ";" + (Math.floor(y * 1000) / 1000) + ";" + (Math.floor(z * 1000) / 1000) + ";"
                + (Math.floor(yaw * 1000) / 1000) + ";" + (Math.floor(pitch * 1000) / 1000) + ";"
                + (sneak ? 1 : 0) + ";" + (onGround ? 1 : 0) + ";" + (prac ? 1 : 0) + ";" + world;
        return s;
    }

    @Override
    public PlayerMoveTick clone() {
        PlayerMoveTick playerMoveTick = new PlayerMoveTick();
        playerMoveTick.x = this.x;
        playerMoveTick.y = this.y;
        playerMoveTick.z = this.z;
        playerMoveTick.yaw = this.yaw;
        playerMoveTick.pitch = this.pitch;
        playerMoveTick.onGround = this.onGround;
        playerMoveTick.sneak = this.sneak;
        playerMoveTick.prac = this.prac;
        playerMoveTick.world = this.world;
        return playerMoveTick;
    }

}
