package band.kessokuteatime.poemofstarwars.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;

public class ShowCreditsScreenCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        MinecraftClient.getInstance().setScreen(new CreditsScreen(false, () -> MinecraftClient.getInstance().setScreen(screen)));

        return SINGLE_SUCCESS;
    }
}
