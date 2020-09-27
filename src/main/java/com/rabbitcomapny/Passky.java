package com.rabbitcomapny;

import com.rabbitcomapny.commands.*;
import com.rabbitcomapny.listeners.*;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public final class Passky extends JavaPlugin {

    private static Passky instance;

    public static HashMap<UUID, Boolean> isLoggedIn = new HashMap<>();
    public static HashMap<UUID, Integer> failures = new HashMap<>();
    public static HashMap<UUID, Double> damage = new HashMap<>();

    private File c = null;
    private final YamlConfiguration conf = new YamlConfiguration();

    private File m = null;
    private final YamlConfiguration mess = new YamlConfiguration();

    private File p = null;
    private final YamlConfiguration pass = new YamlConfiguration();


    public void onEnable() {
        instance = this;

        this.c = new File(getDataFolder(), "config.yml");
        this.m = new File(getDataFolder(), "messages.yml");
        this.p = new File(getDataFolder(), "passwords.yml");

        mkdirAndLoad(c, conf);
        mkdirAndLoad(m, mess);
        mkdirAndLoad(p, pass);

        Bukkit.getConsoleSender().sendMessage(Utils.chat("&7[&aPassky&7] &aPlugin is enabled!"));

        getCommand("login").setExecutor(new Login());
        getCommand("register").setExecutor(new Register());
        getCommand("changepassword").setExecutor(new Changepass());
        getCommand("forcechangepassword").setExecutor(new ForceChangePassword());
        getCommand("forceregister").setExecutor(new ForceRegister());

        new PlayerJoinListener(this);
        new PlayerMoveListener(this);
        new PlayerDropItemListener(this);
        new PlayerPickUpItemListener(this);
        new InventoryOpenListener(this);
        new PlayerChatListener(this);
        new PlayerCommandListener(this);
        new InventoryClickListener(this);
        new PlayerDamageListener(this);
        new BlockPlaceListener(this);
        new BlockBreakListener(this);
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Utils.chat("&7[&aPassky&7] &cPlugin is disabled!"));
    }

    private void mkdirAndLoad(File file, YamlConfiguration conf) {
        if (!file.exists()) {
            saveResource(file.getName(), false);
        } else {
            Utils.mergeYaml(file.getName(), file);
        }
        try {
            conf.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }


    public YamlConfiguration getConf() {
        return this.conf;
    }

    public YamlConfiguration getMess() {
        return this.mess;
    }

    public YamlConfiguration getPass() {
        return this.pass;
    }

    public void saveConf() {
        try {
            this.conf.save(this.c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMess() {
        try {
            this.mess.save(this.m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePass() {
        try {
            this.pass.save(this.p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Passky getInstance() { return instance; }
}
