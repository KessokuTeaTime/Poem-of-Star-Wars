package band.kessokuteatime.poemofstarwars;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.api.ModInitializer;
import net.krlite.equator.visual.color.AccurateColor;
import net.krlite.equator.visual.color.Palette;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gl.WindowFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class PoemOfStarWars implements ModInitializer {
	public static final String NAME = "Poem o' Star Wars", ID = "poemofstarwars";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	private static IntBuffer intBuffer;
	private static int width, height;

	@Override
	public void onInitialize() {
	}

	public static void intBuffer(Framebuffer framebuffer) {
		intBuffer = toIntBuffer(framebuffer);
		width = framebuffer.textureWidth;
		height = framebuffer.textureHeight;
	}

	public static IntBuffer intBuffer() {
		return intBuffer;
	}

	public static int width() {
		return width;
	}

	public static int height() {
		return height;
	}

	public static IntBuffer toIntBuffer(Framebuffer framebuffer) {
		NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(framebuffer);
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(nativeImage.getWidth() * nativeImage.getHeight() * 4);
		for (int y = 0; y < nativeImage.getHeight(); y++) {
			for (int x = 0; x < nativeImage.getWidth(); x++) {
				byteBuffer.put(nativeImage.getRed(x, y));
				byteBuffer.put(nativeImage.getGreen(x, y));
				byteBuffer.put(nativeImage.getBlue(x, y));
				byteBuffer.put(nativeImage.getOpacity(x, y));
			}
		}
		nativeImage.close();
		return byteBuffer.flip().asIntBuffer();
	}

	public static void drawTilted(DrawContext context, IntBuffer intBuffer) {
		AccurateColor textureColor = Palette.WHITE;

		int textureId = GL11.glGenTextures();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, PoemOfStarWars.width(), PoemOfStarWars.height(),
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, intBuffer);

		RenderSystem.enableBlend();

		float width = MinecraftClient.getInstance().getWindow().getScaledWidth(), height = MinecraftClient.getInstance().getWindow().getScaledHeight();

		context.getMatrices().push();
		context.getMatrices().translate(0, height, 0);

		BufferBuilder builder = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();

		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
		RenderSystem.setShaderTexture(0, textureId);

		builder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_TEXTURE_COLOR);

		for (int y = 0; y < height; y++) {
			textureColor(
					builder, matrix,
					-width / 2,
					height / 2 + y,
					0, y / height, textureColor
			);	// Left

			textureColor(
					builder, matrix,
					width / 2,
					height / 2 + y,
					1, y / height, textureColor
			);	// Right
		}

		BufferRenderer.drawWithGlobalProgram(builder.end());
		RenderSystem.enableCull();

		context.getMatrices().pop();
	}

	private static void textureColor(BufferBuilder builder, Matrix4f matrix, float x, float y, float u, float v, AccurateColor color) {
		builder.vertex(matrix, x, y, 0).texture(u, v).color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat()).next();
	}

	private static void color(BufferBuilder builder, Matrix4f matrix, float x, float y, AccurateColor color) {
		builder.vertex(matrix, x, y, 0).color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat()).next();
	}
}
