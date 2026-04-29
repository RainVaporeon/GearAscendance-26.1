package io.github.rainvaporeon.handler;

import io.github.rainvaporeon.EntryPoint;
import io.github.rainvaporeon.data.AscendanceTemplateInfo;
import io.github.rainvaporeon.data.FakeItemInfo;
import io.github.rainvaporeon.data.ItemAscendanceInfo;
import io.github.rainvaporeon.utils.AscendanceHelper;
import io.github.rainvaporeon.utils.ItemUtils;
import io.github.rainvaporeon.utils.ListHelper;
import io.github.rainvaporeon.utils.RomanNumeral;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SmithCompletionHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onSmithCompletion(SmithItemEvent event) {
        ItemStack is = event.getInventory().getResult();

        if (is == null) return;;

        FakeItemInfo info = this.getFakeItem(is);

        if (info == FakeItemInfo.NONE) return;

        ItemStack isx = this.performAscendance(is, info, event.getWhoClicked());

        event.getInventory().setResult(
                isx
        );

    }

    private FakeItemInfo getFakeItem(ItemStack is) {
        if (!is.hasItemMeta()) return FakeItemInfo.NONE;

        assert is.getItemMeta() != null;

        return FakeItemInfo.fromJson(is.getItemMeta().getPersistentDataContainer().get(
                EntryPoint.getFakeItemInfo(),
                PersistentDataType.STRING
        ));
    }

    private ItemStack performAscendance(ItemStack is, FakeItemInfo info, HumanEntity clicker) {
        ItemStack isx = is.clone();

        double randomness = Math.random();
        // Take 10% for example, you roll more than 0.1 and you fail
        if (randomness > info.successProbability()) {
            ItemStack ret = failAscendance(isx, info, clicker);
            AscendanceHelper.reapplyAscendTier(ret, info.nextTier() - 1); // does not succeed
            return ret;
        }

        Enchantment selection = ListHelper.randomElement(
                info.upgradeCandidates(),
                info.attunement(),
                info.attunement() == null ? 0 : 2
        );

        int targetLevelSrc = isx.removeEnchantment(selection);

        isx.addUnsafeEnchantment(selection, targetLevelSrc + 1);

        ItemUtils.applyMeta(isx, meta -> {
            ItemAscendanceInfo ia = ItemAscendanceInfo.fromJson(
                    meta.getPersistentDataContainer().get(
                            EntryPoint.getAscendanceInfoKey(),
                            PersistentDataType.STRING
                    )
            );

            List<Enchantment> applied = new ArrayList<>(ia.appliedAscendance());
            applied.add(selection);

            ItemAscendanceInfo nia = new ItemAscendanceInfo(
                    ia.ascendanceTier() + 1,
                    applied
            );

            meta.getPersistentDataContainer().set(
                    EntryPoint.getAscendanceInfoKey(),
                    PersistentDataType.STRING,
                    nia.toJson()
            );
        });

        clicker.sendMessage(
                ChatColor.GREEN + "The ascendance attempt was successful!"
        );
        clicker.sendMessage(
                String.format(ChatColor.GREEN + "The enchantment %s has been upgraded to level %s%s!",
                        ItemUtils.convertToDisplayName(selection),
                        ChatColor.AQUA,
                        RomanNumeral.toRomanNumerals(targetLevelSrc + 1)
                        )
        );

        AscendanceHelper.reapplyAscendTier(isx, info.nextTier());

        return isx;
    }

    private ItemStack failAscendance(ItemStack is, FakeItemInfo info, HumanEntity clicker) {
        ItemStack isx = is.clone();
        int nextTier = info.nextTier();
        int currentTier = nextTier - 1;
        if (currentTier > 0) {
            AscendanceHelper.reapplyAscendTier(isx, currentTier);
        }

        ItemUtils.applyMeta(isx, meta -> {
            meta.getPersistentDataContainer().remove(
                    EntryPoint.getFakeItemInfo()
            );
        });

        clicker.getWorld().playSound(
                clicker,
                Sound.BLOCK_ANVIL_DESTROY,
                1.0f,
                1.0f
        );

        clicker.sendMessage(
                ChatColor.RED + "The ascendance attempt failed!"
        );

        if (info.blessed() || is.getEnchantmentLevel(Enchantment.VANISHING_CURSE) > 0) {
            clicker.sendMessage(
                    ChatColor.GRAY + "Nothing occurred."
            );
        } else {
            clicker.sendMessage(
                    ChatColor.GRAY + "The item has received Curse of Vanishing..."
            );
            isx.addEnchantment(Enchantment.VANISHING_CURSE, 1);
        }

        return isx;
    }

}
