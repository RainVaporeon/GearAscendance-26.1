package io.github.rainvaporeon.utils;

/**
 * Class holding feature constants.
 */
// Note: This class was created so it can be scaled easier down the road.
public class FeatureConsts {

    /**
     * Gets the maximum levels of ascendance allowed
     * @return the maximum level of ascendance that can be performed on an item
     */
    public static int ascendanceCap() {
        return 3;
    }

    /**
     * Gets the maximum rerolls to perform if the random enchantment rolled
     * is not the specified attuned enchantment
     * @return the maximum rerolls to perform
     */
    public static int attuneMaxRerolls() {
        return 2;
    }

    /**
     * Gets the success rate multiplier to the ascendance template
     * @param templateTier the tier
     * @return the rate
     */
    public static int successMultiplier(int templateTier) {
        if (templateTier <= 0) return 0;
        return switch (templateTier) {
            case 1 -> 50;
            case 2 -> 65;
            case 3 -> 80;
            case 4 -> 100;
            case 5 -> 110;
            default -> 100 + (10 * (templateTier - 4));
        };
    }

    /**
     * Gets the base success rate for upgrading from this tier to the next
     * @param upgradeTier the starting tier one is upgrading from
     * @return the rate
     */
    public static int baseSuccessRate(int upgradeTier) {
        if (upgradeTier < 0) return 100;
        return switch (upgradeTier) {
            case 0 -> 80;
            case 1 -> 75;
            case 2 -> 40;
            case 3 -> 15;
            case 4 -> 5;
            default -> 0;
        };
    }
}
