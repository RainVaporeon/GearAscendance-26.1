package io.github.rainvaporeon.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemGetter {

    public static ItemStack getAscendanceTemplate(int tier) {
        return getAscendanceTemplate(tier, false, null);
    }

    public static ItemStack getAscendanceTemplate(int tier, Enchantment attuned) {
        return getAscendanceTemplate(tier, false, attuned);
    }

    public static ItemStack getAscendanceTemplate(int tier, boolean blessed) {
        return getAscendanceTemplate(tier, blessed, null);
    }

    public static ItemStack getAscendanceTemplate(int tier, boolean blessed, Enchantment attuned) {
        ItemStack template = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        ItemUtils.setTemplateAscendanceTier(template, tier, blessed, attuned);
        return template;
    }
}
