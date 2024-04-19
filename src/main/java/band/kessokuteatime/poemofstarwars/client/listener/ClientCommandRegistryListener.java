package band.kessokuteatime.poemofstarwars.client.listener;

import band.kessokuteatime.poemofstarwars.PoemOfStarWars;
import band.kessokuteatime.poemofstarwars.client.command.ShowCreditsScreenCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ClientCommandRegistryListener implements ClientCommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal(PoemOfStarWars.ID)
                .then(literal("credits").executes(new ShowCreditsScreenCommand())));
    }
}
