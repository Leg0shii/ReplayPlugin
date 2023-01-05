package de.legoshi.replaymod.database;
import de.legoshi.replaymod.Main;
import de.legoshi.replaymod.PlayerObject;
import de.legoshi.replaymod.utils.FileWriter;
import de.legoshi.replaymod.utils.PlayerMoveTick;
import org.bukkit.Bukkit;

import java.sql.SQLException;

public class DBManager {

    public AsyncMySQL mySQL;

    public AsyncMySQL initializeTables() {
        this.mySQL = connectToDB();
        if(mySQL != null) {
            Bukkit.getConsoleSender().sendMessage("DB connected.");
            mySQL.update("CREATE TABLE IF NOT EXISTS playerclip (clipid INT NOT NULL AUTO_INCREMENT, playerUUID VARCHAR(255), world VARCHAR(255), playerjoin BOOL, reviewed BOOL, saved BOOL, publicclip BOOL, date BigInt, clip BLOB, PRIMARY KEY(clipid));");
            Main.running = true;
        } else {
            Bukkit.getConsoleSender().sendMessage("DB couldn't be connected.");
        }
        return mySQL;
    }

    public AsyncMySQL connectToDB() {
        Main instance = Main.getInstance();
        FileWriter config = new FileWriter("./plugins/ReplayConfiguration/", "replaydbconfig.yaml");

        String host = config.getString("host");
        int port = config.getInt("port");
        String username = config.getString("username");
        String password = config.getString("password");
        String database = config.getString("database");

        try {
            mySQL = new AsyncMySQL(instance, host, port, username, password, database);
            return mySQL;
        } catch (SQLException | ClassNotFoundException throwables) { throwables.printStackTrace(); }

        return null;
    }

    /**
     * Saves the clip to the database
     * @param playerObject
     * @param args 0: onQuit, 1: onJoin, 2: onIllegalMove
     */
    public void saveCurrentClip(PlayerObject playerObject, String args) {
        String values = "";
        String[] arg = args.split(";");
        boolean publicUpload = false;
        if (arg.length == 2 && arg[1].equals("true")) publicUpload = true;
        for (PlayerMoveTick playerMoveTick : playerObject.getLastPlayerPositions()) {
            String moveString = playerMoveTick.toString();
            if (values.equals("")) values = moveString;
            else values = values + ":" + moveString;
        }
        String uuid = playerObject.getPlayer().getUniqueId().toString();
        long time = System.currentTimeMillis();
        mySQL.update("INSERT INTO playerclip (playerUUID, world, playerjoin, date, clip, reviewed, saved, publicclip) " +
                "VALUES ('" + uuid + "', '" + playerObject.getPlayer().getWorld().getName() + "', " + arg[0] + ", '" + time + "', '" + values + "', " + false + ", " + false + ", " + publicUpload + ");");
    }

}
