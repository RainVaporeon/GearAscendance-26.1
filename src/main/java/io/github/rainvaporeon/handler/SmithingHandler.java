package io.github.rainvaporeon.handler;

import io.github.rainvaporeon.data.AscendanceTemplateInfo;
import io.github.rainvaporeon.data.ItemAscendanceInfo;
import io.github.rainvaporeon.utils.AscendanceHelper;
import io.github.rainvaporeon.utils.FeatureConsts;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;

public class SmithingHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onSmithingTableWork(PrepareSmithingEvent event) {
        ItemStack templateItem = event.getInventory().getItem(0);
        ItemStack craftingTool = event.getInventory().getItem(1);
        ItemStack withMaterial = event.getInventory().getItem(2);

        if (!AscendanceHelper.isValidTemplateItem(templateItem)) {
            if (templateItem == null) return;
            // otherwise look and intercept any non-diamond tool
            if (templateItem.getType().equals(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)) {
                if (craftingTool == null) return;
                Material mat = craftingTool.getType();
                if (mat.isAir()) return;
                // don't permit non-diamond w/ netherite template
                if (!mat.name().startsWith("DIAMOND_")) {
                    event.setResult(null);
                    return;
                }
            }
        }
        // onwards we are testing against a valid template
        if (withMaterial == null) {
            event.setResult(null);
            return;
        }
        // not netherite and not totem (blessing); invalid recipe
        if (withMaterial.getType() != Material.NETHERITE_INGOT && withMaterial.getType() != Material.TOTEM_OF_UNDYING) {
            event.setResult(null);
            return;
        }
        if (craftingTool == null) {
            return;
        }


        ItemAscendanceInfo info = AscendanceHelper.getItemAscendanceInfo(craftingTool);
        AscendanceTemplateInfo templateInfo = AscendanceHelper.getAscendingTemplateInfo(templateItem);

        if (templateInfo == AscendanceTemplateInfo.NONE) return;

        if (!this.canAscend(craftingTool, info.ascendanceTier() + 1)) {
            event.setResult(null);
            return;
        }

        event.setResult(
                AscendanceHelper.generateFakeCompletionItem(
                        craftingTool,
                        info,
                        info.ascendanceTier(),
                        templateInfo
                )
        );

    }

    private boolean canAscend(ItemStack is, int toTier) {
        if (toTier > FeatureConsts.ascendanceCap()) return false;

        int currentEnchantments = (int) is.getEnchantments().values().stream().filter(i -> i != null && i > 0).count();
        if (currentEnchantments < toTier) return false; // impossible to over-tier
        return true;
    }

}
