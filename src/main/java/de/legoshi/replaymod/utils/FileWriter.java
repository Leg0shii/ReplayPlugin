package de.legoshi.replaymod.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class FileWriter {

    private File f;
    private YamlConfiguration c;

    public FileWriter(String FilePath, String FileName) {
        this.f = new File(FilePath, FileName);
        this.c = YamlConfiguration.loadConfiguration(this.f);
    }

    public FileWriter setValue(String ValuePath, Object Value) {
        c.set(ValuePath, Value);
        return this;
    }

    public boolean exist() {
        return f.exists();
    }

    public int getInt(String ValuePath) {
        return c.getInt(ValuePath);
    }

    public String getString(String ValuePath) {
        return c.getString(ValuePath);
    }

    public boolean getBoolean(String ValuePath) {
        return c.getBoolean(ValuePath);
    }

    public long getLong(String ValuePath) {
        return c.getLong(ValuePath);
    }

    public List<String> getStringList(String ValuePath) {
        return c.getStringList(ValuePath);
    }

    public Set<String> getKeys(boolean deep) {
        return c.getKeys(deep);
    }

    public ConfigurationSection getConfigurationSection(String Section) {
        return c.getConfigurationSection(Section);
    }

    public FileWriter save() {
        try { this.c.save(this.f); }
        catch (IOException ignored) {}

        return this;
    }

}
