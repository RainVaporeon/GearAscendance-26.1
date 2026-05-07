package io.github.rainvaporeon.gearascendance.adapt;

import io.github.rainvaporeon.gearascendance.EntryPoint;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

// implement your stuffs here
public class PaperSpigotAdapter {

    public static Registry<Enchantment> getEnchantmentRegistry() {
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    }

    // Spigot wants key-or-throw or key-or-null; our Spigot impl uses key-or-null
    public static NamespacedKey getKey(Keyed key) {
        return key.getKey();
    }

    public static @NonNull NamespacedKey keyString(String key) {
        NamespacedKey k = NamespacedKey.fromString(key, EntryPoint.getInstance());
        if (k == null) throw new IllegalArgumentException("invalid entry: " + key);
        return k;
    }

    public static String getItemName(ItemStack stack) {
        if (!stack.hasItemMeta()) return stack.effectiveName().toString();
        return stack.getItemMeta().itemName().toString();
    }
}
