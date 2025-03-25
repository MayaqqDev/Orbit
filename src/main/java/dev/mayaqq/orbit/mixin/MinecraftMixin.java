package dev.mayaqq.orbit.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mayaqq.orbit.screen.ControlsPassthroughScreen;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @WrapWithCondition(
            method = "setScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;setLastInputType(Lnet/minecraft/client/InputType;)V"
            )
    )
    private boolean onSetScreen(Minecraft instance, InputType lastInputType, @Local(argsOnly = true) Screen screen) {
        return !(screen instanceof ControlsPassthroughScreen);
    }

    @WrapWithCondition(
            method = "setScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;releaseAll()V"
            )
    )
    private boolean onSetScreen(@Local(argsOnly = true) Screen screen) {
        return !(screen instanceof ControlsPassthroughScreen);
    }
}
