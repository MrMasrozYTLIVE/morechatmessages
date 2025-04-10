package net.mitask.morechatmessages.mixin;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.play.ChatMessagePacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.mitask.morechatmessages.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayerHandlerMixin {
    @Shadow private ServerPlayerEntity player;

    @Redirect(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    public void replaceDisconnectMessage(PlayerManager instance, Packet packet) {
        ChatMessagePacket messagePacket = (ChatMessagePacket) packet;
        var config = Config.INSTANCE;

        if(!config.leaveEnabled) return;

        messagePacket.chatMessage = Config.INSTANCE.leaveMessage
                .replaceAll("\\{player}", player.name);

        instance.sendToAll(messagePacket);
    }

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V"))
    public void replaceOnDisconnectMessage(PlayerManager instance, Packet packet) {
        ChatMessagePacket messagePacket = (ChatMessagePacket) packet;
        var config = Config.INSTANCE;

        if(!config.leaveEnabled) return;

        messagePacket.chatMessage = Config.INSTANCE.leaveMessage
                .replaceAll("\\{player}", player.name);

        instance.sendToAll(messagePacket);
    }
}
