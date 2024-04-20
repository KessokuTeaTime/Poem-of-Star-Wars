package band.kessokuteatime.poemofstarwars.mixin;

import band.kessokuteatime.poemofstarwars.PoemOfStarWars;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.CreditsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreditsScreen.class)
public abstract class CreditsScreenMixin {
    @Unique
    private final Framebuffer framebuffer = new SimpleFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth, MinecraftClient.getInstance().getFramebuffer().textureHeight, false, false);

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"
            )
    )
    private void beginWrite(DrawContext context, int i, int j, float f, CallbackInfo ci) {
        framebuffer.beginWrite(false);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    shift = At.Shift.AFTER
            )
    )
    private void endWrite(DrawContext context, int i, int j, float f, CallbackInfo ci) {
        framebuffer.endWrite();
        PoemOfStarWars.intBuffer(framebuffer);

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
    }
}
