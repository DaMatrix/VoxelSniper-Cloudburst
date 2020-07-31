package com.thevoxelbox.voxelsniper.brush.type.entity;

import java.util.Arrays;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.utils.TextFormat;
public class EntityBrush extends AbstractBrush {

	private EntityType entityType = EntityType.ZOMBIE;

	@SuppressWarnings("deprecation")
	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(TextFormat.BLUE + "The available entity types are as follows:");
			String names = Arrays.stream(EntityType.values())
				.map(currentEntity -> TextFormat.AQUA + " | " + TextFormat.DARK_GREEN + currentEntity.getName())
				.collect(Collectors.joining("", "", TextFormat.AQUA + " |"));
			messenger.sendMessage(names);
		} else {
			EntityType currentEntity = EntityType.fromName(parameters[1]);
			if (currentEntity != null) {
				this.entityType = currentEntity;
				messenger.sendMessage(TextFormat.GREEN + "Entity type set to " + this.entityType.getName());
			} else {
				messenger.sendMessage(TextFormat.RED + "This is not a valid entity!");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		spawn(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		spawn(snipe);
	}

	private void spawn(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		SnipeMessenger messenger = snipe.createMessenger();
		for (int x = 0; x < toolkitProperties.getBrushSize(); x++) {
			try {
				World world = getWorld();
				Block lastBlock = getLastBlock();
				Class<? extends Entity> entityClass = this.entityType.getEntityClass();
				if (entityClass == null) {
					return;
				}
				world.spawn(lastBlock.getLocation(), entityClass);
			} catch (IllegalArgumentException exception) {
				messenger.sendMessage(TextFormat.RED + "Cannot spawn entity!");
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.message(TextFormat.LIGHT_PURPLE + "Entity brush" + " (" + this.entityType.getName() + ")")
			.brushSizeMessage()
			.send();
	}
}
