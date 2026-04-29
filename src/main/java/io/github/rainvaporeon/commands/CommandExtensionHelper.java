package io.github.rainvaporeon.commands;

import org.bukkit.command.Command;

interface CommandExtensionHelper {

    String getName();

   default boolean checkCommand(Command cmd) {
        return cmd.getName().equals(this.getName());
    }
}
