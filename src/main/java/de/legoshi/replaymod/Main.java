package de.legoshi.replaymod;

import de.legoshi.replaymod.command.ReplayGUICommand;
import de.legoshi.replaymod.database.AsyncMySQL;
import de.legoshi.replaymod.database.DBManager;
import de.legoshi.replaymod.listener.JoinListener;
import de.legoshi.replaymod.listener.MovementListener;
import de.legoshi.replaymod.listener.QuitListener;
import de.legoshi.replaymod.replay.Replay;
import de.legoshi.replaymod.utils.CheatDetection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static boolean running;

    private PlayerManager playerManager;
    private DBManager dbManager;
    private CheatDetection cheatDetection;

    public AsyncMySQL mySQL;
    public Replay replay;

    @Override
    public void onEnable() {
        instance = this;
        running = false;

        this.dbManager = new DBManager();
        this.mySQL = dbManager.initializeTables();
        this.replay = new Replay(mySQL);

        this.playerManager = new PlayerManager();
        this.cheatDetection = new CheatDetection(playerManager, dbManager);

        registerEvents();
        registerCommands();

        deleteOldClips();
    }

    @Override
    public void onDisable() {
        if (!Main.running) return;
        for (Player all : Bukkit.getOnlinePlayers()) {
            // save everything that is running right now
        }
    }

    private void registerEvents() {
        PluginManager pM = Bukkit.getPluginManager();
        pM.registerEvents(new JoinListener(playerManager), this);
        pM.registerEvents(new QuitListener(playerManager), this);
        pM.registerEvents(new MovementListener(playerManager, cheatDetection), this);
    }

    private void registerCommands() {
        getCommand("replay").setExecutor(new ReplayGUICommand(mySQL));
    }

    private void deleteOldClips() {
        long currentTime = System.currentTimeMillis() - 604800000L;
        mySQL.update("DELETE FROM playerclip WHERE date < " + currentTime + " AND saved = false;");

        currentTime = System.currentTimeMillis() - (604800000/7);
        mySQL.update("DELETE FROM playerclip WHERE date < " + currentTime + " AND saved = false AND reviewed = true;");
    }

    public static Main getInstance() {
        return instance;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning() {
        running = true;
    }

}
