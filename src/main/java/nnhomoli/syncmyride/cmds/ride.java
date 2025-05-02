package nnhomoli.syncmyride.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.server.net.command.ConsoleCommandSource;

public final class ride implements CommandManager.CommandRegistry {
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("ride")
			.requires(c -> ((CommandSource)c).hasAdmin()).then(ArgumentBuilderRequired.argument("rider", ArgumentTypeEntity.entity()).
				then(ArgumentBuilderRequired.argument("vehicle", ArgumentTypeEntity.entity()).executes(
			c -> {
				Object source = c.getSource();
				if(source instanceof ConsoleCommandSource) {
					((ConsoleCommandSource)source).sendTranslatableMessage("cmd.sorry-my-dear-console");
					return 0;
				}
				CommandSource s = (CommandSource) source;

				EntitySelector vehicle = c.getArgument("vehicle", EntitySelector.class);
				EntitySelector rider = c.getArgument("rider", EntitySelector.class);

				Entity r = rider.get(s).get(0);
				Entity v = vehicle.get(s).get(0);
				if(v == r) {
					s.getSender().sendTranslatedChatMessage("ride.same");
					return 0;
				}

				r.startRiding(v);
				return 1;
			}
		))));
	}
}
