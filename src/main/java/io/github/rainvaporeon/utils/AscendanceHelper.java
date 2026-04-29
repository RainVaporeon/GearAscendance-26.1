package io.github.rainvaporeon.utils;

import io.github.rainvaporeon.EntryPoint;
import io.github.rainvaporeon.data.AscendanceTemplateInfo;
import io.github.rainvaporeon.data.FakeItemInfo;
import io.github.rainvaporeon.data.ItemAscendanceInfo;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AscendanceHelper {

    public static boolean isValidTemplateItem(ItemStack is) {
        return getAscendingTemplateInfo(is) != AscendanceTemplateInfo.NONE;
    }

    public static ItemAscendanceInfo getItemAscendanceInfo(ItemStack is) {
        if (is == null) return ItemAscendanceInfo.NONE;

        if (!is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            is.setItemMeta(im);
        }

        assert is.getItemMeta() != null;

        String str = is.getItemMeta().getPersistentDataContainer().get(
                EntryPoint.getAscendanceInfoKey(),
                PersistentDataType.STRING
        );

        return ItemAscendanceInfo.fromJson(str);
    }

    public static AscendanceTemplateInfo getAscendingTemplateInfo(ItemStack is) {
        if (is == null) return AscendanceTemplateInfo.NONE;

        if (!is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            is.setItemMeta(im);
        }

        assert is.getItemMeta() != null;

        String str = is.getItemMeta().getPersistentDataContainer().get(
                EntryPoint.getSmithingInfoKey(),
                PersistentDataType.STRING
        );

        return AscendanceTemplateInfo.fromJson(str);
    }

    public static ItemStack generateFakeCompletionItem(ItemStack is, ItemAscendanceInfo ascendanceInfo, int currentTier, AscendanceTemplateInfo info) {
        ItemStack isx = is.clone();

        AscendanceHelper.reapplyAscendProbabilityAndTier(isx,
                ascendanceInfo.ascendanceTier() + 1,
                AscendanceHelper.getAscendanceProbability(currentTier, info.tier()),
                info.blessed(),
                info.attune());

        // note: ok so we want lhs without rhs so we do this instead
        List<Enchantment> candidates = new ArrayList<>();
        List<Enchantment> applied = ascendanceInfo.appliedAscendance();
        is.getEnchantments().keySet().forEach(ench -> {
            if (applied.contains(ench)) return;
            candidates.add(ench);
        });

        FakeItemInfo fi = new FakeItemInfo(
                currentTier + 1,
                candidates,
                info.attune(),
                info.blessed(),
                AscendanceHelper.getAscendanceProbability(currentTier, info.tier())
        );

        ItemUtils.applyMeta(isx, meta -> {
            meta.getPersistentDataContainer().set(
                    EntryPoint.getFakeItemInfo(),
                    PersistentDataType.STRING,
                    fi.toJson()
            );
        });

        return isx;
    }

    /**
     * Provides a predicate that clears intermediate lore
     * @return the predicate
     */
    private static Predicate<String> clearIntermediatePredicate() {
        return str -> {
            return str.contains("Ascended ") ||
                    str.contains(ChatColor.LIGHT_PURPLE + "Blessed") ||
                    str.contains(ChatColor.AQUA + "Attuned " + ChatColor.GRAY) ||
                    str.contains(ChatColor.GRAY + "Success Rate: " + ChatColor.WHITE);
        };
    }

    public static void reapplyAscendTier(ItemStack isx, int tier) {
        if (tier <= 0) {
            ItemUtils.applyMeta(isx, meta -> {
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

                assert lore != null;

                lore.removeIf(AscendanceHelper.clearIntermediatePredicate());
            });
            return;
        }
        ItemUtils.applyMeta(isx, meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            assert lore != null;

            lore.removeIf(AscendanceHelper.clearIntermediatePredicate());

            ChatColor color = switch (tier) {
                case 1 -> ChatColor.GRAY;
                case 2 -> ChatColor.WHITE;
                case 3 -> ChatColor.LIGHT_PURPLE;
                case 4 -> ChatColor.DARK_PURPLE;
                case 5 -> ChatColor.GOLD;
                default -> ChatColor.RED;
            };

            lore.addLast(color + "Ascended " + RomanNumeral.toRomanNumerals(tier));

            meta.setLore(lore);
        });
    }

    public static void reapplyAscendProbabilityAndTier(ItemStack isx, int tier, double probability, boolean blessed, Enchantment attune) {
        ItemUtils.applyMeta(isx, meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            assert lore != null;

            lore.removeIf(AscendanceHelper.clearIntermediatePredicate());

            ChatColor color = switch (tier) {
                case 1 -> ChatColor.GRAY;
                case 2 -> ChatColor.WHITE;
                case 3 -> ChatColor.LIGHT_PURPLE;
                case 4 -> ChatColor.DARK_PURPLE;
                case 5 -> ChatColor.GOLD;
                default -> ChatColor.MAGIC;
            };

            lore.addLast(color + "Ascended " + RomanNumeral.toRomanNumerals(tier) +
                    ChatColor.GRAY + "...?");

            if (blessed) {
                lore.addLast(ChatColor.LIGHT_PURPLE + "Blessed");
            }

            if (attune != null) {
                lore.addLast(ChatColor.AQUA + "Attuned " + ChatColor.GRAY + "(" + attune.getKeyOrNull() + ")");
            }

            lore.addLast(String.format(
                    ChatColor.GRAY + "Success Rate: " + ChatColor.WHITE + "%.2f%%", probability * 100.0
            ));

            meta.setLore(lore);
        });
    }

    public static double getAscendanceProbability(int currentTier, int templateTier) {
        double baseCompletionRate = ItemUtils.getBaseSuccessChance(currentTier) / 100.0D;
        double templateMultiplier = ItemUtils.getSuccessMultiplierPercentage(templateTier) / 100.0D;

        return baseCompletionRate * templateMultiplier;
    }
}
