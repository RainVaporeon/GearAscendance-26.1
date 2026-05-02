package io.github.rainvaporeon.utils;

public class PSVM {
    static void main() {
        System.out.printf(
                "%12s | %12s | %12s\n",
                "Tool Tier", "Template Tier", "Ascension %"
        );
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j <= 5; j++) {
                System.out.printf(
                        "%12s | %12s | %12.2f%%\n",
                        i, j, AscendanceHelper.getAscendanceProbability(i, j) * 100
                );
            }
        }

        System.out.printf(
                "%12s | %12s | %12s\n",
                "Template Tier", "Blessing %", "Attuning %"
        );

        for (int i = 1; i <= 5; i++) {
            System.out.printf(
                    "%12s | %12.2f%% | %12.2f%%\n",
                    i,
                    AscendanceHelper.getBlessingProbability(i) * 100,
                    AscendanceHelper.getAttunementProbability(i) * 100
            );
        }

        System.out.printf(
                "%12s | %12s | %12s | %12s\n",
                "Enchants", "Success %", "Base %", "Improve %"
        );
        for (int i = 1; i <= 10; i++) {
            double numerator = (i - 1);
            double denominator = i;
            int exponent = FeatureConsts.attuneMaxRerolls() + 1;
            double allMiss = Math.pow(numerator, exponent) / Math.pow(denominator, exponent);
            double origin = 1 / denominator;
            double difference = (1 - allMiss) - origin;
            System.out.printf(
                    "%12s | %12.2f%% | %12.2f%% | %12.2f%%\n",
                    i,
                    (1 - allMiss) * 100.0,
                    origin * 100.0,
                    difference * 100.0
            );
        }
    }
}
