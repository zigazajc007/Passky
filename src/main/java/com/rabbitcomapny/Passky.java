package com.rabbitcomapny;

import com.rabbitcomapny.commands.*;
import com.rabbitcomapny.listeners.*;
import com.rabbitcomapny.utils.Session;
import com.rabbitcomapny.utils.Utils;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public final class Passky extends JavaPlugin {

	//Database
	public static HikariDataSource hikari = null;
	public static HashMap<UUID, Boolean> isLoggedIn = new HashMap<>();
	public static HashMap<UUID, Integer> failures = new HashMap<>();
	public static HashMap<String, Session> session = new HashMap<>();
	public static String new_version = null;
	private static Passky instance;
	private static Connection conn = null;
	private static org.apache.logging.log4j.core.Filter passwordFilter;
	private final YamlConfiguration conf = new YamlConfiguration();
	private final YamlConfiguration mess = new YamlConfiguration();
	private final YamlConfiguration pass = new YamlConfiguration();
	private File c = null;
	private File m = null;
	private File p = null;

	public static Passky getInstance() {
		return instance;
	}

	public void onDisable() {
		info("&4Disabling");
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException ignored) {
			}
		}
	}

	public void onEnable() {
		instance = this;

		this.c = new File(getDataFolder(), "config.yml");
		this.m = new File(getDataFolder(), "messages.yml");
		this.p = new File(getDataFolder(), "passwords.yml");

		mkdirAndLoad(c, conf);
		mkdirAndLoad(m, mess);
		mkdirAndLoad(p, pass);

		//bStats
		Metrics metrics = new Metrics(this, 18975);
		metrics.addCustomChart(new Metrics.SimplePie("mysql", () -> getConf().getString("mysql", "false")));

		//Updater
		new UpdateChecker(this, 71835).getVersion(updater_version -> {
			if (!getDescription().getVersion().equalsIgnoreCase(updater_version)) {
				new_version = updater_version;
			}
			info("&aEnabling");
		});

		if (conf.getBoolean("hide_password", true)) {
			setupPasswordFilter();
		}

		//Database connection
		if (getConf().getBoolean("mysql", false)) {
			setupMySQL();
		}

		getCommand("login").setExecutor(new Login());
		getCommand("logout").setExecutor(new Logout());
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
		new PlayerLeaveListener(this);
		new PlayerHungerListener(this);
		new PlayerInteractListener(this);
		new PlayerAttackListener(this);
	}

	public void setupPasswordFilter() {
		//Load main command from plugins
		YamlConfiguration pluginYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(getResource("plugin.yml")), StandardCharsets.UTF_8));
		ConfigurationSection section = pluginYaml.getConfigurationSection("commands");
		final Set<String> commandHeads = new LinkedHashSet<>();
		String pluginName = pluginYaml.getString("name");

		if (section != null) {
			for (String key : section.getKeys(false)) {
				//Handle "plugin:command" case
				commandHeads.add(pluginName + ':' + key);
				commandHeads.add(key);
				for (String aliases : section.getStringList(key + ".aliases")) {
					commandHeads.add(pluginName + ':' + aliases);
					commandHeads.add(aliases);
				}
			}
		}

		//make sure filter init only once
		if (passwordFilter == null) {
			passwordFilter = new org.apache.logging.log4j.core.Filter() {
				@Override
				public Result getOnMismatch() {
					return Result.NEUTRAL;
				}

				@Override
				public Result getOnMatch() {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object... objects) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, String s, Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, Object o9) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Object o, Throwable throwable) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(org.apache.logging.log4j.core.Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
					return Result.NEUTRAL;
				}

				@Override
				public Result filter(LogEvent logEvent) {
					if (logEvent.getLevel() == Level.INFO) {
						String[] commandRecord = logEvent.getMessage().getFormattedMessage().split("issued server command: ", 2);
						if (commandRecord.length == 2) {
							//Remove '/'
							String command = commandRecord[1].substring(1);
							//Remove space, get the main command
							command = command.split(" ", 2)[0];
							for (String commandHead : commandHeads) {
								if (command.equalsIgnoreCase(commandHead)) {
									return Result.DENY;
								}
							}
						}
					}
					return Result.NEUTRAL;
				}

				@Override
				public State getState() {
					return isStarted() ? State.STARTED : State.STOPPED;
				}

				@Override
				public void initialize() {

				}

				@Override
				public void start() {

				}

				@Override
				public void stop() {

				}

				@Override
				public boolean isStarted() {
					return Passky.getInstance().isEnabled();
				}

				@Override
				public boolean isStopped() {
					return !Passky.getInstance().isEnabled();
				}
			};
			((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(passwordFilter);
		}
	}

	private void mkdirAndLoad(File file, YamlConfiguration conf) {
		if (!file.exists()) {
			saveResource(file.getName(), false);
		}

		try {
			conf.load(file);
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
	}

	private void setupMySQL() {
		try {
			hikari = new HikariDataSource();
			hikari.setMaximumPoolSize(10);
			hikari.setJdbcUrl("jdbc:mysql://" + getConf().getString("mysql_host") + ":" + getConf().getString("mysql_port") + "/" + getConf().getString("mysql_database"));
			hikari.setUsername(getConf().getString("mysql_user"));
			hikari.setPassword(getConf().getString("mysql_password"));
			hikari.addDataSourceProperty("useSSL", getConf().getString("mysql_useSSL", "false"));
			hikari.addDataSourceProperty("cachePrepStmts", "true");
			hikari.addDataSourceProperty("prepStmtCacheSize", "250");
			hikari.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			hikari.setConnectionTimeout(10000);

			conn = hikari.getConnection();
			conn.createStatement().execute("CREATE TABLE IF NOT EXISTS passky_players(uuid CHAR(36) NOT NULL PRIMARY KEY, algo CHAR(20) NOT NULL, hash CHAR(200) NOT NULL, salt CHAR(200) NOT NULL, ip CHAR(39), date CHAR(20) NOT NULL)");
			conn.close();
		} catch (SQLException ignored) {
			conn = null;
		}
	}

	private void info(String message) {
		String text = "\n\n";
		text += "&8[]============[" + message + " &cPassky&8]============[]\n";
		text += "&8|\n";
		text += "&8| &cInformation:\n";
		text += "&8|\n";
		text += "&8|   &9Name: &bPassky\n";
		text += "&8|   &9Developer: &bBlack1_TV\n";
		if (new_version != null) {
			text += "&8|   &9Version: &b" + getDescription().getVersion() + " (&6update available&b)\n";
		} else {
			text += "&8|   &9Version: &b" + getDescription().getVersion() + "\n";
		}
		text += "&8|   &9Website: &bhttps://rabbit-company.com\n";
		text += "&8|\n";
		text += "&8| &cSponsors:\n";
		text += "&8|\n";
		text += "&8|   &9- &6https://rabbitserverlist.com\n";
		text += "&8|\n";
		text += "&8| &cSupport:\n";
		text += "&8|\n";
		text += "&8|   &9Discord: &bziga.zajc007\n";
		text += "&8|   &9Mail: &bziga.zajc007@gmail.com\n";
		text += "&8|   &9Discord: &bhttps://discord.gg/hUNymXX\n";
		text += "&8|\n";
		text += "&8[]=========================================[]\n";

		Bukkit.getConsoleSender().sendMessage(Utils.chat(text));
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
}
