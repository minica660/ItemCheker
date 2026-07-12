package MiniCash;

import MiniCash.api.ApplyProtectionResult;
import MiniCash.api.ItemChekerAPI;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

public class LockCommand implements BasicCommand {
    private Component main = Component.text("[").color(NamedTextColor.DARK_GRAY).append(Component.text("ItemCheker").color(NamedTextColor.AQUA).append(Component.text("]").color(NamedTextColor.DARK_GRAY)));
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {

        if (commandSourceStack.getExecutor() instanceof Player player) {

            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType().isAir()) {
                player.sendMessage(main.append(Component.text("メインハンドにアイテムを持ってください").color(NamedTextColor.RED)));
                return;
            }

            if (ItemChekerAPI.isSystemProtected(item)) {
                player.sendMessage(main.append(Component.text("このアイテムはシステムによって保護されていたため解除できませんでした").color(NamedTextColor.RED)));
                return;
            }

            if (ItemChekerAPI.isPlayerProtected(item)) {
                boolean success = ItemChekerAPI.removePlayerProtection(item);
                if (success) {
                player.sendMessage(main.append(Component.text("アイテムの保護を解除しました\n作業台などでアイテムを使用できます").color(NamedTextColor.DARK_GREEN)));

                } else {
                    player.sendMessage(main.append(Component.text("保護の解除に失敗しました").color(NamedTextColor.RED)));
                }

                return;
            }

            ApplyProtectionResult result = ItemChekerAPI.applyProtection(item, ItemChekerAPI.ProtectionType.PLAYER);
            if (result.isSuccess()) {

                player.sendMessage(main.append(Component.text("アイテムを保護しました\n作業台などでアイテムを使用できなくなりました").color(NamedTextColor.YELLOW)));

            } else {

                player.sendMessage(main.append(Component.text(result.getMessage()).color(NamedTextColor.RED)));

            }

            return ;


        }else {
            commandSourceStack.getSender().sendMessage(Component.text("このコマンドはプレイヤーのみ実行可能です").color(NamedTextColor.RED));
        }

    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }

    @Override
    public @Nullable String permission() {
        return "itemcheker.command.lock";
    }
}
