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
            mySQL.update("CREATE TABLE IF NOT EXISTS playerclip (clipid INT NOT NULL AUTO_INCREMENT, userid VARCHAR(255), world VARCHAR(255), reviewed BOOL, saved BOOL, date BigInt, clip BLOB, PRIMARY KEY(clipid));");
            Main.setRunning();
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
     */
    public void saveCurrentClip(PlayerObject playerObject) {
        StringBuilder values = new StringBuilder();
        for (PlayerMoveTick playerMoveTick : playerObject.getLastPlayerPositions()) {
            String moveString = playerMoveTick.toString();
            if (values.toString().equals("")) values = new StringBuilder(moveString);
            else values.append(":").append(moveString);
        }

        String world = playerObject.getPlayer().getWorld().getName();
        String uuid = playerObject.getPlayer().getUniqueId().toString();
        long time = System.currentTimeMillis();

        mySQL.update("INSERT INTO playerclip (userid, world, date, clip, reviewed, saved) VALUES " +
                "('" + uuid + "', '" + world + "', " + time + "', '" + values + "', " + false + ", " + false + ");");
    }

}
