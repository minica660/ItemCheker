package MiniCash;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ConfigManager {

    private final FileConfiguration config;
    private final Logger logger;

    private final Set<Material> blockMaterials = new HashSet<>();
    private boolean enchanted = false;
    private boolean attributes = false;
    private boolean unbreakble = false;

    public ConfigManager(FileConfiguration config, Logger logger){
        this.config = config;
        this.logger = logger;
    }


    public void load(){

        List<String> blockNames = config.getStringList("block-list");

        for (String name : blockNames) {

            Material material = Material.matchMaterial(name);

            if (material != null && material.isBlock()) {

                blockMaterials.add(material);

                logger.info("読み込んだブロック: " + material.name());



            } else {
                logger.warning("無効なブロック名: " + name);
            }
        }


        this.enchanted = config.getBoolean("enchanted", false);
        this.attributes = config.getBoolean("attributes", false);
        this.unbreakble = config.getBoolean("unbreakble", false);


    }


    public Set<Material> blockMaterials(){
        return blockMaterials;
    }

    public boolean isEnchanted(){
        return enchanted;
    }

    public boolean isAttributes(){
        return attributes;
    }

    public boolean isUnbreakble(){
        return unbreakble;
    }
}
