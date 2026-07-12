package MiniCash;

import MiniCash.api.ItemChekerAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemCheker extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        ItemChekerAPI.init(this);
        getServer().getPluginManager().registerEvents(new Event(), this);

        registerCommand("ilock",new LockCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
