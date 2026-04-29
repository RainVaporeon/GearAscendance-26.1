package io.github.rainvaporeon.utils;

/**
 * Class holding feature constants.
 */
// Note: This class was created so it can be scaled easier down the road.
public class FeatureConsts {

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
