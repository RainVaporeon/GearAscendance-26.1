package io.github.rainvaporeon.gearascendance.utils;

import io.github.rainvaporeon.gearascendance.EntryPoint;
import io.github.rainvaporeon.gearascendance.data.AscendanceTemplateInfo;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemUtils {

    public static boolean applyGlintAndHideEnchants(ItemStack is) {
        if (is.getItemMeta() == null) return false;
        is.addUnsafeEnchantment(Enchantment.PROTECTION, 1);
        ItemUtils.applyMeta(is, meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        return true;
    }

    public static void setTemplateAscendanceTier(ItemStack is, int level, boolean blessed, Enchantment attuned) {
        if (!is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            is.setItemMeta(im);
        }

        assert is.getItemMeta() != null;

        ItemUtils.applyMeta(is, meta -> {
            meta.setEnchantmentGlintOverride(level >= 1);
            meta.setRarity(ItemUtils.getRarityByTier(level));
            if (blessed) {
                if (attuned != null) {
                    meta.setLore(ItemUtils.getBlessedTemplateLoreAtTierAttuned(level, attuned));
                } else {
                    meta.setLore(ItemUtils.getBlessedTemplateLoreAtTier(level));
                }
            } else {
                if (attuned != null) {
                    meta.setLore(ItemUtils.getUnblessedTemplateLoreAtTierAttuned(level, attuned));
                } else {
                    meta.setLore(ItemUtils.getUnblessedTemplateLoreAtTier(level));
                }
            }
            StringBuilder sb = new StringBuilder("Gear Ascendance Template (" + RomanNumeral.toRomanNumerals(level) + ")");
            if (blessed) {
                sb.append(" (φ)");
            }
            if (attuned != null) {
                sb.append(" (ϑ)");
            }
            meta.setItemName(sb.toString());
            AscendanceTemplateInfo ai = new AscendanceTemplateInfo(level, blessed, attuned);
            meta.getPersistentDataContainer().set(
                    EntryPoint.getAscendanceTemplateInfoKey(),
                    PersistentDataType.STRING,
                    ai.toJson()
            );
        });
    }

    public static String convertToDisplayName(Enchantment e) {
        if (e == null) return "null";
        NamespacedKey key = e.getKeyOrNull();
        if (key == null) return "null";
        String name = key.getKey(); // e.g. "sharpness"

        return Arrays.stream(name.split("_"))
                .map(s -> {
                    if ("the".equals(s) || "of".equals(s)) return s;
                    return s.substring(0, 1).toUpperCase() + s.substring(1);
                })
                .collect(Collectors.joining(" "));
    }

    public static List<String> getBlessedTemplateLoreAtTier(int tier) {
        List<String> ls = new ArrayList<>(16);
        ls.add(ChatColor.GRAY + "Template to perform a gear ascendance.");
        ls.add(ChatColor.GRAY + "Requires a " + ChatColor.DARK_PURPLE + "Netherite Ingot " + ChatColor.GRAY + "on a smithing table.");
        ls.add(ChatColor.GRAY + "Success multiplier: " + ChatColor.GREEN +
                ItemUtils.getSuccessMultiplierPercentage(tier) + "%");
        return ls;
    }

    public static List<String> getUnblessedTemplateLoreAtTier(int tier) {
        List<String> ls = ItemUtils.getBlessedTemplateLoreAtTier(tier);
        ls.add(" ");
        ls.add(ChatColor.GRAY + "This template is not blessed, failing to perform an ascendance");
        ls.add(ChatColor.GRAY + "will result in " + ChatColor.RED + "Curse of Vanishing" + ChatColor.GRAY + " applied to the item.");
        return ls;
    }

    public static List<String> getUnblessedTemplateLoreAtTierAttuned(int tier, Enchantment enchantment) {
        List<String> ls = ItemUtils.getUnblessedTemplateLoreAtTier(tier);
        ls.add(" ");
        ls.add(ChatColor.GOLD + "This template is attuned to " + ChatColor.AQUA + convertToDisplayName(enchantment));
        return ls;
    }

    public static List<String> getBlessedTemplateLoreAtTierAttuned(int tier, Enchantment enchantment) {
        List<String> ls = ItemUtils.getBlessedTemplateLoreAtTier(tier);
        ls.add(" ");
        ls.add(ChatColor.GOLD + "This template is attuned to " + ChatColor.AQUA + convertToDisplayName(enchantment));
        return ls;
    }

    /**
     * Performs modification on the ItemStack's ItemMeta.
     * @param isx the item stack
     * @param metaConsumer the meta to perform modification on
     */
    public static void applyMeta(ItemStack isx, Consumer<ItemMeta> metaConsumer) {
        ItemMeta im = isx.getItemMeta();
        metaConsumer.accept(im);
        isx.setItemMeta(im);
    }

    /**
     * Gets the native rarity by smithing template tier
     * @param level the level
     * @return the tier
     */
    private static ItemRarity getRarityByTier(int level) {
        if (level >= 5) return ItemRarity.EPIC;
        if (level == 4) return ItemRarity.RARE;
        if (level == 3) return ItemRarity.UNCOMMON;
        return ItemRarity.COMMON;
    }

    /**
     * Gets the base success probability multiplier when using this level of template
     * @param level the template level
     * @return the success multiplier
     * @apiNote template after level 5 is generated with 10% each level beyond level 4
     */
    public static int getSuccessMultiplierPercentage(int level) {
        return FeatureConsts.successMultiplier(level);
    }

    /**
     * Gets the base success probability when performing an ascendance at this level
     * @param level the level
     * @return the base chance for the ascendance to succeed (advancing into the next level)
     * @apiNote success chance after level 5 always fails.
     */
    public static int getBaseSuccessChance(int level) {
        return FeatureConsts.baseSuccessRate(level);
    }
}
