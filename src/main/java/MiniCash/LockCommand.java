package MiniCash;

import MiniCash.api.ApplyProtectionResult;
import MiniCash.api.ItemChekerAPI;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LockCommand implements BasicCommand {

    private Component main = Component.text("[").color(NamedTextColor.DARK_GRAY).append(Component.text("ItemCheker").color(NamedTextColor.AQUA).append(Component.text("]").color(NamedTextColor.DARK_GRAY)));

    private final Cache<UUID, ItemStack> pendingItems = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(10)
            .build();

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {

        if (commandSourceStack.getExecutor() instanceof Player player) {

            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType().isAir()) {
                player.sendMessage(main.append(Component.text("メインハンドにアイテムを持ってください").color(NamedTextColor.RED)));
                return;
            }


            if (!ItemChekerAPI.isUnProtectedItem(item)) {

                ItemStack cachedItem = pendingItems.getIfPresent(player.getUniqueId());

                if (cachedItem == null || !cachedItem.equals(item)) {

                    player.sendMessage(main.append(
                            Component.text("本当に保護対象から解除しますか？ その場合は再度同じコマンドを実行してください", NamedTextColor.RED)
                    ));

                    pendingItems.put(player.getUniqueId(), item.clone());
                    return;
                }

                pendingItems.invalidate(player.getUniqueId());

                ApplyProtectionResult result = ItemChekerAPI.applyUnProtection(item);

                if (result.isSuccess()) {


                    player.sendMessage(main.append(Component.text("アイテムの保護を解除しました\n作業台などでアイテムを使用できます").color(NamedTextColor.DARK_GREEN)));

                }else {

                    player.sendMessage(main.append(Component.text("保護の解除に失敗しました").color(NamedTextColor.RED)));
                }

                return;

            }

            ApplyProtectionResult result = ItemChekerAPI.removeUnProtected(item);
            if (result.isSuccess()) {

                player.sendMessage(main.append(Component.text("アイテムを保護しました\n作業台などでアイテムを使用できなくなりました").color(NamedTextColor.YELLOW)));

            } else {

                player.sendMessage(main.append(Component.text(result.getMessage()).color(NamedTextColor.RED)));

            }

            return;


        } else {
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
