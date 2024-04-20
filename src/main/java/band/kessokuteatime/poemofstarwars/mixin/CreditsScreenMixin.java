package band.kessokuteatime.poemofstarwars.mixin;

import band.kessokuteatime.poemofstarwars.PoemOfStarWars;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreditsScreen.class)
public abstract class CreditsScreenMixin {
    @Shadow private float time;
    @Shadow @Final private float baseSpeed;
    @Shadow @Final private LogoDrawer logoDrawer;
    @Unique
    private Framebuffer framebuffer;

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"
            )
    )
    private void beginWrite(DrawContext context, int i, int j, float f, CallbackInfo ci) {
        framebuffer = new WindowFramebuffer(MinecraftClient.getInstance().getFramebuffer().textureWidth, MinecraftClient.getInstance().getFramebuffer().textureHeight);
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

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);
        PoemOfStarWars.drawTilted(framebuffer);
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/LogoDrawer;draw(Lnet/minecraft/client/gui/DrawContext;IFI)V"
            )
    )
    private void hideMovingLogo(LogoDrawer logoDrawer, DrawContext context, int i, float f, int j) {
        // Does nothing
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
                    shift = At.Shift.AFTER
            )
    )
    private void renderStaticLogo(DrawContext context, int i, int j, float f, CallbackInfo ci) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth(), height = MinecraftClient.getInstance().getWindow().getScaledHeight();

        float opacity = MathHelper.clamp(
                Math.min(time / baseSpeed * 0.02F, 1 - (time - 100) / baseSpeed * 0.02F),
                0, 1
        );
        float scale = 1.5F * Math.max(0, 1 - time / baseSpeed * 0.004F);

        RenderSystem.enableBlend();
        context.getMatrices().push();
        context.getMatrices().translate(width / 2.0, height / 2.0, 0);
        context.getMatrices().scale(scale, scale, scale);
        context.getMatrices().translate(-width / 2.0, -height / 2.0, 0);

        logoDrawer.draw(
                context, width,
                opacity, height / 2 - 16
        );

        context.getMatrices().pop();
    }
}
