package io.github.rainvaporeon.gearascendance.utils;

import io.github.rainvaporeon.gearascendance.EntryPoint;

import java.util.Map;

/**
 * Class holding feature constants.
 */
// Note: This class was created so it can be scaled easier down the road.
public class FeatureConsts {
    // Configuration key start

    public static final String ASCENDANCE_CAP_KEY = "ascendance.cap";

    public static final String ATTUNE_MAX_REROLL_KEY = "attune.max_reroll";
    public static final String ATTUNE_BASE_SUCCESS_KEY = "attune.base.rate";
    public static final String ATTUNE_BASE_XP_COST_KEY = "attune.base.xp_cost";
    public static final String ATTUNE_SUCCESS_MULTIPLIER_KEY = "attune.success_multiplier";
    // pity is not implemented
    public static final String ATTUNE_PITY_ENABLED_KEY = "attune.pity.enabled";
    public static final String ATTUNE_PITY_IS_MULTIPLIER_KEY = "attune.pity.is_multiplier";
    public static final String ATTUNE_PITY_RATE_KEY = "attune.pity.rate";
    // end pity
    public static final String BLESSING_BASE_RATE_KEY = "blessing.base";
    public static final String BLESSING_SUCCESS_MULTIPLIER_KEY = "blessing.success_multiplier";

    public static final String ASCENDANCE_SUCCESS_MULTIPLIER_KEY = "ascendance.success_multiplier";
    public static final String ASCENDANCE_BASE_SUCCESS_RATE_KEY = "ascendance.base.rate";

    public static final String ASCENDANCE_FAIL_CURSE_RATE_KEY = "ascendance.fail_curse_rate";

    // Configuration key end

    /**
     * Gets the maximum levels of ascendance allowed
     * @return the maximum level of ascendance that can be performed on an item
     */
    // Balance note: Limiting the maximum ascensions requires the player
    //               to pick at most this many enchantments to boost.
    public static int ascendanceCap() {
        return EntryPoint.getInstance().getConfig().getInt(ASCENDANCE_CAP_KEY);
    }

    /**
     * Gets the maximum rerolls to perform if the random enchantment rolled
     * is not the specified attuned enchantment
     * @return the maximum rerolls to perform
     */
    // Balance note: Recall that it is easier to get a specific enchantment
    //               boosted as the ascension rises.
    public static int attuneMaxRerolls() {
        return EntryPoint.getInstance().getConfig().getInt(ATTUNE_MAX_REROLL_KEY);
    }

    /**
     * Gets the base success rate on attuning
     * @return the success rate
     */
    public static int attuneSuccessRate() {
        return EntryPoint.getInstance().getConfig().getInt(ATTUNE_BASE_SUCCESS_KEY);
    }

    public static int attunementXPCost() {
        return EntryPoint.getInstance().getConfig().getInt(ATTUNE_BASE_XP_COST_KEY);
    }

    /**
     * Gets the attunement success multiplier by template tier
     * @param templateTier the template tier
     * @return the multiplier
     */
    public static int attuneSuccessMultiplier(int templateTier) {
        if (templateTier <= 0) return 0;
        Map<String, Object> map = EntryPoint.getInstance().getConfig().getConfigurationSection(ATTUNE_BASE_SUCCESS_KEY).getValues(false);

        int basis = (int) map.getOrDefault("-10", 300);
        int step = (int) map.getOrDefault("-1", 25);

        return (int) map.getOrDefault(
                String.valueOf(templateTier),
                basis + step * (templateTier - 5)
        );
    }

    /**
     * Whether attune pity is enabled <br />
     * Attune pity is a mechanic to adjust the success rate
     * as more consecutive fail count occur on the item.
     * @return whether it is enabled
     */
    public static boolean attunePityEnabled() {
        return true;
    }

    /**
     * Whether the attune pity acts as a multiplier to the
     * base rate, or is a flat addition
     * @return {@code true} if multiplied, {@code false} if added
     */
    public static boolean isAttunePityMultiplier() {
        return false;
    }

    /**
     * Gets the attune pity
     * @param failCount consecutive fails in a row
     * @return the pity rate
     */
    public static int attunePityRate(int failCount) {
        if (!attunePityEnabled()) return 0;
        return switch (failCount) {
            case 0, 1, 2 -> 0;
            case 3 -> 3;
            case 4 -> 7;
            case 5 -> 12;
            case 6 -> 20;
            case 7 -> 31;
            case 8 -> 46;
            case 9 -> 74;
            default -> (isAttunePityMultiplier() ? 10 * failCount : 100);
        };
    }

    public static int blessingSuccessRate() {
        return EntryPoint.getInstance().getConfig().getInt(BLESSING_BASE_RATE_KEY);
    }

    public static int blessingSuccessMultiplier(int templateTier) {
        if (templateTier <= 0) return 0;
        Map<String, Object> map = EntryPoint.getInstance().getConfig().getConfigurationSection(BLESSING_SUCCESS_MULTIPLIER_KEY).getValues(false);

        int basis = (int) map.getOrDefault("-10", 250);
        int step = (int) map.getOrDefault("-1", 50);

        return (int) map.getOrDefault(
                String.valueOf(templateTier),
                basis + step * (templateTier - 5)
        );
    }

    /**
     * Gets the success rate multiplier to the ascendance template
     * @param templateTier the tier
     * @return the rate
     */
    // Balance note: Making the initial success rate pathetic may encourage
    //               players to not settle for getting more low-tier template
    //               and instead focusing on getting higher tiered templates.
    public static int successMultiplier(int templateTier) {
        if (templateTier <= 0) return 0;
        Map<String, Object> map = EntryPoint.getInstance().getConfig().getConfigurationSection(ATTUNE_SUCCESS_MULTIPLIER_KEY).getValues(false);

        int basis = (int) map.getOrDefault("-10", 200);
        int step = (int) map.getOrDefault("-1", 15);

        return (int) map.getOrDefault(
                String.valueOf(templateTier),
                basis + step * (templateTier - 4)
        );
    }

    /**
     * Gets the base success rate for upgrading from this tier to the next
     * @param upgradeTier the starting tier one is upgrading from
     * @return the rate
     */
    // Balance note: Higher ascendance level naturally points to more value
    //               associated with the item, in addition to reduction of
    //               possible enchantments to boost.
    public static int baseSuccessRate(int upgradeTier) {
        if (upgradeTier < 0) return 0;
        Map<String, Object> map = EntryPoint.getInstance().getConfig().getConfigurationSection(ASCENDANCE_BASE_SUCCESS_RATE_KEY).getValues(false);

        int def = (int) map.getOrDefault("-1", 2);

        return (int) map.getOrDefault(
                String.valueOf(upgradeTier),
                def
        );
    }

    /**
     * Gets the curse rate on failure
     * @return the rate to curse a tool if the ascension fails, default 40
     */
    public static int ascendanceFailureCurseRate() {
        int rate = EntryPoint.getInstance().getConfig().getInt(ASCENDANCE_FAIL_CURSE_RATE_KEY, 40);
        return Math.clamp(rate, 0, 100);
    }
}
