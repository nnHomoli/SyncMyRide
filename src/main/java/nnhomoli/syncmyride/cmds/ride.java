package nnhomoli.syncmyride.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.server.net.command.ServerCommandSource;

public final class ride implements CommandManager.CommandRegistry {
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("ride").requires(c -> ((ServerCommandSource)c).hasAdmin()).then(ArgumentBuilderRequired.argument("rider", ArgumentTypeEntity.entity()).then(ArgumentBuilderRequired.argument("vehicle", ArgumentTypeEntity.entity()).executes(
			c -> {
				CommandSource s = (CommandSource) c.getSource();
				EntitySelector vehicle = c.getArgument("vehicle", EntitySelector.class);
				EntitySelector rider = c.getArgument("rider", EntitySelector.class);

				Entity r = rider.get(s).get(0);
				Entity v = vehicle.get(s).get(0);
				if(v == r) return 0;


				r.startRiding(v);
				return 1;
			}
		))));
	}
}
