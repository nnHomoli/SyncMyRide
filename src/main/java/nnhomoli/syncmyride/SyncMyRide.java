package nnhomoli.syncmyride;

import turniplabs.halplibe.util.ConfigHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SyncMyRide implements ModInitializer{
    private final String MOD_ID = "syncmyride";
    private final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static int VehicleDelay;
	private static int DummyId;

	private void setupConfig() {
		Properties props = new Properties();
		props.setProperty("vehicle-delay","5");
		props.setProperty("dummy-id","16415");
		ConfigHandler cfg = new ConfigHandler(MOD_ID,props);

		VehicleDelay = cfg.getInt("vehicle-delay");
		DummyId = cfg.getInt("dummy-id");
	}

	public static int getVehicleDelay() {return VehicleDelay;}
	public static int getDummyId() {return DummyId;}

    @Override
    public void onInitialize() {
		setupConfig();
        LOGGER.info("SyncMyRide initialized.");
    }
}
