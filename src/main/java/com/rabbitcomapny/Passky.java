package com.rabbitcomapny;
import com.rabbitcomapny.commands.Changepass;
import com.rabbitcomapny.commands.Login;
import com.rabbitcomapny.commands.Register;
import com.rabbitcomapny.listeners.*;
import com.rabbitcomapny.utils.Utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Passky extends JavaPlugin {

    private static Passky instance;

    public static HashMap<Player, Boolean> isLoggedIn = new HashMap<>();
    public static HashMap<Player, Integer> failures = new HashMap<>();
    public static HashMap<Player, Double> damage = new HashMap<>();

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

        mkdir();
        loadYamls();

        Bukkit.getConsoleSender().sendMessage(Utils.chat("&7[&aPassky&7] &aPlugin is enabled!"));

        getCommand("login").setExecutor(new Login());
        getCommand("register").setExecutor(new Register());
        getCommand("changepassword").setExecutor(new Changepass());

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

    public void onDisable() { Bukkit.getConsoleSender().sendMessage(Utils.chat("&7[&aPassky&7] &cPlugin is disabled!")); }

    private void mkdir() {
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

    private void loadYamls() {
        try {
            this.conf.load(this.c);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

        try {
            this.pass.load(this.p);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

        try {
            this.mess.load(this.m);
        } catch (IOException | InvalidConfigurationException e) {
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

    public static Passky getInstance() { return instance; }
}
