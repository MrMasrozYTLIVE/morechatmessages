package net.mitask.morechatmessages.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.achievement.Achievement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.Stat;
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

        if(!hasAchievement) {
            String message = "%s has just earned the achievement §a[%s]".formatted(player.name, achievement.stringId);

            getMinecraftServer().playerManager.players.forEach(o -> {
                var otherOther = PlayerEntity.class.cast(o);
                otherOther.sendMessage(message);
            });
        }
    }

    @Inject(method = "onKilledBy", at = @At("HEAD"))
    public void onKilledBy(Entity adversary, CallbackInfo ci) {
        if(!((Object) this instanceof PlayerEntity player)) return;

        StringBuilder messageBuilder = new StringBuilder(player.name);

        if(adversary == null) messageBuilder.append(" has died");
        else {
            messageBuilder.append(" was killed by ");
            if(adversary instanceof PlayerEntity killer) {
                messageBuilder.append(killer.name);
                if(killer.getHand() != null) messageBuilder.append(" using ").append(killer.getHand().getItem().getTranslatedName());
            } else {
                messageBuilder.append(EntityRegistry.getId(adversary));
            }
        }

        String message = messageBuilder.toString();
        getMinecraftServer().playerManager.players.forEach(o -> {
            var otherOther = PlayerEntity.class.cast(o);
            otherOther.sendMessage(message);
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
