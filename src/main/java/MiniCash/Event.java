package MiniCash;

import MiniCash.api.ItemChekerAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Event implements Listener {

    @EventHandler
    public void craft(InventoryClickEvent event) {


        if (event.getClickedInventory() == null) {
            return;
        }

        Inventory topInventory = event.getView().getTopInventory();
        InventoryType type = topInventory.getType();

        if (!protectType(type)) {
            return;
        }


        if (event.getClick() == ClickType.NUMBER_KEY ||
                event.getClick() == ClickType.SWAP_OFFHAND ||
                event.getClick() == ClickType.DOUBLE_CLICK) {

            event.setCancelled(true);
            return;
        }


        if (event.isShiftClick()) {
            if (event.getClickedInventory().equals(event.getView().getBottomInventory())) {
                if (ItemChekerAPI.isProtected(event.getCurrentItem())) {
                    event.setCancelled(true);
                    return;
                }
            }
            return;
        }

        if (event.getClickedInventory().equals(topInventory)) {
            int slot = event.getSlot();

            if(isMaterialSlot(type,slot)){

                ItemStack cursorItem = event.getCursor();

                if (event.getAction().name().contains("PLACE")) {
                    // カーソルに持っているアイテムが保護対象ならキャンセル
                    if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                        if (ItemChekerAPI.isProtected(cursorItem)) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                }

                if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                    if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                        if (ItemChekerAPI.isProtected(cursorItem)) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

            }


        }





    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        Inventory topInventory = event.getView().getTopInventory();
        InventoryType type = topInventory.getType();

        if (!protectType(type)) {
            return;
        }

        // ドラッグしようとしているアイテムのチェック
        ItemStack draggedItem = event.getOldCursor();
        if (draggedItem.getType() == Material.AIR) {
            return;
        }

        if (ItemChekerAPI.isProtected(draggedItem)) {
            // ドラッグによってアイテムが置かれる予定の全スロット（生のID）をループ
            for (int rawSlot : event.getRawSlots()) {

                // 生のスロットIDが、上インベントリ（作業台など）の範囲内かチェック
                if (rawSlot < topInventory.getSize()) {

                    // 素材スロット（0から始まるインベントリ固有のスロット番号）に該当するかチェック
                    if (isMaterialSlot(type, rawSlot)) {
                        // 1つでも保護対象スロットへの配置が含まれていればドラッグ全体をキャンセル
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    private boolean protectType(InventoryType type){
        return type == InventoryType.WORKBENCH
                || type == InventoryType.ANVIL
                || type == InventoryType.SMITHING       // 鍛冶台を追加
                || type == InventoryType.CARTOGRAPHY
                || type == InventoryType.FURNACE
                || type == InventoryType.SMOKER
                || type == InventoryType.BLAST_FURNACE
                || type == InventoryType.BREWING
                || type == InventoryType.HOPPER
                || type == InventoryType.ENCHANTING
                || type == InventoryType.GRINDSTONE
                || type == InventoryType.LOOM
                || type == InventoryType.STONECUTTER
                || type == InventoryType.BEACON
                || type == InventoryType.CRAFTER;
    }


    private boolean isMaterialSlot(InventoryType type, int slot) {
        return switch (type) {
            // 作業台
            case WORKBENCH -> (slot >= 1 && slot <= 9);

            // 金床
            case ANVIL -> (slot == 0 || slot == 1);

            // 鍛冶台
            case SMITHING -> (slot >= 0 && slot <= 2);

            // 製図台
            case CARTOGRAPHY -> (slot == 0 || slot == 1);

            case FURNACE, SMOKER, BLAST_FURNACE -> (slot == 0 || slot == 1);
            // 醸造台
            case BREWING -> (slot >= 0 && slot <= 4);
            // エンチャントテーブル
            case ENCHANTING -> (slot == 0 || slot == 1);
            // 砥石
            case GRINDSTONE -> (slot == 0 || slot == 1);
            // 織機
            case LOOM -> (slot >= 0 && slot <= 2);
            // ストーンカッター
            case STONECUTTER -> (slot == 0);
            // ビーコン
            case BEACON -> (slot == 0);

            case CRAFTER -> (slot >= 0 && slot <= 8);

            case HOPPER -> (slot >= 0 && slot <= 4);

            default -> false;
        };
    }


    @EventHandler
    public void inventoryPickup(InventoryPickupItemEvent event) {

        if (event.isCancelled()) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();

        if (ItemChekerAPI.isProtected(item)) {
            if (event.getInventory().getType() == InventoryType.HOPPER) {

                event.setCancelled(true);

            }
        }
    }

}
