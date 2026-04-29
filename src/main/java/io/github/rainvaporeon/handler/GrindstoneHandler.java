package io.github.rainvaporeon.handler;

import io.github.rainvaporeon.data.ItemAscendanceInfo;
import io.github.rainvaporeon.utils.AscendanceHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;

public class GrindstoneHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onGrindstonePrepare(PrepareGrindstoneEvent event) {
        for (ItemStack is : event.getInventory().getStorageContents()) {
            if (AscendanceHelper.isValidTemplateItem(is) ||
            AscendanceHelper.getItemAscendanceInfo(is) != ItemAscendanceInfo.NONE) {
                event.setResult(null);
            }
        }
    }
}
