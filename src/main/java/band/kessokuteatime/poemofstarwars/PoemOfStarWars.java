package band.kessokuteatime.poemofstarwars;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
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

		for (float y = 0; y < height; y++) {
			float factor = y / height;

			whiteVertex(builder, width * (0.5 - factor / 2), y, 0, 1 - factor);
			whiteVertex(builder, width * (0.5 + factor / 2), y, 1, 1 - factor);
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
