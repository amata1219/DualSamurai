package dual.samurai;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomConfig {

	private FileConfiguration config = null;
	private final File configFile;
	private final String file;
	private final DualSamurai plugin;

	public CustomConfig(DualSamurai plugin){
		this(plugin, "config.yml");
	}

	public CustomConfig(DualSamurai plugin, String fileName){
		this.plugin = plugin;
		this.file = fileName;
		configFile = new File(plugin.getDataFolder(), file);
	}

	public void saveDefaultConfig(){
		if(!configFile.exists()){
			plugin.saveResource(file, false);
		}
	}

	public void updateConfig(){
		saveConfig();
		reloadConfig();
	}

	public void reloadConfig(){
		config = YamlConfiguration.loadConfiguration(configFile);
		final InputStream defConfigStream = plugin.getResource(file);
		if(defConfigStream == null){
			return;
		}
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
	}

	public FileConfiguration getConfig(){
		if(config == null){
			reloadConfig();
		}
		return config;
	}

	public void saveConfig(){
		if(config == null){
			return;
		}
		try{
			getConfig().save(configFile);
		}catch(IOException e){
			return;
		}
	}
}
