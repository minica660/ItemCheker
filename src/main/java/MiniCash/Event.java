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

                // カーソルに持っているアイテムが保護対象ならキャンセル
                if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    if (ItemChekerAPI.isProtected(cursorItem)) {
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
                || type == InventoryType.CARTOGRAPHY;
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

            default -> false;
        };
    }

}
