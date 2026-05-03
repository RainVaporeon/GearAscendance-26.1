package io.github.rainvaporeon.gearascendance.commands;

import io.github.rainvaporeon.gearascendance.data.FakeAscendanceItemInfo;
import io.github.rainvaporeon.gearascendance.data.FakeAttunementItemInfo;
import io.github.rainvaporeon.gearascendance.data.FakeBlessingItemInfo;
import io.github.rainvaporeon.gearascendance.data.ItemAscendanceInfo;
import io.github.rainvaporeon.gearascendance.utils.AscendanceHelper;
import io.github.rainvaporeon.gearascendance.utils.ItemGetter;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UpdateItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!"ascendance_updateitem".equals(command.getName())) return false;

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return true;
        }

        ItemStack handStack = player.getInventory().getItemInMainHand();

        // bukkit im trusting you with this even though the setter is nullable
        if (handStack.getType().isAir()) {
            player.sendMessage("You must be holding an item to update it!");
            return true;
        }

        ItemStack is;
        if (handStack.getType().equals(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)) {
            is = updateTemplate(handStack);
        } else {
            is = updateItem(handStack);
        }

        if (is == null) {
            player.sendMessage("This item does not require updating.");
        } else {
            player.getInventory().setItemInMainHand(is);
            player.sendMessage("This item has been updated.");
        }
        return true;
    }

    private ItemStack updateTemplate(ItemStack stack) {
        ItemStack ret = stack.clone();
        int qty = ret.getAmount();

        FakeAttunementItemInfo attune = FakeAttunementItemInfo.fromItemStack(stack);
        FakeBlessingItemInfo bless = FakeBlessingItemInfo.fromItemStack(stack);

        if (attune == FakeAttunementItemInfo.NONE && bless == FakeBlessingItemInfo.NONE) return null; // no need

        int highestOfTiers = Math.max(attune.tier(), bless.tier());
        boolean bestBlessing = attune.blessed() || Math.random() <= bless.successProbability(); // quick roll
        Enchantment bestAttune = bless.attune() == null ?
                (Math.random() <= attune.successProbability() ? attune.target() : null) : bless.attune();

        attune.detachFromItem(ret);
        bless.detachFromItem(ret);

        // generate an ideal one
        ItemStack data = ItemGetter.getAscendanceTemplate(highestOfTiers, bestBlessing, bestAttune);
        data.setAmount(qty);
        return data;
    }

    private ItemStack updateItem(ItemStack stack) {
        ItemStack stx = stack.clone();

        FakeAscendanceItemInfo info = FakeAscendanceItemInfo.fromItemStack(stx);
        ItemAscendanceInfo ascInfo = ItemAscendanceInfo.fromItemStack(stx);

        if (ascInfo == ItemAscendanceInfo.NONE) return null;

        info.detachFromItem(stx);

        AscendanceHelper.reapplyAscendTier(stx, ascInfo.ascendanceTier());

        return stx;
    }
}
