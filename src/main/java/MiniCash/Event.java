package MiniCash;

import MiniCash.api.ItemChekerAPI;
import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;

public class Event implements Listener {

    @EventHandler
    public void craft(InventoryClickEvent event) {


        if (event.getClickedInventory() == null) {
            return;
        }

        Inventory topInventory = event.getView().getTopInventory();
        Inventory bottomInventory = event.getView().getBottomInventory();
        Inventory clickedInventory = event.getClickedInventory();

        InventoryType type = topInventory.getType();

        ItemStack item = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

//        if (!protectType(type)) {
//            return;
//        }


        if (clickedInventory == bottomInventory) {
            if (event.isShiftClick() && ItemChekerAPI.isProtectionLocked(item)) {
                if (isRestrictedInventory(type)) {
                    event.setCancelled(true);
                    return;
                }
            }
            return;
        }



        if (clickedInventory == topInventory && isRestrictedInventory(type)) {

            // マウスカーソルに制限アイテムを持った状態で、作業枠をクリックしたとき
            if (ItemChekerAPI.isProtectionLocked(cursorItem)) {
                if (isProcessingSlot(type, event.getSlot())) {
                    event.setCancelled(true);
                    return;
                }
            }

            // スロットに元々ある制限アイテムを、作業枠の中で動かそうとしたとき
            if (ItemChekerAPI.isProtectionLocked(item)) {
                if (isProcessingSlot(type, event.getSlot())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        //  数字キー（ホットバー）による上側へのすり抜け対策
        if (event.getClick() == ClickType.NUMBER_KEY) {
            // アイテムを送り込もうとしたターゲット（スロット）が「上側」かつ「素材スロット」の場合のみブロック
            if (clickedInventory == topInventory && isProcessingSlot(type, event.getSlot())) {
                event.setCancelled(true);
            }
        }

//        if (event.getClick() == ClickType.NUMBER_KEY ||
//                event.getClick() == ClickType.SWAP_OFFHAND ||
//                event.getClick() == ClickType.DOUBLE_CLICK) {
//
//            event.setCancelled(true);
//            return;
//        }




//        if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
//            if (cursorItem != null && cursorItem.getType() != Material.AIR) {
//                if (ItemChekerAPI.isProtected(cursorItem)) {
//                    event.setCancelled(true);
//                    return;
//                }
//            }
//        }


//        if (event.isShiftClick()) {
//            if (event.getClickedInventory().equals(event.getView().getBottomInventory())) {
//                if (ItemChekerAPI.isProtected(event.getCurrentItem())) {
//                    event.setCancelled(true);
//                    return;
//                }
//            }
//            return;
//        }

//        if (event.getClickedInventory().equals(topInventory)) {
//            int slot = event.getSlot();
//
//            if(isMaterialSlot(type,slot)){
//
//                ItemStack cursorItem = event.getCursor();
//
//                if (event.getAction().name().contains("PLACE")) {
//                    // カーソルに持っているアイテムが保護対象ならキャンセル
//                    if (cursorItem != null && cursorItem.getType() != Material.AIR) {
//                        if (ItemChekerAPI.isProtected(cursorItem)) {
//                            event.setCancelled(true);
//                            return;
//                        }
//                    }
//
//                }
//
//            }
//
//
//        }





    }


    @EventHandler
    public void onRecipeBookClick(PlayerRecipeBookClickEvent event) {
        Player player = event.getPlayer();

        Recipe recipe = Bukkit.getRecipe(event.getRecipe());

        if (recipe == null){
            return;
        }

        List<Material> requiredMaterials = new ArrayList<>();

        if (recipe instanceof ShapedRecipe shaped) {
            for (ItemStack ingredient : shaped.getIngredientMap().values()) {
                if (ingredient != null) requiredMaterials.add(ingredient.getType());
            }
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            for (ItemStack ingredient : shapeless.getIngredientList()) {
                if (ingredient != null) requiredMaterials.add(ingredient.getType());
            }
        } else if (recipe instanceof CookingRecipe<?> cooking) { // かまど、燻製器、溶鉱炉など
            RecipeChoice choice = cooking.getInputChoice();
            if (choice instanceof RecipeChoice.MaterialChoice matChoice) {
                requiredMaterials.addAll(matChoice.getChoices());
            }
        }

        if (requiredMaterials.isEmpty()) return;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (requiredMaterials.contains(item.getType()) && ItemChekerAPI.isProtectionLocked(item)) {
                event.setCancelled(true);
                return;
            }
        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        Inventory topInventory = event.getView().getTopInventory();
        InventoryType type = topInventory.getType();

        if (!isRestrictedInventory(type)) {
            return;
        }

        // ドラッグしようとしているアイテムのチェック
        ItemStack draggedItem = event.getOldCursor();

        if (draggedItem.getType() == Material.AIR) {
            return;
        }


        if (ItemChekerAPI.isProtectionLocked(draggedItem)) {

            for (int rawSlot : event.getRawSlots()) {


                if (rawSlot < topInventory.getSize() && isProcessingSlot(type, rawSlot)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (ItemChekerAPI.isProtectionLocked(event.getItemInHand())) {
            event.setCancelled(true);
        }

    }


    private boolean isRestrictedInventory(InventoryType type) {
        return switch (type) {
            case CRAFTING, WORKBENCH, FURNACE, BLAST_FURNACE, SMOKER,
                 BREWING, SMITHING, STONECUTTER, CARTOGRAPHY, LOOM, GRINDSTONE, ANVIL -> true;
            default -> false;
        };
    }


//    private boolean protectType(InventoryType type){
//        return type == InventoryType.WORKBENCH
//                || type == InventoryType.ANVIL
//                || type == InventoryType.SMITHING       // 鍛冶台を追加
//                || type == InventoryType.CARTOGRAPHY
//                || type == InventoryType.FURNACE
//                || type == InventoryType.SMOKER
//                || type == InventoryType.BLAST_FURNACE
//                || type == InventoryType.BREWING
//                || type == InventoryType.HOPPER
//                || type == InventoryType.ENCHANTING
//                || type == InventoryType.GRINDSTONE
//                || type == InventoryType.LOOM
//                || type == InventoryType.STONECUTTER
//                || type == InventoryType.BEACON
//                || type == InventoryType.CRAFTER;
//    }


    private boolean isProcessingSlot(InventoryType type, int slot) {
        return switch (type) {
            case CRAFTING, WORKBENCH -> slot != 0; // 完成品(0)以外はすべて素材
            case FURNACE, BLAST_FURNACE, SMOKER -> slot == 0 || slot == 1; // 0=素材, 1=燃料
            case BREWING -> slot == 3 || slot == 4; // 3=素材, 4=燃料
            case SMITHING -> slot == 0 || slot == 1 || slot == 2; // 0=ベース, 1=素材, 2=型
            case STONECUTTER, CARTOGRAPHY -> slot == 0 || slot == 1;
            case LOOM, GRINDSTONE, ANVIL -> slot == 0 || slot == 1 || slot == 2;
            default -> false;
        };
    }

//    private boolean isMaterialSlot(InventoryType type, int slot) {
//        return switch (type) {
//            // 作業台
//            case WORKBENCH -> (slot >= 1 && slot <= 9);
//
//            // 金床
//            case ANVIL -> (slot == 0 || slot == 1);
//
//            // 鍛冶台
//            case SMITHING -> (slot >= 0 && slot <= 2);
//
//            // 製図台
//            case CARTOGRAPHY -> (slot == 0 || slot == 1);
//
//            case FURNACE, SMOKER, BLAST_FURNACE -> (slot == 0 || slot == 1);
//            // 醸造台
//            case BREWING -> (slot >= 0 && slot <= 4);
//            // エンチャントテーブル
//            case ENCHANTING -> (slot == 0 || slot == 1);
//            // 砥石
//            case GRINDSTONE -> (slot == 0 || slot == 1);
//            // 織機
//            case LOOM -> (slot >= 0 && slot <= 2);
//            // ストーンカッター
//            case STONECUTTER -> (slot == 0);
//            // ビーコン
//            case BEACON -> (slot == 0);
//
//            case CRAFTER -> (slot >= 0 && slot <= 8);
//
//            case HOPPER -> (slot >= 0 && slot <= 4);
//
//            default -> false;
//        };
//    }


    @EventHandler
    public void inventoryPickup(InventoryPickupItemEvent event) {

        if (event.isCancelled()) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();

        if (ItemChekerAPI.isProtectionLocked(item)) {
            if (event.getInventory().getType() == InventoryType.HOPPER) {

                event.setCancelled(true);

            }
        }
    }

}
