package net.mitask.morechatmessages.config;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

public class ConfigObject {
    @ConfigEntry(name = "Achievement message enabled")
    public boolean advancementEnabled = true;

    @ConfigEntry(name = "Achievement message")
    public String advancementMessage = "{player} has just earned the achievement §a[{achievement}]";


    @ConfigEntry(name = "Death message enabled")
    public boolean deathEnabled = true;

    @ConfigEntry(name = "Unknown death reason message")
    public String unknownDeath = "{player} has died";
    @ConfigEntry(name = "Killed by entity message")
    public String killedByEntity = "{player} was killed by {killer}";
    @ConfigEntry(name = "Killed by player message")
    public String killedByPlayer = "{player} was killed by {killer}";
    @ConfigEntry(name = "Killed by player using item message")
    public String killedByPlayerUsingItem = "{player} was killed by {killer} using {item}";
}
