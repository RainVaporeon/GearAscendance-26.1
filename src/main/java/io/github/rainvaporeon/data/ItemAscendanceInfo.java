package io.github.rainvaporeon.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.rainvaporeon.EntryPoint;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public record ItemAscendanceInfo(
        /*
         * The current tier of the item
         */
        int ascendanceTier,
        /*
         * All enchantments that were ascended
         */
        List<Enchantment> appliedAscendance
) implements Attachable {
    public static final ItemAscendanceInfo NONE = new ItemAscendanceInfo(0, List.of());

    @Override
    public NamespacedKey key() {
        return EntryPoint.getItemAscendanceInfoKey();
    }

    public String toJson() {
        JsonObject jo = new JsonObject();
        jo.addProperty("tier", ascendanceTier);
        JsonArray arr = new JsonArray();
        appliedAscendance.forEach(enc -> arr.add(String.valueOf(enc.getKeyOrNull())));
        jo.add("ascendance", arr);
        return jo.toString();
    }

    public static ItemAscendanceInfo fromJson(String json) {
        if (json == null) return NONE;
        try {
            JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
            List<Enchantment> enchs = new ArrayList<>();
            jo.get("ascendance").getAsJsonArray().forEach(je -> {
                String key = je.getAsString();
                if ("null".equals(key) || key.isBlank()) return;
                Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.fromString(key));
                enchs.add(ench);
            });
            return new ItemAscendanceInfo(jo.get("tier").getAsInt(), enchs);
        } catch (RuntimeException ex) {
            return NONE;
        }
    }
}
