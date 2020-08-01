package com.thevoxelbox.voxelsniper.brush.type.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.entity.Entity;
import org.cloudburstmc.server.level.chunk.Chunk;
import org.cloudburstmc.server.utils.TextFormat;
public class EntityRemovalBrush extends AbstractBrush {

	private List<String> exemptions = new ArrayList<>(3);

	public EntityRemovalBrush() {
		this.exemptions.add("org.bukkit.entity.Player");
		this.exemptions.add("org.bukkit.entity.Hanging");
		this.exemptions.add("org.bukkit.entity.NPC");
	}

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String currentParam : parameters) {
			if (!currentParam.isEmpty() && (currentParam.charAt(0) == '+' || currentParam.charAt(0) == '-')) {
				boolean isAddOperation = currentParam.charAt(0) == '+';
				// +#/-# will suppress auto-prefixing
				String exemptionPattern = currentParam.startsWith("+#") || currentParam.startsWith("-#") ? currentParam.substring(2) : (currentParam.contains(".") ? currentParam.substring(1) : ".*." + currentParam.substring(1));
				if (isAddOperation) {
					this.exemptions.add(exemptionPattern);
					messenger.sendMessage(String.format("Added %s to entity exemptions list.", exemptionPattern));
				} else {
					this.exemptions.remove(exemptionPattern);
					messenger.sendMessage(String.format("Removed %s from entity exemptions list.", exemptionPattern));
				}
			}
			if (currentParam.equalsIgnoreCase("list-exemptions") || currentParam.equalsIgnoreCase("lex")) {
				for (String exemption : this.exemptions) {
					messenger.sendMessage(TextFormat.LIGHT_PURPLE + exemption);
				}
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		radialRemoval(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		radialRemoval(snipe);
	}

	private void radialRemoval(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		Chunk targetChunk = getTargetBlock().getChunk();
		int entityCount = 0;
		int chunkCount = 0;
		try {
			entityCount += removeEntities(targetChunk);
			int radius = Math.round(toolkitProperties.getBrushSize() / 16.0F);
			for (int x = targetChunk.getX() - radius; x <= targetChunk.getX() + radius; x++) {
				for (int z = targetChunk.getZ() - radius; z <= targetChunk.getZ() + radius; z++) {
					entityCount += removeEntities(getLevel().getChunk(x, z));
					chunkCount++;
				}
			}
		} catch (PatternSyntaxException exception) {
			exception.printStackTrace();
			messenger.sendMessage(TextFormat.RED + "Error in RegEx: " + TextFormat.LIGHT_PURPLE + exception.getPattern());
			messenger.sendMessage(TextFormat.RED + String.format("%s (Index: %d)", exception.getDescription(), exception.getIndex()));
		}
		messenger.sendMessage(TextFormat.GREEN + "Removed " + TextFormat.RED + entityCount + TextFormat.GREEN + " entities out of " + TextFormat.BLUE + chunkCount + TextFormat.GREEN + (chunkCount == 1 ? " chunk." : " chunks."));
	}

	private int removeEntities(Chunk chunk) {
		int entityCount = 0;
		for (Entity entity : chunk.getEntities()) {
			if (!isClassInExemptionList(entity.getClass())) {
				entity.close();
				entityCount++;
			}
		}
		return entityCount;
	}

	private boolean isClassInExemptionList(Class<? extends Entity> entityClass) {
		// Create a list of superclasses and interfaces implemented by the current entity type
		List<String> entityClassHierarchy = new ArrayList<>();
		Class<?> currentClass = entityClass;
		while (currentClass != null && !currentClass.equals(Object.class)) {
			entityClassHierarchy.add(currentClass.getCanonicalName());
			for (Class<?> interfaceClass : currentClass.getInterfaces()) {
				entityClassHierarchy.add(interfaceClass.getCanonicalName());
			}
			currentClass = currentClass.getSuperclass();
		}
		return this.exemptions.stream()
			.anyMatch(exemptionPattern -> entityClassHierarchy.stream()
				.anyMatch(typeName -> typeName.matches(exemptionPattern)));
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.message(TextFormat.GREEN + "Exemptions: " + TextFormat.LIGHT_PURPLE + String.join(", ", this.exemptions))
			.brushSizeMessage()
			.send();
	}
}
