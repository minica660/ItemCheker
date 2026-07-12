package MiniCash.api;

import org.bukkit.inventory.ItemStack;

public class ApplyProtectionResult {
    private ItemStack resultItem;
    private boolean isSuccess;
    private String message;

    public ApplyProtectionResult(ItemStack resultItem, boolean isSuccess, String message) {
        this.resultItem = resultItem;
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public ItemStack getResultItem() {
        return resultItem;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

}
