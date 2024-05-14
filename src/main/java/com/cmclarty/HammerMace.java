package com.cmclarty;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HammerMace implements ModInitializer {
	public static final String MOD_ID = "hammer-mace";
    public static final Logger LOGGER = LoggerFactory.getLogger("hammer-mace");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing hammer mace");
	}
}