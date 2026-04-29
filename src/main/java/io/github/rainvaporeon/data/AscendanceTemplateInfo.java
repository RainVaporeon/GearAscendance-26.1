package io.github.rainvaporeon.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

public record AscendanceTemplateInfo(
        int tier,
        boolean blessed,
        Enchantment attune
) {
    public static final AscendanceTemplateInfo NONE = new AscendanceTemplateInfo(-1, false, null);

    public String toDetailJson() {
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
