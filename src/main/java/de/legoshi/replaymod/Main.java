package de.legoshi.replaymod;

import de.legoshi.replaymod.command.EndReplayCommand;
import de.legoshi.replaymod.command.ReplayCommand;
import de.legoshi.replaymod.command.ReplayGUICommand;
import de.legoshi.replaymod.command.SaveReplayCommand;
import de.legoshi.replaymod.database.AsyncMySQL;
import de.legoshi.replaymod.database.DBManager;
import de.legoshi.replaymod.listener.JoinListener;
import de.legoshi.replaymod.listener.MovementListener;
import de.legoshi.replaymod.listener.QuitListener;
import de.legoshi.replaymod.replay.Replay;
import de.legoshi.replaymod.utils.CheatDetection;
import de.legoshi.replaymod.utils.FileWriter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {

    public PlayerManager playerManager;
    public DBManager dbManager;
    private CheatDetection cheatDetection;

    public Replay replay;

    public AsyncMySQL mySQL;
    @Getter private static Main instance;

    public int joinRecTime;
    public int leaveRecTime;

    public static boolean running = false;

    @Override
    public void onEnable() {
        loadConfig();

        instance = this;
        this.dbManager = new DBManager();
        this.mySQL = dbManager.initializeTables();
        this.replay = new Replay(mySQL);

        this.playerManager = new PlayerManager(dbManager);
        this.cheatDetection = new CheatDetection(playerManager, dbManager);

        registerEvents();
        registerCommands();

        deleteOldClips();
    }

    @Override
    public void onDisable() {
        if (!Main.running) return;
        for (Player all : Bukkit.getOnlinePlayers()) {
            dbManager.mySQL.update("UPDATE playerclip SET publicclip = true WHERE publicclip = false AND playerUUID =  '" + all.getUniqueId().toString() + "';");
            PlayerObject playerObject = playerManager.playerHashMap.get(all);
            dbManager.saveCurrentClip(playerObject, "0");
        }
    }

    private void registerEvents() {
        PluginManager pM = Bukkit.getPluginManager();
        pM.registerEvents(new JoinListener(playerManager), this);
        pM.registerEvents(new QuitListener(playerManager), this);
        pM.registerEvents(new MovementListener(playerManager, cheatDetection), this);
    }

    private void registerCommands() {
        getCommand("rp").setExecutor(new ReplayCommand(replay));
        getCommand("rpgui").setExecutor(new ReplayGUICommand(mySQL));
        getCommand("rpsave").setExecutor(new SaveReplayCommand(dbManager, playerManager));
        getCommand("rpend").setExecutor(new EndReplayCommand());
    }

    private void loadConfig() {
        File dir = new File("./plugins/ReplayConfiguration/");
        if (!dir.exists()) dir.mkdir();

        File file = new File("./plugins/ReplayConfiguration/replayconfig.yaml");
        FileWriter replayConfig = new FileWriter("./plugins/ReplayConfiguration/", "replayconfig.yaml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                replayConfig.setValue("joinrecordingtime", 10);
                replayConfig.setValue("leaverecordingtime", 10);
                replayConfig.save();
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("Error with loading replayconfig....");
                return;
            }
        }
        joinRecTime = replayConfig.getInt("joinrecordingtime");
        leaveRecTime = replayConfig.getInt("leaverecordingtime");

        File fileDB = new File("./plugins/ReplayConfiguration/replaydbconfig.yaml");
        FileWriter replayConfigDB = new FileWriter("./plugins/ReplayConfiguration/", "replaydbconfig.yaml");
        if (!fileDB.exists()) {
            try {
                fileDB.createNewFile();
                replayConfigDB.setValue("host", "localhost");
                replayConfigDB.setValue("port", 3306);
                replayConfigDB.setValue("username", "root");
                replayConfigDB.setValue("password", "root");
                replayConfigDB.setValue("database", "replaytest");
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("Error with loading dbconfig....");
            }
            replayConfigDB.save();
        }
    }

    private void deleteOldClips() {
        long currentTime = System.currentTimeMillis() - 604800000L;
        mySQL.update("DELETE FROM playerclip WHERE date < " + currentTime + " AND saved = false;");

        currentTime = System.currentTimeMillis() - (604800000/7);
        mySQL.update("DELETE FROM playerclip WHERE date < " + currentTime + " AND saved = false AND reviewed = true;");
    }

}
