package io.github.rainvaporeon.gearascendance.handler;

import io.github.rainvaporeon.gearascendance.data.AscendanceTemplateInfo;
import io.github.rainvaporeon.gearascendance.data.ItemAscendanceInfo;
import io.github.rainvaporeon.gearascendance.utils.AscendanceHelper;
import io.github.rainvaporeon.gearascendance.utils.FeatureConsts;
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

        // not an ascension template
        if (!AscendanceHelper.isValidTemplateItem(templateItem)) {
            return;
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
