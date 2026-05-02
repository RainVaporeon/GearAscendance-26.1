package io.github.rainvaporeon.gearascendance.utils;

/**
 * Class holding feature constants.
 */
// Note: This class was created so it can be scaled easier down the road.
public class FeatureConsts {

    /**
     * Gets the maximum levels of ascendance allowed
     * @return the maximum level of ascendance that can be performed on an item
     */
    // Balance note: Limiting the maximum ascensions requires the player
    //               to pick at most this many enchantments to boost.
    public static int ascendanceCap() {
        return 3;
    }

    /**
     * Gets the maximum rerolls to perform if the random enchantment rolled
     * is not the specified attuned enchantment
     * @return the maximum rerolls to perform
     */
    // Balance note: Recall that it is easier to get a specific enchantment
    //               boosted as the ascension rises.
    public static int attuneMaxRerolls() {
        return 2;
    }

    /**
     * Gets the base success rate on attuning
     * @return the success rate
     */
    public static int attuneSuccessRate() {
        return 20;
    }

    public static int attunementXPCost() {
        return 30;
    }

    /**
     * Gets the attunement success multiplier by template tier
     * @param templateTier the template tier
     * @return the multiplier
     */
    public static int attuneSuccessMultiplier(int templateTier) {
        if (templateTier <= 0) return 0;
        return switch (templateTier) {
            case 1 -> 50;
            case 2 -> 75;
            case 3 -> 100;
            case 4 -> 150;
            case 5 -> 300;
            default -> 300 + (templateTier - 5) * 25;
        };
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
        return 40;
    }

    public static int blessingSuccessMultiplier(int templateTier) {
        if (templateTier <= 0) return 0;
        return switch (templateTier) {
            case 1 -> 0;
            case 2 -> 66;
            case 3 -> 133;
            case 4 -> 200;
            case 5 -> 250;
            default -> 250 + (templateTier - 5) * 50;
        };
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
        return switch (templateTier) {
            case 1 -> 40;
            case 2 -> 70;
            case 3 -> 100;
            case 4 -> 150;
            case 5 -> 200;
            default -> 200 + (15 * (templateTier - 4));
        };
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
        if (upgradeTier < 0) return 100;
        return switch (upgradeTier) {
            case 0 -> 50;
            case 1 -> 35;
            case 2 -> 15;
            case 3 -> 10;
            case 4 -> 5;
            default -> 2;
        };
    }
}
