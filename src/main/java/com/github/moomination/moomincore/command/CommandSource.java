package com.github.moomination.moomincore.command;

import org.bukkit.command.CommandSender;

public record CommandSource(CommandSender sender, String[] args) {

}
