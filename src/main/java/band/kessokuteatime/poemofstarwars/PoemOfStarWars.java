package band.kessokuteatime.poemofstarwars;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoemOfStarWars implements ModInitializer {
	public static final String NAME = "Poem o' Star Wars", ID = "poemofstarwars";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
	}

	public static void drawTilted(Framebuffer framebuffer) {
		int width = MinecraftClient.getInstance().getFramebuffer().textureWidth, height = MinecraftClient.getInstance().getFramebuffer().textureHeight;

		RenderSystem.assertOnRenderThread();
		RenderSystem.colorMask(true, true, true, false);
		RenderSystem.disableCull();
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.viewport(0, 0, width, height);

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		ShaderProgram shaderProgram = minecraftClient.gameRenderer.blitScreenProgram;
		shaderProgram.addSampler("DiffuseSampler", framebuffer.getColorAttachment());
		
		Matrix4f matrix = new Matrix4f().setOrtho(0, width, height, 0, 1000.0F, 3000.0F);
		RenderSystem.setProjectionMatrix(matrix, VertexSorter.BY_Z);
		
		if (shaderProgram.modelViewMat != null) {
			shaderProgram.modelViewMat.set((new Matrix4f()).translation(0, 0, -2000.0F));
		}

		if (shaderProgram.projectionMat != null) {
			shaderProgram.projectionMat.set(matrix);
		}

		shaderProgram.bind();
		
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder builder = tessellator.getBuffer();
		builder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);

		for (float x = 0; x < width; x++) {
			float factor = x / width, threshold = 0.45F;
			whiteVertex(builder, width * factor, height, factor, 0);
			whiteVertex(builder, width * (threshold + factor * (1 - 2 * threshold)), height * 0.2, factor, 1);
		}

		BufferRenderer.draw(builder.end());

		shaderProgram.unbind();

		RenderSystem.depthMask(true);
		RenderSystem.colorMask(true, true, true, true);
	}

	private static void whiteVertex(BufferBuilder builder, double x, double y, float u, float v) {
		builder.vertex(x, y, 0).texture(u, v).color(255, 255, 255, 255).next();
	}
}
