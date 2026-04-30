package io.github.rainvaporeon.handler;

import io.github.rainvaporeon.EntryPoint;
import io.github.rainvaporeon.data.AscendanceTemplateInfo;
import io.github.rainvaporeon.data.FakeBlessingItemInfo;
import io.github.rainvaporeon.utils.AscendanceHelper;
import io.github.rainvaporeon.utils.ItemGetter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class TemplateBlessingHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onSmithingTableWork(PrepareSmithingEvent event) {
        ItemStack templateItem = event.getInventory().getItem(0);
        ItemStack craftingTool = event.getInventory().getItem(1);
        ItemStack withMaterial = event.getInventory().getItem(2);

        if (!AscendanceHelper.isValidTemplateItem(templateItem)) return;
        // onwards we are testing against a valid template
        if (withMaterial == null || withMaterial.getType() != Material.TOTEM_OF_UNDYING) {
            event.setResult(null);
            return;
        }
        if (craftingTool == null || craftingTool.getType() != Material.HEART_OF_THE_SEA) {
            event.setResult(null);
            return;
        }

        AscendanceTemplateInfo templateInfo = AscendanceHelper.getAscendingTemplateInfo(templateItem);

        if (templateInfo == AscendanceTemplateInfo.NONE) return;

        if (templateInfo.blessed()) {
            event.setResult(null);
            return;
        }

        event.setResult(
                AscendanceHelper.generateFakeTemplateBlessingItem(
                        templateInfo
                )
        );

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSmithCompletion(SmithItemEvent event) {
        ItemStack is = event.getInventory().getResult();

        if (is == null) return;

        FakeBlessingItemInfo info = this.getFakeItem(is);

        if (info == FakeBlessingItemInfo.NONE) return;

        ItemStack isx = this.performBlessing(info, event.getWhoClicked());

        event.getInventory().setResult(
                isx
        );

    }

    private ItemStack performBlessing(FakeBlessingItemInfo info, HumanEntity player) {
        if (Math.random() > info.successProbability()) {
            return failBlessing(info, player);
        }

        player.sendMessage(
                ChatColor.GREEN + "The blessing was successful!"
        );

        player.getWorld().playSound(
                player,
                Sound.BLOCK_SMITHING_TABLE_USE,
                1.0f,
                1.0f
        );

        return ItemGetter.getAscendanceTemplate(
                info.tier(),
                true,
                info.attune()
        );
    }

    private ItemStack failBlessing(FakeBlessingItemInfo info, HumanEntity player) {
        player.sendMessage(
                ChatColor.RED + "Blessing failed..."
        );
        player.sendMessage(
                ChatColor.GRAY + "Nothing was lost, except for the materials..."
        );
        return ItemGetter.getAscendanceTemplate(
                info.tier(),
                false,
                info.attune()
        );
    }

    private FakeBlessingItemInfo getFakeItem(ItemStack is) {
        if (!is.hasItemMeta()) return FakeBlessingItemInfo.NONE;

        assert is.getItemMeta() != null;

        return FakeBlessingItemInfo.fromJson(
                is.getItemMeta().getPersistentDataContainer().get(
                EntryPoint.getFakeBlessingItemInfo(),
                PersistentDataType.STRING
        ));
    }
}
