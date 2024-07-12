package band.kessokuteatime.poemofstarwars;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PoemOfStarWars implements ClientModInitializer {
	public static final String NAME = "Poem o' Star Wars", ID = "poemofstarwars";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("May the force be with you!");
	}

	public static void drawTilted(Framebuffer framebuffer) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		int width = minecraftClient.getFramebuffer().textureWidth, height = MinecraftClient.getInstance().getFramebuffer().textureHeight;
		ShaderProgram shaderProgram = minecraftClient.gameRenderer.blitScreenProgram;
		if (shaderProgram == null) return;

		RenderSystem.assertOnRenderThread();
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.disableCull();
		//RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		//RenderSystem.depthMask(false);
		RenderSystem.viewport(0, 0, width, height);
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

		Matrix4f matrix = new Matrix4f().setOrtho(0, width, height, 0, 1000.0F, 21000.0F);
		RenderSystem.setProjectionMatrix(matrix, VertexSorter.BY_Z);

		if (shaderProgram.modelViewMat != null) {
			shaderProgram.modelViewMat.set((new Matrix4f()).translation(0, 0, -11000.0F));
		}

		if (shaderProgram.projectionMat != null) {
			shaderProgram.projectionMat.set(matrix);
		}

		shaderProgram.addSampler("DiffuseSampler", framebuffer.getColorAttachment());
		shaderProgram.bind();

		/*
		Tessellator tessellator = RenderSystem.renderThreadTesselator();
		BufferBuilder builder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);

		double cameraDepth = 300, furthestDepth = 1080;
		double levitation = furthestDepth / cameraDepth * 0.85, difference = furthestDepth - cameraDepth;

		for (float y = 0; y < height; y++) {
			float factor = y / height, opacity = (float) Math.pow(Math.min(1, 2 * (1 - factor)), 2);
			Vector2d projectionLeft = perspectiveProjection(cameraDepth, -width * 0.65, y * levitation, factor * difference)
					.add(width / 2.0, 0);
			Vector2d projectionRight = perspectiveProjection(cameraDepth, width * 0.65, y * levitation, factor * difference)
					.add(width / 2.0, 0);

			whiteVertex(builder, (float) projectionLeft.x(), (float) (height - projectionLeft.y()), 0, factor, opacity);
			whiteVertex(builder, (float) projectionRight.x(), (float) (height - projectionRight.y()), 1, factor, opacity);
		}

		 */

		BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
		builder.vertex(0.0F, 0.0F, 0.0F).texture(0, 0).color(1, 1, 1, 1F);
		builder.vertex(width, 0.0F, 0.0F).texture(1, 0).color(1, 1, 1, 1F);
		builder.vertex(width, height, 0.0F).texture(1, 1).color(1, 1, 1, 1F);
		builder.vertex(0.0F, height, 0.0F).texture(0, 1).color(1, 1, 1, 1F);

		BufferRenderer.drawWithGlobalProgram(builder.end());

		shaderProgram.unbind();
		framebuffer.endRead();

		RenderSystem.depthMask(true);
	}

	private static void whiteVertex(BufferBuilder builder, float x, float y, float u, float v, float opacity) {
		builder.vertex(x, y, 0).texture(u, v).color(1, 1, 1, opacity);
	}

	private static Vector2d perspectiveProjection(double cameraDepth, double x, double y, double depth) {
		double pixelDepth = depth + cameraDepth;

		return new Vector2d(x, y).mul(cameraDepth / pixelDepth);
	}
}
