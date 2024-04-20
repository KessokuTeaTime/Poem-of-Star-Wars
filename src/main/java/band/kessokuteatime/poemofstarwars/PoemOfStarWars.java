package band.kessokuteatime.poemofstarwars;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class PoemOfStarWars implements ModInitializer {
	public static final String NAME = "Poem o' Star Wars", ID = "poemofstarwars";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
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

	}

	private static void textureColor(BufferBuilder builder, Matrix4f matrix, float x, float y, float u, float v, AccurateColor color) {
		builder.vertex(matrix, x, y, 0).texture(u, v).color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat()).next();
	}

	private static void color(BufferBuilder builder, Matrix4f matrix, float x, float y, AccurateColor color) {
		builder.vertex(matrix, x, y, 0).color(color.redAsFloat(), color.greenAsFloat(), color.blueAsFloat(), color.opacityAsFloat()).next();
	}
}
