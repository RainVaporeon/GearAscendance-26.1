package io.github.rainvaporeon.gearascendance;

import io.github.rainvaporeon.gearascendance.utils.FeatureConsts;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

class ConfigInitializer {

    static void setup(FileConfiguration cfg) {
        cfg.set(
                "initialized", true
        );
        cfg.setComments(
                "initialized",
                List.of(
                        "Set this to false to regenerate the file; otherwise do not touch"
                )
        );

        putDefaults(
                cfg,
                FeatureConsts.ASCENDANCE_CAP_KEY,
                3
        );
        cfg.setComments(
                FeatureConsts.ASCENDANCE_CAP_KEY,
                List.of(
                        "Limits the maximum ascension an item can receive",
                        "Default: 3"
                )
        );

        cfg.createSection(
                FeatureConsts.ASCENDANCE_BASE_SUCCESS_RATE_KEY,
                new HashMap<Integer, Integer>() {{
                    put(0, 50);
                    put(1, 35);
                    put(2, 15);
                    put(3, 10);
                    put(4, 5);
                    put(-1, 2);
                    put(-10, 100);
                }}
        );
        cfg.setComments(
                FeatureConsts.ASCENDANCE_BASE_SUCCESS_RATE_KEY,
                List.of(
                        "A map of the ascendance base success rate",
                        "The key is the ascendance tier of the item",
                        "The value is the base success rate for ascension",
                        "The key '-1' denotes the default success rate for",
                        "ascendance tiers above 0. (Default 2)",
                        "The key '-10' denotes the default success rate for",
                        "ascendance tier at 0 or less (Default 100)"
                )
        );

        cfg.createSection(
                FeatureConsts.ASCENDANCE_SUCCESS_MULTIPLIER_KEY,
                new HashMap<Integer, Integer>() {{
                    put(1, 40);
                    put(2, 70);
                    put(3, 100);
                    put(4, 150);
                    put(5, 200);
                    put(-10, 200);
                    put(-1, 50);
                }}
        );
        cfg.setComments(
                FeatureConsts.ASCENDANCE_SUCCESS_MULTIPLIER_KEY,
                List.of(
                        "A map of the ascendance template multiplier",
                        "The key is the tier of the template, up to 5",
                        "The value is the base success multiplier",
                        "The key '-10' denotes the default base rate",
                        "The key '-1' denotes the default scaling rate",
                        "For example: The pairing '-10: 200, -1: 50' will result in",
                        "any tier past level 5 have a rate of 200+50x, where x is",
                        "tiers past 5 (tier=10, x=5)"
                )
        );

        putDefaults(
                cfg,
                FeatureConsts.BLESSING_BASE_RATE_KEY,
                40
        );
        cfg.setComments(
                FeatureConsts.BLESSING_BASE_RATE_KEY,
                List.of(
                        "The base success rate of blessing a template",
                        "Default: 40"
                )
        );

        cfg.createSection(
                FeatureConsts.BLESSING_SUCCESS_MULTIPLIER_KEY,
                new HashMap<Integer, Integer>() {{
                    put(1, 0);
                    put(2, 75);
                    put(3, 166);
                    put(4, 233);
                    put(5, 400);
                    put(-10, 500);
                    put(-1, 50);
                }}
        );
        cfg.setComments(
                FeatureConsts.BLESSING_SUCCESS_MULTIPLIER_KEY,
                List.of(
                        "A map of the blessing template multiplier",
                        "The key is the tier of the template, up to 5",
                        "The value is the base success multiplier",
                        "The key '-10' denotes the default base rate",
                        "The key '-1' denotes the default scaling rate",
                        "For example: The pairing '-10: 200, -1: 50' will result in",
                        "any tier past level 5 have a rate of 200+50x, where x is",
                        "tiers past 5 (tier=10, x=5)"
                )
        );

        putDefaults(
                cfg,
                FeatureConsts.ATTUNE_MAX_REROLL_KEY,
                2
        );
        cfg.setComments(
                FeatureConsts.ATTUNE_MAX_REROLL_KEY,
                List.of(
                        "The maximum reroll done for an attunement",
                        "Attuning increases the likelihood for an enchantment to be picked",
                        "by re-rolling a random selection.",
                        "Success rate is calculated by this:",
                        "1-((p-1)^x)/(p^x) where p=enchant count and x=reroll count",
                        "Default: 2"
                )
        );

        putDefaults(
                cfg,
                FeatureConsts.ATTUNE_BASE_SUCCESS_KEY,
                40
        );
        cfg.setComments(
                FeatureConsts.ATTUNE_BASE_SUCCESS_KEY,
                List.of(
                        "The base success rate for attuning",
                        "Default: 20"
                )
        );

        putDefaults(
                cfg,
                FeatureConsts.ATTUNE_BASE_XP_COST_KEY,
                30
        );
        cfg.setComments(
                FeatureConsts.ATTUNE_BASE_XP_COST_KEY,
                List.of(
                        "The flat XP cost for attuning",
                        "Default: 30"
                )
        );

        cfg.createSection(
                FeatureConsts.ATTUNE_SUCCESS_MULTIPLIER_KEY,
                new HashMap<Integer, Integer>() {{
                    put(1, 50);
                    put(2, 80);
                    put(3, 120);
                    put(4, 160);
                    put(5, 300);
                    put(-10, 300);
                    put(-1, 25);
                }}
        );
        cfg.setComments(
                FeatureConsts.ATTUNE_SUCCESS_MULTIPLIER_KEY,
                List.of(
                        "A map of the attune template multiplier",
                        "The key is the tier of the template, up to 5",
                        "The value is the base success multiplier",
                        "The key '-10' denotes the default base rate",
                        "The key '-1' denotes the default scaling rate",
                        "For example: The pairing '-10: 200, -1: 50' will result in",
                        "any tier past level 5 have a rate of 200+50x, where x is",
                        "tiers past 5 (tier=10, x=5)"
                )
        );
    }

    private static void putDefaults(FileConfiguration cfg, String key, Object value) {
        cfg.set(
                key, value
        );

        cfg.addDefault(
                key, value
        );
    }

}
