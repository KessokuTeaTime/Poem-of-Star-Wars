package band.kessokuteatime.poemofstarwars.mixin;

import band.kessokuteatime.poemofstarwars.PoemOfStarWars;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.render.*;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final
    MinecraftClient client;

    @Shadow public abstract void close();

    @Unique
    private DrawContext context;

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/toast/ToastManager;draw(Lnet/minecraft/client/gui/DrawContext;)V"
            )
    )
    private DrawContext getMatrixStack(DrawContext context) {
        this.context = context;
        return context;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"
            )
    )
    private void render(float f, long l, boolean bl, CallbackInfo ci) {
        if (client.currentScreen != null && client.currentScreen.getClass() == CreditsScreen.class) {
            context.getMatrices().push();

            PoemOfStarWars.drawTilted(context, PoemOfStarWars.intBuffer());

            /*
            BufferBuilder builder = Tessellator.getInstance().getBuffer();
            Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            builder.vertex(matrix, 0, 50, 0).color(1F, 1F, 1F, 1F).next();
            builder.vertex(matrix, 50, 50, 0).color(1F, 1F, 1F, 1F).next();
            builder.vertex(matrix, 50, 0, 0).color(1F, 1F, 1F, 1F).next();
            builder.vertex(matrix, 0, 0, 0).color(1F, 1F, 1F, 1F).next();

            BufferRenderer.drawWithGlobalProgram(builder.end());

             */

            context.getMatrices().pop();
        }
    }
}
