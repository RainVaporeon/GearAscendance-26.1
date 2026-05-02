package io.github.rainvaporeon.gearascendance.data;

import io.github.rainvaporeon.gearascendance.utils.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public interface Attachable {

    String toJson();

    NamespacedKey key();

    /**
     * Stores the attachable's information to the item
     * @param isx the item
     */
    default void attachToItem(ItemStack isx) {
        ItemUtils.applyMeta(isx, meta -> {
            meta.getPersistentDataContainer().set(key(), PersistentDataType.STRING, this.toJson());
        });
    }

    /**
     * Deletes the attachable from the item
     * @param isx the item
     * @apiNote this will delete the information associated with the key
     */
    default void detachFromItem(ItemStack isx) {
        ItemUtils.applyMeta(isx, meta -> {
            meta.getPersistentDataContainer().remove(key());
        });
    }
}
