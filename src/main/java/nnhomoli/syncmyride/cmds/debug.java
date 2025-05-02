package nnhomoli.syncmyride.cmds;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeLong;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.server.net.command.ConsoleCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class debug implements CommandManager.CommandRegistry {
	private final List<UUID> firstTime = new ArrayList<>();
	public void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register((ArgumentBuilderLiteral)ArgumentBuilderLiteral.literal("debugu").requires(c -> ((CommandSource)c).hasAdmin())
			.then(ArgumentBuilderRequired.argument("count", ArgumentTypeLong.longArg()).executes(
			c -> {
				Object source = c.getSource();
				if(source instanceof ConsoleCommandSource) {
					((ConsoleCommandSource)source).sendTranslatableMessage("cmd.sorry-my-dear-console");
					return 0;
				}
				CommandSource s = (CommandSource) source;

				Player p = s.getSender();
				if(!firstTime.contains(p.uuid)) {
					p.sendTranslatedChatMessage("debugu.first-time");
					firstTime.add(p.uuid);
					return 0;
				}

				Long l = c.getArgument("count",Long.class);
				for(int i=0;i<l.intValue();i++) {
					Mob a =  new MobZombie(p.world);
					Mob b = new MobSkeleton(p.world);
					a.setPos(p.x,p.y,p.z);
					b.setPos(p.x,p.y,p.z);

					p.world.entityJoinedWorld(a);
					p.world.entityJoinedWorld(b);

					b.startRiding(a);
				}

				return 1;
			}
		)));
	}
}
