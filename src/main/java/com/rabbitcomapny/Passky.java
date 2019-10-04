package com.rabbitcomapny;

import com.rabbitcomapny.commands.Login;
import com.rabbitcomapny.commands.Register;
import com.rabbitcomapny.listeners.*;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public final class Passky extends JavaPlugin {

    private static Passky instance;
    public static HashMap<Player, Boolean> isLoggedIn = new HashMap<>();

    private File c = null;
    private YamlConfiguration conf = new YamlConfiguration();

    private File m = null;
    private YamlConfiguration mess = new YamlConfiguration();

    private File p = null;
    private YamlConfiguration pass = new YamlConfiguration();

    @Override
    public void onEnable() {
        instance = this;

        this.c = new File(getDataFolder(), "config.yml");
        this.m = new File(getDataFolder(), "messages.yml");
        this.p = new File(getDataFolder(), "passwords.yml");

        mkdir();
        loadYamls();

        Bukkit.getConsoleSender().sendMessage(Utils.chat("&7[&aPassky&7] &aPlugin is enabled!"));

        //Commands
        this.getCommand("login").setExecutor((CommandExecutor) new Login());
        this.getCommand("register").setExecutor((CommandExecutor) new Register());

        //Listeners
        new PlayerJoinListener(this);
        new PlayerMoveListener(this);
        new PlayerDropItemListener(this);
        new InventoryOpenListener(this);
        new PlayerChatListener(this);
        new PlayerCommandListener(this);
        new InventoryClickListener(this);
        new PlayerDamageListener(this);
        new BlockPlaceListener(this);
        new BlockBreakListener(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Utils.chat("&7[&aPassky&7] &cPlugin is disabled!"));
    }

    private void mkdir(){
        if (!this.c.exists()) {
            saveResource("config.yml", false);
        }

        if (!this.p.exists()) {
            saveResource("passwords.yml", false);
        }

        if (!this.m.exists()) {
            saveResource("messages.yml", false);
        }
    }

    private void loadYamls(){

        try {
            this.conf.load(this.c);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.pass.load(this.p);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            this.mess.load(this.m);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConf() { return this.conf; }
    public YamlConfiguration getMess() { return this.mess; }
    public YamlConfiguration getPass() { return this.pass; }

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

    public static Passky getInstance(){
        return instance;
    }
}
