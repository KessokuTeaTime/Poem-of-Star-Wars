package band.kessokuteatime.poemofstarwars;

import band.kessokuteatime.poemofstarwars.client.listener.ClientCommandRegistryListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoemOfStarWars implements ModInitializer {
	public static final String NAME = "Poem o' Star Wars", ID = "poemofstarwars";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		ClientCommandRegistrationCallback.EVENT.register(new ClientCommandRegistryListener());
	}
}
