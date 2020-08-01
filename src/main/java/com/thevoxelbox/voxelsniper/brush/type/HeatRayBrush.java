package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Random;

import com.nukkitx.math.vector.Vector3f;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.noise.filter.ScaleOctavesOffsetFilter;
import net.daporkchop.lib.random.impl.FastPRandom;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.block.behavior.BlockBehaviorLiquid;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public class HeatRayBrush extends AbstractBrush {

	private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
	private static final double REQUIRED_COBBLE_DENSITY = 0.5;
	private static final double REQUIRED_FIRE_DENSITY = -0.25;
	private static final double REQUIRED_AIR_DENSITY = 0;

	private int octaves = 5;
	private double frequency = 1;
	private double amplitude = 0.3;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String s : parameters) {
			String parameter = s.toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Heat Ray brush Parameters:");
				messenger.sendMessage(TextFormat.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
				messenger.sendMessage(TextFormat.AQUA + "/b hr amp[float] -- Amplitude parameter for the noise generator.");
				messenger.sendMessage(TextFormat.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
			}
			if (parameter.startsWith("oct")) {
				this.octaves = Integer.parseInt(parameter.replace("oct", ""));
				messenger.sendMessage(TextFormat.GREEN + "Octaves: " + this.octaves);
			} else if (parameter.startsWith("amp")) {
				this.amplitude = Double.parseDouble(parameter.replace("amp", ""));
				messenger.sendMessage(TextFormat.GREEN + "Amplitude: " + this.amplitude);
			} else if (parameter.startsWith("freq")) {
				this.frequency = Double.parseDouble(parameter.replace("freq", ""));
				messenger.sendMessage(TextFormat.GREEN + "Frequency: " + this.frequency);
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		heatRay(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		heatRay(snipe);
	}

	/**
	 * Heat Ray executer.
	 */
	public void heatRay(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		NoiseSource generator = new ScaleOctavesOffsetFilter(new PerlinNoiseEngine(new FastPRandom()), this.frequency, this.frequency, this.frequency, this.octaves, 1, 0);
		Block targetBlock = getTargetBlock();
		Vector3f targetBlockVector = targetBlock.getPosition().toFloat();
		Undo undo = new Undo();
		int brushSize = toolkitProperties.getBrushSize();
		for (int z = brushSize; z >= -brushSize; z--) {
			for (int x = brushSize; x >= -brushSize; x--) {
				for (int y = brushSize; y >= -brushSize; y--) {
					Vector3f currentLocation = targetBlockVector.add(x, y, z);
					if (currentLocation.distance(targetBlockVector) <= brushSize) {
						Block currentBlock = targetBlock.getLevel().getBlock(currentLocation);
						Identifier currentBlockType = currentBlock.getState().getType();
						if (currentBlockType == BlockTypes.CHEST) {
							continue;
						}
						if (currentBlock.getState().getBehavior() instanceof BlockBehaviorLiquid) {
							undo.put(currentBlock);
							currentBlock.set(BlockState.AIR);
							continue;
						}
						if (currentBlock.getState().getBehavior().getBurnAbility() > 0) {
							undo.put(currentBlock);
							currentBlock.set(BlockState.get(BlockTypes.FIRE));
							continue;
						}
						if (currentBlockType != BlockTypes.AIR) {
							double airDensity = generator.get(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
							double fireDensity = generator.get(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
							double cobbleDensity = generator.get(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
							double obsidianDensity = generator.get(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
							if (obsidianDensity >= REQUIRED_OBSIDIAN_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != BlockTypes.OBSIDIAN) {
									currentBlock.set(BlockState.get(BlockTypes.OBSIDIAN));
								}
							} else if (cobbleDensity >= REQUIRED_COBBLE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != BlockTypes.COBBLESTONE) {
									currentBlock.set(BlockState.get(BlockTypes.COBBLESTONE));
								}
							} else if (fireDensity >= REQUIRED_FIRE_DENSITY) {
								undo.put(currentBlock);
								if (currentBlockType != BlockTypes.FIRE) {
									currentBlock.set(BlockState.get(BlockTypes.FIRE));
								}
							} else if (airDensity >= REQUIRED_AIR_DENSITY) {
								undo.put(currentBlock);
								currentBlock.set(BlockState.AIR);
							}
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.GREEN + "Octaves: " + this.octaves);
		messenger.sendMessage(TextFormat.GREEN + "Amplitude: " + this.amplitude);
		messenger.sendMessage(TextFormat.GREEN + "Frequency: " + this.frequency);
		messenger.sendBrushSizeMessage();
	}
}
