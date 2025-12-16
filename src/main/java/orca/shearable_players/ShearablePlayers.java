package orca.shearable_players;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShearablePlayers implements ModInitializer {
	public static final String MOD_ID = "shearable_players";
	
	public static final orca.shearable_players.ShearablePlayersConfig CONFIG = orca.shearable_players.ShearablePlayersConfig.createAndLoad();
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {}
	
}