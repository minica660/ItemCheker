package MiniCash.api;

import MiniCash.ItemCheker;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemChekerAPI {
    private static ItemCheker plugin;

    private static NamespacedKey namespacedKey;

    public ItemChekerAPI(ItemCheker plugin) {
        ItemChekerAPI.init(plugin);
    }

    public static void init(ItemCheker pluginInstance) {
        plugin = pluginInstance;
        namespacedKey = new NamespacedKey(plugin, "item_cheker_protected_item");
    }

    public enum ProtectionType {
        SYSTEM("system"),
        PLAYER("player");

        private final String value;
        ProtectionType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }

    }

    public static ApplyProtectionResult applyProtection(ItemStack item , ProtectionType type) {

        if (item == null || item.getType().isAir()) {
            return new ApplyProtectionResult(item , false , "指定されたアイテムがなかったため保護キーを指定できませんでした");
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING ,  type.getValue());
            item.setItemMeta(meta);
        }

        return new ApplyProtectionResult(item , true , "保護キーを指定することが出来ました");
    }

    public static boolean removePlayerProtection(ItemStack item) {
        if (!isPlayerProtected(item)) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().remove(namespacedKey);
            item.setItemMeta(meta);
            return true;
        }
        return false;
    }



    public static boolean isProtected(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }


        return meta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING);
    }


    public static boolean isSystemProtected(ItemStack item) {
        if (item == null || item.getType().isAir()){
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null){
            return false;
        }

        String value = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
        return ProtectionType.SYSTEM.getValue().equals(value);
    }

    public static boolean isPlayerProtected(ItemStack item) {
        if (item == null || item.getType().isAir()){
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null){
            return false;
        }

        String value = meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
        return ProtectionType.PLAYER.getValue().equals(value);
    }

}
