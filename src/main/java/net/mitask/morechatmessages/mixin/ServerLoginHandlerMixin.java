package net.mitask.morechatmessages.mixin;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.mitask.morechatmessages.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginHandlerMixin {
    @Shadow private String username;

    @Redirect(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    public void replaceJoinMessage(PlayerManager instance, Packet packet) {
        ChatMessagePacket messagePacket = (ChatMessagePacket) packet;
        var config = Config.INSTANCE;

        if(!config.joinEnabled) return;

        messagePacket.chatMessage = Config.INSTANCE.joinMessage
                .replaceAll("\\{player}", username);

        instance.sendToAll(messagePacket);
    }
}
