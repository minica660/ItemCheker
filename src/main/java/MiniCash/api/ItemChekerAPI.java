package MiniCash.api;

import MiniCash.ConfigManager;
import MiniCash.ItemCheker;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemChekerAPI {
    private static ItemCheker plugin;

    private static ConfigManager configManager;
    private static NamespacedKey namespacedKey;

    public ItemChekerAPI(ItemCheker plugin, ConfigManager configManager) {
        ItemChekerAPI.init(plugin,configManager);

    }

    public static void init(ItemCheker pluginInstance, ConfigManager configManager) {
        plugin = pluginInstance;
        ItemChekerAPI.configManager = configManager;
        namespacedKey = new NamespacedKey(plugin, "item_cheker_unprotected");
    }


    public static ApplyProtectionResult applyUnProtection(ItemStack item ) {

        if (item == null || item.getType().isAir()) {
            return new ApplyProtectionResult(item , false , "指定されたアイテムがなかったため保護キーを指定できませんでした");
        }




        if(configManager.blockMaterials().contains(item.getType())){
            return new ApplyProtectionResult(item, false , "404 Not Found");
        }


        if (isSpecialItem(item)){
            return new ApplyProtectionResult(item, false , "404 Not Found");
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {

            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BOOLEAN ,  true);

            item.setItemMeta(meta);

        }

        return new ApplyProtectionResult(item , true , "保護対象外キーを指定することが出来ました");
    }

    public static ApplyProtectionResult removeUnProtected(ItemStack item) {

        if (!isProtectionLocked(item)){
            return new ApplyProtectionResult(item,false, "保護が解除されていないアイテムです");
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().remove(namespacedKey);
            item.setItemMeta(meta);
            return new ApplyProtectionResult(item, true, "保護を行いました");
        }
        return new ApplyProtectionResult(item, false, "処理が行われませんでした");
    }

    private static boolean isSpecialItem(ItemStack item) {

        if (item == null || item.getType().isAir()) {
            return false;
        }

        if (!item.hasItemMeta()) {
            return true;
        }

        ItemMeta meta = item.getItemMeta();

        return meta.hasAttributeModifiers() || meta.hasEnchants() || meta.isUnbreakable();

    }


    public static boolean isUnProtectedItem(ItemStack item){

        if (item == null || item.getType().isAir()){
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();

        return itemMeta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.BOOLEAN);

    }

    /**
     * 保護対象のアイテムかどうかを判別します
     * @param item
     * @return true 保護対象
     */
    public static boolean isProtectionLocked(ItemStack item) {

        if (item == null || item.getType().isAir()){
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();

        // 保護対象から外すものかの判定
        if (isUnProtectedItem(item)) {
            return false;
        }


        if (item.hasItemMeta()) {

                if (itemMeta.hasCustomModelDataComponent()){
                    return true;
                }

                if (itemMeta.hasItemModel()){
                    return true;
                }

        }

        return false;
    }



}
