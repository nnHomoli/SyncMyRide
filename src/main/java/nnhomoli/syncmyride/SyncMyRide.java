package nnhomoli.syncmyride;

import net.minecraft.core.net.command.CommandManager;

import turniplabs.halplibe.util.ConfigHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


import nnhomoli.syncmyride.cmds.ride;

public final class SyncMyRide implements ModInitializer{
    private final String MOD_ID = "syncmyride";
    private final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static int vehicleDelay;
	private static int dummyId;

	private void setupConfig() {
		Properties props = new Properties();
		props.setProperty("vehicle-delay","5");
		props.setProperty("dummy-id","16415");
		ConfigHandler cfg = new ConfigHandler(MOD_ID,props);

		vehicleDelay = cfg.getInt("vehicle-delay");
		dummyId = cfg.getInt("dummy-id");
	}

	public static int getVehicleDelay() {return vehicleDelay;}
	public static int getDummyId() {return dummyId;}

	@Override
    public void onInitialize() {
		setupConfig();

		CommandManager.registerServerCommand(new ride());

        LOGGER.info("SyncMyRide initialized.");
    }
}
