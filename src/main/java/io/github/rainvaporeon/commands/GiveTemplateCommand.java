package io.github.rainvaporeon.commands;

import io.github.rainvaporeon.utils.*;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class GiveTemplateCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!"ascendance".equals(command.getName())) return false;

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You need to be a player to run this command!");
            return true;
        }

        // help message
        if (args.length == 0) return false;

        // ascendance give
        if (args.length == 1) return false;

        // ascendance give template
        if (args.length == 2) {
            player.getInventory().addItem(
                    ItemInstances.TEMPLATE_T1
            );
            return true;
        }

        int amount = Parser.ignoring(() -> Integer.parseInt(args[2]), 1);
        int tier = args.length >= 4 ? Parser.ignoring(() -> Integer.parseInt(args[3]), 1) : 1;
        boolean blessed = args.length >= 5 ? Parser.ignoring(() -> Boolean.parseBoolean(args[4]), false) : false;
        String bias = args.length >= 6 ? args[5] : "";

        NamespacedKey key = bias.isBlank() ? null : NamespacedKey.fromString(bias);

        ItemStack is = ItemGetter.getAscendanceTemplate(
                tier, blessed, key == null ? null : Registry.ENCHANTMENT.get(key)
        );

        is.setAmount(amount);

        player.getInventory().addItem(
                is
        );

        String name;

        if (is.hasItemMeta()) {
            ItemMeta meta = is.getItemMeta();
            assert meta != null;
            name = meta.getDisplayName();
        } else {
            name = is.getType().toString();
        }

        player.sendMessage(String.format(
                "Given %d of %s",
                amount, name
        ));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!"ascendance".equals(command.getName())) return List.of();

        if (args.length == 0) {
            return List.of("give");
        }

        if (args.length == 1) {
            return TabCompletionHelper.provideStarting(args[0], "give");
        }

        if (args.length == 2) {
            switch (args[0]) {
                case "give":
                    return TabCompletionHelper.provideStarting(
                            args[1],
                            "template"
                    );
            }
        }

        if (args.length >= 5) {
            if (args.length == 5) {
                return TabCompletionHelper.provideStarting(args[4], "true", "false");
            }
            if (args.length == 6) {
                return TabCompletionHelper.provideStarting(args[5], Registry.ENCHANTMENT.stream().filter(e -> {
                    NamespacedKey key = e.getKeyOrNull();
                    return key != null;
                }).map(e -> e.getKeyOrNull().getKey()).toList());
            }
        }

        // base case
        return List.of();
    }
}
