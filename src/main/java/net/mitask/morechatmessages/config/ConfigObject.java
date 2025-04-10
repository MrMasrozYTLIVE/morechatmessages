package net.mitask.morechatmessages.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class ConfigObject {
    @ConfigEntry(name = "Achievement message enabled")
    public Boolean advancementEnabled = true;
    @ConfigEntry(name = "Death message enabled")
    public Boolean deathEnabled = true;
    @ConfigEntry(name = "Join message enabled")
    public Boolean joinEnabled = true;
    @ConfigEntry(name = "Leave message enabled")
    public Boolean leaveEnabled = true;

    @ConfigEntry(name = "Join message")
    public String joinMessage = "§e{player} joined the game.";
    @ConfigEntry(name = "Leave message")
    public String leaveMessage = "§e{player} left the game.";

    @ConfigEntry(name = "Achievement message")
    public String advancementMessage = "{player} has just earned the achievement §a[{achievement}]";


    @ConfigEntry(name = "Unknown death reason message")
    public String unknownDeath = "{player} has died";
    @ConfigEntry(name = "Killed by entity message")
    public String killedByEntity = "{player} was killed by {killer}";
    @ConfigEntry(name = "Killed by player message")
    public String killedByPlayer = "{player} was killed by {killer}";
    @ConfigEntry(name = "Killed by player using item message")
    public String killedByPlayerUsingItem = "{player} was killed by {killer} using {item}";
}
