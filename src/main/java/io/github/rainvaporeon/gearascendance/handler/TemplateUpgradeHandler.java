package io.github.rainvaporeon.gearascendance.handler;

import io.github.rainvaporeon.gearascendance.EntryPoint;
import io.github.rainvaporeon.gearascendance.data.AscendanceTemplateInfo;
import io.github.rainvaporeon.gearascendance.data.FakeAttunementItemInfo;
import io.github.rainvaporeon.gearascendance.utils.AscendanceHelper;
import io.github.rainvaporeon.gearascendance.utils.FeatureConsts;
import io.github.rainvaporeon.gearascendance.utils.ItemGetter;
import io.github.rainvaporeon.gearascendance.utils.ItemUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class TemplateUpgradeHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack template = event.getInventory().getItem(0);
        ItemStack book = event.getInventory().getItem(1);

        if (template == null || book == null) return; // does not form

        if (AscendanceHelper.getAscendingTemplateInfo(template) == AscendanceTemplateInfo.NONE) {
          return;
        }

        if (!book.getType().equals(Material.ENCHANTED_BOOK)) {
           return; // not a book
        }

        if (!(book.getItemMeta() instanceof EnchantmentStorageMeta esm)) {
           return; // does not store anything
        }
        // implicit null-check ;)

        // do not permit multi-enchant
        if (esm.getStoredEnchants().size() != 1) {
            return;
        }

        // get first
        Enchantment target = esm.getStoredEnchants().keySet().iterator().next();
        int level = esm.getStoredEnchantLevel(target);

        // ignore non-max books
        if (target.getMaxLevel() > level) {
            return;
        }

        if (template.getAmount() != 1) {
            return;
        }

        // generate fake item
        AscendanceTemplateInfo info = AscendanceHelper.getAscendingTemplateInfo(template);

        ItemStack result = AscendanceHelper.generateFakeTemplateAttunementItem(info, target);

        event.getView().setRepairCost(30);
        event.setResult(result);

        Bukkit.getScheduler().runTask(EntryPoint.getInstance(), () -> {
            event.getView().setRepairCost(30);
            event.setResult(result);
        });


    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAnvilCompletion(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof AnvilInventory ai)) return;
        ItemStack isx = event.getCurrentItem();
        if (isx == null) return;
        if (event.getRawSlot() != 2 || event.getSlotType() != InventoryType.SlotType.RESULT) return;

        // handle fake item
        FakeAttunementItemInfo info = AscendanceHelper.getFakeAttunementTemplateInfo(isx);

        if (info == FakeAttunementItemInfo.NONE) return;

        event.setCancelled(true);

        HumanEntity clicker = event.getWhoClicked();

        if (clicker instanceof Player p && p.getGameMode() != GameMode.CREATIVE) {
            if (p.getLevel() < FeatureConsts.attunementXPCost()) return;
            p.giveExpLevels(-FeatureConsts.attunementXPCost());
        }

        ItemStack is = handleAttunement(clicker, isx, info);

        if (event.isShiftClick()) {
            clicker.getInventory().addItem(is);
        } else {
            event.getView().setCursor(is);
        }
        ai.clear();
        clicker.getWorld().playSound(
                clicker,
                Sound.BLOCK_ANVIL_USE,
                1.0f,
                1.0f
        );
        event.getView().setItem(0, null);
        event.getView().setItem(1, null);
        event.getView().setItem(2, null);
    }

    private ItemStack handleAttunement(HumanEntity player, ItemStack template, FakeAttunementItemInfo info) {
        if (Math.random() > info.successProbability()) {
            return failAttunement(player, template);
        }

        player.sendMessage(
                ChatColor.GREEN + "The attunement was successful!"
        );
        player.sendMessage(
                ChatColor.GREEN + "The ascendance template is now attuned to " +
                        ChatColor.AQUA + ItemUtils.convertToDisplayName(info.target())
        );

        player.getWorld().playSound(
                player,
                Sound.BLOCK_ANVIL_BREAK,
                1.0f,
                1.0f
        );

        return ItemGetter.getAscendanceTemplate(info.tier(), info.blessed(), info.target());
    }

    private ItemStack failAttunement(HumanEntity player, ItemStack template) {
        player.sendMessage(
                ChatColor.RED + "The attunement failed!"
        );
        player.sendMessage(
                ChatColor.GRAY + "Nothing was lost, except for the material..."
        );

        AscendanceTemplateInfo data = AscendanceHelper.getAscendingTemplateInfo(template);

        return ItemGetter.getAscendanceTemplate(data.tier(), data.blessed(), data.attune());
    }
}
