package net.mitask.morechatmessages.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.achievement.Achievement;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stat;
import net.mitask.morechatmessages.config.Config;
import net.mitask.morechatmessages.config.ConfigObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Mixin(ServerPlayerEntity.class)
@Environment(EnvType.SERVER)
public class ServerPlayerMixin {
    @Unique
    private static final Logger LOGGER = LogManager.getLogger();
    @Unique
    private static NbtCompound advancementsNBT = new NbtCompound();
    @Unique
    private static final File advancementsFile = new File("advancements_data.dat");

    @Unique
    private MinecraftServer getMinecraftServer() {
        return (MinecraftServer) FabricLoader.getInstance().getGameInstance();
    }

    @Inject(method = "increaseStat", at = @At("HEAD"))
    public void increaseStat(Stat stat, int amount, CallbackInfo ci) {
        if(!(stat instanceof Achievement achievement)) return;
        if(achievement.localOnly) return;

        var player = ServerPlayerEntity.class.cast(this);
        var playerNBT = advancementsNBT.getCompound(player.name);
        boolean hasAchievement = playerNBT.getBoolean(achievement.uuid);
        if(!hasAchievement) {
            playerNBT.putBoolean(achievement.uuid, true);
            advancementsNBT.put(player.name, playerNBT);

            try {
                NbtIo.writeCompressed(advancementsNBT, new FileOutputStream(advancementsFile));
            } catch (FileNotFoundException e) {
                LOGGER.error("Failed to write advancements!", e);
            }
        }

        ConfigObject config = Config.INSTANCE;
        if(!hasAchievement && config.advancementEnabled) {
            String message = config.advancementMessage
                    .replaceAll("\\{player}", player.name)
                    .replaceAll("\\{achievement}", I18n.getTranslation(achievement.stringId));


            getMinecraftServer().playerManager.players.forEach(o -> {
                var otherOther = PlayerEntity.class.cast(o);
                otherOther.sendMessage(message);
            });
        }
    }

    @Inject(method = "onKilledBy", at = @At("HEAD"))
    public void onKilledBy(Entity adversary, CallbackInfo ci) {
        if(!((Object) this instanceof PlayerEntity player)) return;

        ConfigObject config = Config.INSTANCE;
        if(!config.deathEnabled) return;

        String message = "§4[MoreChatMessages] Error happened with death message. Please report this bug to @MiTask with explanation what you did when this happened.";

        String killer = "";
        String item = "";

        if(adversary == null) message = config.unknownDeath;
        else {
            if(adversary instanceof PlayerEntity entity) {
                message = config.killedByPlayer;
                killer = entity.name;
                if(entity.getHand() != null) {
                    message = config.killedByPlayerUsingItem;
                    item = entity.getHand().getItem().getTranslatedName();
                };
            } else {
                message = config.killedByEntity;
                killer = EntityRegistry.getId(adversary);
            }
        }

        String finalMessage = message
                .replaceAll("\\{player}", player.name)
                .replaceAll("\\{killer}", killer)
                .replaceAll("\\{item}", item);
        getMinecraftServer().playerManager.players.forEach(o -> {
            var otherOther = PlayerEntity.class.cast(o);
            otherOther.sendMessage(finalMessage);
        });
    }

    static {
        try {
            if(!advancementsFile.exists()) advancementsFile.createNewFile();
            advancementsNBT = NbtIo.readCompressed(new FileInputStream(advancementsFile));
        } catch (Exception e) {
            LOGGER.error("Failed to read advancements!", e);
        }
    }
}
