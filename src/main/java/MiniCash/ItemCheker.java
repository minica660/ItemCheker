package MiniCash;

import MiniCash.api.ItemChekerAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemCheker extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        this.configManager = new ConfigManager(getConfig(),getLogger());

        configManager.load();

        ItemChekerAPI.init(this,configManager);
        getServer().getPluginManager().registerEvents(new Event(), this);

        registerCommand("ilock",new LockCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
