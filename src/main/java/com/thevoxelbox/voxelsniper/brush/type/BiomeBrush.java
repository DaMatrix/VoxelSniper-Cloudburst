package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.biome.Biome;
import org.cloudburstmc.server.level.chunk.Chunk;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.registry.BiomeRegistry;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public class BiomeBrush extends AbstractBrush {

	private Biome selectedBiome = BiomeRegistry.get().getBiome(Identifier.fromString("plains"));

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		//TODO: implement this
		/*Sniper sniper = snipe.getSniper();
		Player player = sniper.getPlayer();
		String firstParameter = parameters[0];
		if (firstParameter.equalsIgnoreCase("info")) {
			player.sendMessage(TextFormat.GOLD + "Biome Brush Parameters:");
			StringBuilder availableBiomes = new StringBuilder();
			for (Biome biome : Biome.values()) {
				if (availableBiomes.length() == 0) {
					availableBiomes = new StringBuilder(TextFormat.DARK_GREEN + biome.name());
					continue;
				}
				availableBiomes.append(TextFormat.RED + ", " + TextFormat.DARK_GREEN)
					.append(biome.name());
			}
			player.sendMessage(TextFormat.DARK_BLUE + "Available biomes: " + availableBiomes);
		} else {
			// allows biome names with spaces in their name
			String biomeName = IntStream.range(2, parameters.length)
				.mapToObj(index -> " " + parameters[index])
				.collect(Collectors.joining("", firstParameter, ""));
			this.selectedBiome = Arrays.stream(Biome.values())
				.filter(biome -> biomeName.equalsIgnoreCase(biome.name()))
				.findFirst()
				.orElse(this.selectedBiome);
			player.sendMessage(TextFormat.GOLD + "Currently selected biome type: " + TextFormat.DARK_GREEN + this.selectedBiome.name());
		}*/
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		biome(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		biome(snipe);
	}

	private void biome(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize, 2);
		Level world = getLevel();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockZ = targetBlock.getZ();
		for (int x = -brushSize; x <= brushSize; x++) {
			double xSquared = Math.pow(x, 2);
			for (int z = -brushSize; z <= brushSize; z++) {
				if (xSquared + Math.pow(z, 2) <= brushSizeSquared) {
					world.setBiomeId(targetBlockX + x, targetBlockZ + z, (byte) BiomeRegistry.get().getRuntimeId(this.selectedBiome));
				}
			}
		}
		Block block1 = world.getBlock(targetBlockX - brushSize, 0, targetBlockZ - brushSize);
		Block block2 = world.getBlock(targetBlockX + brushSize, 0, targetBlockZ + brushSize);
		Chunk chunk1 = block1.getChunk();
		Chunk chunk2 = block2.getChunk();
		int block1X = block1.getX();
		int block2X = block2.getX();
		int chunk1X = chunk1.getX();
		int chunk2X = chunk2.getX();
		int block1Z = block1.getZ();
		int block2Z = block2.getZ();
		int chunk1Z = chunk1.getZ();
		int chunk2Z = chunk2.getZ();
		int lowChunkX = block1X <= block2X ? chunk1X : chunk2X;
		int lowChunkZ = block1Z <= block2Z ? chunk1Z : chunk2Z;
		int highChunkX = block1X >= block2X ? chunk1X : chunk2X;
		int highChunkZ = block1Z >= block2Z ? chunk1Z : chunk2Z;
		for (int x = lowChunkX; x <= highChunkX; x++) {
			for (int z = lowChunkZ; z <= highChunkZ; z++) {
				refreshChunk(world, x, z);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void refreshChunk(Level world, int x, int z) {
		//TODO: what is this?
		// world.refreshChunk(x, z);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.GOLD + "Currently selected biome type: " + TextFormat.DARK_GREEN + this.selectedBiome.getId().getName());
	}
}
