package io.github.rainvaporeon.gearascendance.recipe;

import io.github.rainvaporeon.gearascendance.utils.ItemGetter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class CraftingRecipeHandler implements Listener {

    public static final String TEMPLATE_TIER_PREFIX = "ascendance_template";

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftingPreparation(PrepareItemCraftEvent event) {
        Recipe r = event.getRecipe();
        // not our business
        if (!(r instanceof CraftingRecipe cr)) return;
        String g = cr.getGroup();
        if (!g.contains(TEMPLATE_TIER_PREFIX)) return; // not our recipe
        CraftingInventory ci = event.getInventory();
        if (ci.getMatrix().length != 9) return; // not a 3x3
        if (!assertNoNull(ci.getMatrix())) return;

        int tier = CraftingRecipeHandler.parseTargetTier(g);

        if (tier == -1) {
            event.getInventory().setResult(null);
            return;
        }

        // replace output
        // note: since we can't really make recipes that use custom
        //       components in recipe input, this would be trivial.
        event.getInventory().setResult(ItemGetter.getAscendanceTemplate(tier));
    }

    private static boolean assertNoNull(ItemStack[] array) {
        for (ItemStack i : array) {
            if (i == null || i.getType().isAir()) return false;
        }
        return true;
    }

    private static int parseTargetTier(String group) {
        try {
            return Integer.parseInt(String.valueOf(group.charAt(group.length() - 1)));
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftingCompletion(CraftItemEvent event) {
//        Recipe r = event.getRecipe();
//        // not our business
//        if (!(r instanceof CraftingRecipe cr)) return;
//        String g = cr.getGroup();
//        if (!g.contains(TEMPLATE_TIER_PREFIX)) return; // not our recipe
//        CraftingInventory ci = event.getInventory();
//        if (ci.getMatrix().length != 9) return; // not a 3x3
//        if (!assertNoNull(ci.getMatrix())) return;
//
//        int tier = CraftingRecipeHandler.parseTargetTier(g);
//
//        if (tier == -1) {
//            event.getInventory().setResult(null);
//            return;
//        }
//
//        // replace output
//        // note: since we can't really make recipes that use custom
//        //       components in recipe input, this would be trivial.
//        if (event.isShiftClick()) {
//            event.getWhoClicked().getInventory().addItem(
//                    ItemGetter.getAscendanceTemplate(tier)
//            );
//        } else {
//            event.getView().setCursor(
//                    ItemGetter.getAscendanceTemplate(tier)
//            );
//        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCrafterCrafting(CrafterCraftEvent event) {
        String g = event.getRecipe().getGroup();
        if (g.contains(TEMPLATE_TIER_PREFIX)) {
            event.setResult(null);
            event.setCancelled(true);
        }

    }

}
