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

    @Deprecated
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

    public static ApplyProtectionResult applyProtection(ItemStack item ) {

        if (item == null || item.getType().isAir()) {
            return new ApplyProtectionResult(item , false , "指定されたアイテムがなかったため保護キーを指定できませんでした");
        }


        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.BOOLEAN ,  true);
            item.setItemMeta(meta);
        }

        return new ApplyProtectionResult(item , true , "保護キーを指定することが出来ました");
    }

    public static boolean removePlayerProtection(ItemStack item) {

        if (!isProtectionLocked(item)){
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



    public static boolean isProtectionLocked(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta.getPersistentDataContainer().has(namespacedKey, PersistentDataType.BOOLEAN)) {
            return true;
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


    @Deprecated
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

    @Deprecated
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
