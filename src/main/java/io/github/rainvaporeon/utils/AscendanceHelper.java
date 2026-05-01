package io.github.rainvaporeon.utils;

import io.github.rainvaporeon.EntryPoint;
import io.github.rainvaporeon.data.*;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class AscendanceHelper {

    public static boolean isValidTemplateItem(ItemStack is) {
        return getAscendingTemplateInfo(is) != AscendanceTemplateInfo.NONE;
    }

    public static ItemAscendanceInfo getItemAscendanceInfo(ItemStack is) {
        return AscendanceHelper.retrieveInfo(
                EntryPoint.getItemAscendanceInfoKey(),
                is,
                ItemAscendanceInfo::fromJson,
                ItemAscendanceInfo.NONE
        );
    }

    public static FakeAttunementItemInfo getFakeAttunementTemplateInfo(ItemStack is) {
        return AscendanceHelper.retrieveInfo(
                EntryPoint.getGeneratedAttunementFakeItemKey(),
                is,
                FakeAttunementItemInfo::fromJson,
                FakeAttunementItemInfo.NONE
        );
    }

    public static FakeAscendanceItemInfo getFakeAscendanceItemInfo(ItemStack is) {
        return AscendanceHelper.retrieveInfo(
                EntryPoint.getGeneratedAscendanceFakeItemKey(),
                is,
                FakeAscendanceItemInfo::fromJson,
                FakeAscendanceItemInfo.NONE
        );
    }

    public static AscendanceTemplateInfo getAscendingTemplateInfo(ItemStack is) {
        return AscendanceHelper.retrieveInfo(
                EntryPoint.getAscendanceTemplateInfoKey(),
                is,
                AscendanceTemplateInfo::fromJson,
                AscendanceTemplateInfo.NONE
        );
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

        FakeAscendanceItemInfo fi = new FakeAscendanceItemInfo(
                currentTier + 1,
                candidates,
                info.attune(),
                info.blessed(),
                AscendanceHelper.getAscendanceProbability(currentTier, info.tier())
        );

        ItemUtils.applyMeta(isx, meta -> {
            meta.getPersistentDataContainer().set(
                    EntryPoint.getGeneratedAscendanceFakeItemKey(),
                    PersistentDataType.STRING,
                    fi.toJson()
            );
        });

        return isx;
    }

    public static ItemStack generateFakeTemplateAttunementItem(AscendanceTemplateInfo info, Enchantment enchantment) {
        ItemStack isx = ItemGetter.getAscendanceTemplate(info.tier(), info.blessed(), info.attune());

        AscendanceHelper.reapplyAttunementAndProbability(isx, enchantment, AscendanceHelper.getAttunementProbability(info.tier()));
        FakeAttunementItemInfo attach = new FakeAttunementItemInfo(
                info.tier(),
                info.blessed(),
                enchantment,
                AscendanceHelper.getAttunementProbability(info.tier())
        );

        attach.attachToItem(isx);

        return isx;
    }

    public static ItemStack generateFakeTemplateBlessingItem(AscendanceTemplateInfo info) {
        ItemStack isx = ItemGetter.getAscendanceTemplate(info.tier(), info.blessed(), info.attune());

        AscendanceHelper.reapplyBlessingAndProbability(isx, AscendanceHelper.getBlessingProbability(info.tier()));
        FakeBlessingItemInfo attach = new FakeBlessingItemInfo(
                info.tier(),
                AscendanceHelper.getBlessingProbability(info.tier()),
                info.attune()
        );

        attach.attachToItem(isx);

        return isx;
    }

    /**
     * Provides a predicate that clears intermediate lore
     * @return the predicate
     */
    private static Predicate<String> clearIntermediatePredicate() {
        Predicate<String> p = str -> {
            return str.contains("Ascended ") ||
                    str.contains(ChatColor.LIGHT_PURPLE + "Blessed") ||
                    str.contains(ChatColor.AQUA + "Attuned " + ChatColor.GRAY) ||
                    str.contains(ChatColor.GRAY + "Success Rate: " + ChatColor.WHITE);
        };
        return p.or(PROBABILITY_STR).or(ATTUNEMENT_STR).or(BLESSING_STR);
    }

    public static void reapplyAscendTier(ItemStack isx, int tier) {
        if (tier <= 0) {
            ItemUtils.applyMeta(isx, meta -> {
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

                assert lore != null;

                lore.removeIf(AscendanceHelper.clearIntermediatePredicate());

                meta.setLore(lore);
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
                lore.addLast(ChatColor.AQUA + "Attuned " + ChatColor.GRAY + "(" + ItemUtils.convertToDisplayName(attune) + ")");
            }

            lore.addLast(String.format(
                    ChatColor.GRAY + "Success Rate: " + ChatColor.WHITE + "%.2f%%", probability * 100.0
            ));

            meta.setLore(lore);
        });
    }

    private static final Predicate<String> PROBABILITY_STR = str -> str.startsWith(
            ChatColor.GOLD + "Success Rate: " + ChatColor.AQUA
    );

    private static final Predicate<String> ATTUNEMENT_STR = str -> str.startsWith(
            ChatColor.GOLD + "Attuning to " + ChatColor.AQUA
    );

    private static final Predicate<String> BLESSING_STR = str -> str.startsWith(
            ChatColor.GOLD + "Blessing this template..."
    );

    public static void reapplyAttunementAndProbability(ItemStack isx, Enchantment target, double rate) {
        ItemUtils.applyMeta(isx, meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            assert lore != null;

            lore.removeIf(AscendanceHelper.clearIntermediatePredicate());

            lore.addLast(String.format(
                    ChatColor.GOLD + "Attuning to " + ChatColor.AQUA + ItemUtils.convertToDisplayName(target) + ChatColor.GOLD + "..."
            ));
            lore.addLast(String.format(
                    ChatColor.GOLD + "Success Rate: " + ChatColor.AQUA + "%.2f%%", 100 * rate
            ));

            meta.setLore(lore);
        });
    }

    public static void reapplyBlessingAndProbability(ItemStack isx, double rate) {
        ItemUtils.applyMeta(isx, meta -> {
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            assert lore != null;

            lore.removeIf(AscendanceHelper.clearIntermediatePredicate());

            lore.addLast(String.format(
                    ChatColor.GOLD + "Blessing this template..."
            ));
            lore.addLast(String.format(
                    ChatColor.GOLD + "Success Rate: " + ChatColor.AQUA + "%.2f%%", 100 * rate
            ));

            meta.setLore(lore);
        });
    }

    public static double getAttunementProbability(int templateTier) {
        return (FeatureConsts.attuneSuccessRate() / 100.0) * (FeatureConsts.attuneSuccessMultiplier(templateTier) / 100.0);
    }

    public static double getBlessingProbability(int templateTier) {
        return (FeatureConsts.blessingSuccessRate() / 100.0) * (FeatureConsts.blessingSuccessMultiplier(templateTier) / 100.0);
    }

    public static double getAscendanceProbability(int currentTier, int templateTier) {
        double baseCompletionRate = ItemUtils.getBaseSuccessChance(currentTier) / 100.0D;
        double templateMultiplier = ItemUtils.getSuccessMultiplierPercentage(templateTier) / 100.0D;

        return baseCompletionRate * templateMultiplier;
    }

    private static <T> T retrieveInfo(NamespacedKey key, ItemStack is, Function<String, T> serializer, T def) {
        if (is == null || is.getType().isAir()) return def;

        if (!is.hasItemMeta()) {
            return def;
        }

        assert is.getItemMeta() != null;

        String str = is.getItemMeta().getPersistentDataContainer().get(
                key,
                PersistentDataType.STRING
        );

        return serializer.apply(str);
    }
}
