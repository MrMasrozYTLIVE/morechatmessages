package net.mitask.morechatmessages.config;

import net.glasslauncher.mods.gcapi3.api.ConfigRoot;

public class Config {
    @ConfigRoot(
            value = "config",
            visibleName = "Main Config"
    )
    public static final ConfigObject INSTANCE = new ConfigObject();
}