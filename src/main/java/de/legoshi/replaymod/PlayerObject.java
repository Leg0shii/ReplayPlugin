package de.legoshi.replaymod;

import de.legoshi.replaymod.utils.PlayerMoveTick;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Getter
public class PlayerObject {

    private final Player player;
    private final ArrayList<PlayerMoveTick> lastPlayerPositions = new ArrayList<>();

    @Setter private boolean flyRecording;
    @Setter private double currentYVel;
    @Setter private double prevYVel;
    @Setter private double prevY;
    @Setter private double currentY;
    @Setter private int yVelCount;
    @Setter private int ladderCount;

    public PlayerObject(Player player) {
        this.player = player;
        this.currentYVel = 0;
        this.prevYVel = 0;
        this.prevY = 0;
        this.currentY = 0;
        this.yVelCount = 0;
        this.flyRecording = false;
        this.ladderCount = 0;
    }

    public void addPlayerMoveTick(PlayerMoveTick playerMoveTick) {
        int maxRec = Math.max(Main.getInstance().joinRecTime, Main.getInstance().leaveRecTime);
        int recTime = (20*maxRec) - 1;
        if (lastPlayerPositions.size() > recTime) {
            lastPlayerPositions.remove(recTime);
        }
        lastPlayerPositions.add(0, playerMoveTick);
    }

    public void updatePlayerVel(double yVel) {
        prevYVel = currentYVel;
        currentYVel = yVel;
    }

    public void updatePlayerY(double y) {
        prevY = currentY;
        currentY = y;
    }

}
