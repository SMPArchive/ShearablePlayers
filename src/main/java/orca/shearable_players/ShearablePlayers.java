package orca.shearable_players;

import net.fabricmc.api.ModInitializer;

import orca.shearable_players.mixin.ShearsMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShearablePlayers implements ModInitializer {
	public static final String MOD_ID = "shearable_players";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {}
	
}