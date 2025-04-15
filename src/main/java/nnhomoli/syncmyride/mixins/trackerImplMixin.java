package nnhomoli.syncmyride.mixins;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.collection.IntHashMap;
import net.minecraft.server.entity.EntityTrackerEntryImpl;
import net.minecraft.server.entity.EntityTrackerImpl;
import net.minecraft.server.entity.player.PlayerServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import nnhomoli.syncmyride.lib.entryImplMethods;
import nnhomoli.syncmyride.lib.trackerImplMethods;

@Mixin(value = EntityTrackerImpl.class,remap = false)
public class trackerImplMixin implements trackerImplMethods {
	@Shadow @Final public IntHashMap<EntityTrackerEntryImpl> trackedEntityHashTable;
	@Override
	public void syncMyRide$updateVehicleForTrackedPlayersAndEntity(Entity e) {
		EntityTrackerEntryImpl entityTrackerEntry = trackedEntityHashTable.get(e.id);
		if(entityTrackerEntry != null) {
			entryImplMethods methods = (entryImplMethods) entityTrackerEntry;
			methods.syncMyRide$updateVehicleToTrackedPlayers();
			if(e instanceof PlayerServer) methods.syncMyRide$updateVehicle((PlayerServer) e);
		}
	}
}
