package nnhomoli.syncmyride.lib;

import net.minecraft.server.entity.player.PlayerServer;

public interface entryImplMethods {
	void syncMyRide$updateVehicle(PlayerServer p);
	void syncMyRide$updateVehicleToTrackedPlayers();
	void syncMyRide$removeDummiesForTrackedPlayers();
	void syncMyRide$removeDummies(PlayerServer p);
}
