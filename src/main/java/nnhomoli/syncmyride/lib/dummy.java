package nnhomoli.syncmyride.lib;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;

import static nnhomoli.syncmyride.SyncMyRide.getDummyId;

public class dummy {
	public static EntityItem getDummy(Entity e) {
		EntityItem dummy = new EntityItem(e.world,e.x,e.y,e.z,new ItemStack(getDummyId(),1,0));
//		these are actually ignored in packets
		dummy.noPhysics = true;
		dummy.collision = false;
		return dummy;
	}
}
