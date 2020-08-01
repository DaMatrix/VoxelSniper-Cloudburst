package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public class OceanBrush extends AbstractBrush {

	private static final int WATER_LEVEL_DEFAULT = 62; // y=63 -- we are using array indices here
	private static final int WATER_LEVEL_MIN = 12;
	private static final int LOW_CUT_LEVEL = 12;

	private int waterLevel = WATER_LEVEL_DEFAULT;
	private boolean coverFloor;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (int i = 0; i < parameters.length; i++) {
			String parameter = parameters[i];
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.BLUE + "Parameters:");
				messenger.sendMessage(TextFormat.GREEN + "-wlevel #  " + TextFormat.BLUE + "--  Sets the water level (e.g. -wlevel 64)");
				messenger.sendMessage(TextFormat.GREEN + "-cfloor [y|n]  " + TextFormat.BLUE + "--  Enables or disables sea floor cover (e.g. -cfloor y) (Cover material will be your voxel material)");
			} else if (parameter.equalsIgnoreCase("-wlevel")) {
				if ((i + 1) >= parameters.length) {
					messenger.sendMessage(TextFormat.RED + "Missing parameter. Correct syntax: -wlevel [#] (e.g. -wlevel 64)");
					continue;
				}
				Integer temp = NumericParser.parseInteger(parameters[++i]);
				if (temp == null) {
					messenger.sendMessage(TextFormat.RED + String.format("Error while parsing parameter: %s", parameter));
					return;
				}
				if (temp <= WATER_LEVEL_MIN) {
					messenger.sendMessage(TextFormat.RED + "Error: Your specified water level was below 12.");
					continue;
				}
				this.waterLevel = temp - 1;
				messenger.sendMessage(TextFormat.BLUE + "Water level set to " + TextFormat.GREEN + (this.waterLevel + 1)); // +1 since we are working with 0-based array indices
			} else if (parameter.equalsIgnoreCase("-cfloor") || parameter.equalsIgnoreCase("-coverfloor")) {
				if ((i + 1) >= parameters.length) {
					messenger.sendMessage(TextFormat.RED + "Missing parameter. Correct syntax: -cfloor [y|n] (e.g. -cfloor y)");
					continue;
				}
				this.coverFloor = parameters[++i].equalsIgnoreCase("y");
				messenger.sendMessage(TextFormat.BLUE + String.format("Floor cover %s.", TextFormat.GREEN + (this.coverFloor ? "enabled" : "disabled")));
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Undo undo = new Undo();
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		oceanator(toolkitProperties, undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		handleArrowAction(snipe);
	}

	private void oceanator(ToolkitProperties toolkitProperties, Undo undo) {
		Level world = getLevel();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockZ = targetBlock.getZ();
		int brushSize = toolkitProperties.getBrushSize();
		int minX = (int) Math.floor(targetBlockX - brushSize);
		int minZ = (int) Math.floor(targetBlockZ - brushSize);
		int maxX = (int) Math.floor(targetBlockX + brushSize);
		int maxZ = (int) Math.floor(targetBlockZ + brushSize);
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				int currentHeight = getHeight(x, z);
				int wLevelDiff = currentHeight - (this.waterLevel - 1);
				int newSeaFloorLevel = Math.max((this.waterLevel - wLevelDiff), LOW_CUT_LEVEL);
				int highestY = world.getHighestBlock(x, z);
				// go down from highest Y block down to new sea floor
				for (int y = highestY; y > newSeaFloorLevel; y--) {
					Block block = world.getBlock(x, y, z);
					if (block.getState().getType() != BlockTypes.AIR) {
						undo.put(block);
						block.set(BlockState.AIR);
					}
				}
				// go down from water level to new sea level
				for (int y = this.waterLevel; y > newSeaFloorLevel; y--) {
					Block block = world.getBlock(x, y, z);
					if (block.getState().getType() != BlockTypes.WATER) {
						// do not put blocks into the undo we already put into
						if (block.getState().getType() != BlockTypes.AIR) {
							undo.put(block);
						}
						block.set(BlockState.get(BlockTypes.WATER));
					}
				}
				// cover the sea floor of required
				if (this.coverFloor && (newSeaFloorLevel < this.waterLevel)) {
					Block block = world.getBlock(x, newSeaFloorLevel, z);
					if (block.getState().getType() != toolkitProperties.getBlockType()) {
						undo.put(block);
						block.set(BlockState.get(toolkitProperties.getBlockType()));
					}
				}
			}
		}
	}

	private int getHeight(int bx, int bz) {
		Level world = getLevel();
		for (int y = world.getHighestBlock(bx, bz); y > 0; y--) {
			Block clamp = this.clampY(bx, y, bz);
			Identifier material = clamp.getState().getType();
			if (clamp.getState().getBehavior().isSolid()) {
				return y;
			}
		}
		return 0;
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.BLUE + "Water level set to " + TextFormat.GREEN + (this.waterLevel + 1)); // +1 since we are working with 0-based array indices
		messenger.sendMessage(TextFormat.BLUE + String.format("Floor cover %s.", TextFormat.GREEN + (this.coverFloor ? "enabled" : "disabled")));
	}
}
