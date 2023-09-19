package de.legoshi.replaymod;

import de.legoshi.replaymod.utils.PlayerMoveTick;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Getter
public class PlayerObject {

    private final Player player;
    private final ArrayList<PlayerMoveTick> lastPlayerPositions = new ArrayList<>();

    @Setter private boolean flyRecording;
    @Setter private double currentYVel;
    @Setter private double prevYVel;
    @Setter private int yVelCount;

    public PlayerObject(Player player) {
        this.player = player;
        this.currentYVel = 0;
        this.prevYVel = 0;
        this.yVelCount = 0;
        this.flyRecording = false;
    }

    public void addPlayerMoveTick(PlayerMoveTick playerMoveTick) {
        int recTime = 200;
        if (lastPlayerPositions.size() > recTime) {
            lastPlayerPositions.remove(recTime);
        }
        lastPlayerPositions.add(0, playerMoveTick);
    }

    public void updatePlayerVel(double yVel) {
        prevYVel = currentYVel;
        currentYVel = yVel;
    }

}
