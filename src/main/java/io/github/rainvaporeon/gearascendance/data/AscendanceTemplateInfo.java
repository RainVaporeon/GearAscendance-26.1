package io.github.rainvaporeon.gearascendance.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.rainvaporeon.gearascendance.EntryPoint;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public record AscendanceTemplateInfo(
        int tier,
        boolean blessed,
        Enchantment attune
) implements Attachable {
    public static final AscendanceTemplateInfo NONE = new AscendanceTemplateInfo(-1, false, null);

    public String toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("tier", tier);
        jo.addProperty("blessed", blessed);
        if (attune == null) {
            jo.addProperty("attune", "null");
        } else {
            jo.addProperty("attune", String.valueOf(attune.getKeyOrNull()));
        }
        return jo.toString();
    }

    @Override
    public NamespacedKey key() {
        return EntryPoint.getAscendanceTemplateInfoKey();
    }

    public static AscendanceTemplateInfo fromItemStack(ItemStack stack) {
        if (stack == null || stack.getType().isAir() || !stack.hasItemMeta()) return NONE;
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        return AscendanceTemplateInfo.fromJson(
                meta.getPersistentDataContainer().get(
                        EntryPoint.getAscendanceTemplateInfoKey(),
                        PersistentDataType.STRING
                )
        );
    }

    public static AscendanceTemplateInfo fromJson(String json) {
        if (json == null) return NONE;
        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            String attune = jo.get("attune").getAsString();
            return new AscendanceTemplateInfo(
                    jo.get("tier").getAsInt(),
                    jo.get("blessed").getAsBoolean(),
                    attune.equals("null") ? null : Registry.ENCHANTMENT.get(NamespacedKey.fromString(jo.get("attune").getAsString()))
            );
        } catch (RuntimeException e) {
            return AscendanceTemplateInfo.NONE;
        }
    }
}
