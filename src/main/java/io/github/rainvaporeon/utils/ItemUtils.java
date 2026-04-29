package io.github.rainvaporeon.utils;

import io.github.rainvaporeon.EntryPoint;
import io.github.rainvaporeon.data.AscendanceTemplateInfo;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
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
                    EntryPoint.getSmithingInfoKey(),
                    PersistentDataType.STRING,
                    ai.toDetailJson()
            );
        });
    }

    public static String convertToDisplayName(Enchantment e) {
        NamespacedKey key = e.getKeyOrNull();
        if (key == null) return "";
        String name = key.getKey(); // e.g. "sharpness"

        return Arrays.stream(name.split("_"))
                .map(s -> {
                    if ("the".equals(s) || "of".equals(s)) return s;
                    return s.substring(0, 1).toUpperCase() + s.substring(1);
                })
                .collect(Collectors.joining(" "));
    }

    public static List<String> getBlessedTemplateLoreAtTier(int tier) {
        List<String> ls = new ArrayList<>(10);
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

    public static void applyMeta(ItemStack isx, Consumer<ItemMeta> metaConsumer) {
        ItemMeta im = isx.getItemMeta();
        metaConsumer.accept(im);
        isx.setItemMeta(im);
    }

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
        if (level <= 0) return 0;
        return switch (level) {
            case 1 -> 50;
            case 2 -> 65;
            case 3 -> 80;
            case 4 -> 100;
            case 5 -> 110;
            default -> 100 + (10 * (level - 4));
        };
    }

    /**
     * Gets the base success probability when performing an ascendance at this level
     * @param level the level
     * @return the base chance for the ascendance to succeed (advancing into the next level)
     * @apiNote success chance after level 5 always fails.
     */
    public static int getBaseSuccessChance(int level) {
        if (level < 0) return 100;
        return switch (level) {
            case 0 -> 80;
            case 1 -> 75;
            case 2 -> 40;
            case 3 -> 15;
            case 4 -> 5;
            default -> 0;
        };
    }
}
