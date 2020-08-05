package com.thevoxelbox.voxelsniper.brush.type;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import net.daporkchop.lib.random.impl.FastPRandom;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.feature.tree.GenerationTreeSpecies;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.NotNull;

public class TreeSnipeBrush extends AbstractBrush {

	private GenerationTreeSpecies treeType = GenerationTreeSpecies.OAK;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Tree snipe brush:");
				messenger.sendMessage(TextFormat.AQUA + "/b t treetype");
				printTreeType(messenger);
				return;
			}
			try {
				this.treeType = GenerationTreeSpecies.valueOf(parameter.toUpperCase());
				printTreeType(messenger);
			} catch (IllegalArgumentException exception) {
				messenger.sendMessage(TextFormat.LIGHT_PURPLE + "No such tree type.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock().getRelative(0, getYOffset(), 0);
		single(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		single(snipe, getTargetBlock());
	}

	private void single(Snipe snipe, Block targetBlock) {
		//UndoDelegate undoDelegate = new UndoDelegate(targetBlock.getLevel());
		Block blockBelow = targetBlock.down();
		BlockState currentState = blockBelow.getState();
		//undoDelegate.setBlock(blockBelow);
		blockBelow.set(BlockState.get(BlockTypes.GRASS));
		Level world = getLevel();
		//TODO: undo delegate
		this.treeType.getDefaultGenerator().place(world, new FastPRandom(), targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
		//world.generateTree(targetBlock.getLocation(), this.treeType, undoDelegate);
		//Undo undo = undoDelegate.getUndo();
		blockBelow.set(currentState);
		//undo.put(blockBelow);
		Sniper sniper = snipe.getSniper();
		//sniper.storeUndo(undo);
	}

	private int getYOffset() {
		Block targetBlock = getTargetBlock();
		Level world = targetBlock.getLevel();
		return IntStream.range(1, (256 - 1 - targetBlock.getY()))
			.filter(i -> Materials.isEmpty(targetBlock.getRelative(0, i + 1, 0).getState().getType()))
			.findFirst()
			.orElse(0);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		printTreeType(messenger);
	}

	private void printTreeType(SnipeMessenger messenger) {
		String printout = Arrays.stream(GenerationTreeSpecies.values())
			.map(treeType -> ((treeType == this.treeType) ? TextFormat.GRAY + treeType.name()
				.toLowerCase() : TextFormat.DARK_GRAY + treeType.name()
				.toLowerCase()) + TextFormat.WHITE)
			.collect(Collectors.joining(", "));
		messenger.sendMessage(printout);
	}

	/*private static final class UndoDelegate implements BlockChangeDelegate {

		private Level targetLevel;
		private Undo currentUndo;

		private UndoDelegate(Level targetLevel) {
			this.targetLevel = targetLevel;
			this.currentUndo = new Undo();
		}

		public Undo getUndo() {
			Undo pastUndo = this.currentUndo;
			this.currentUndo = new Undo();
			return pastUndo;
		}

		public void setBlock(Block block) {
			Location location = block.getLocation();
			Block blockAtLocation = this.targetLevel.getBlockAt(location);
			this.currentUndo.put(blockAtLocation);
			BlockState blockData = block.getBlockData();
			blockAtLocation.setBlockData(blockData);
		}

		@Override
		public boolean setBlockData(int x, int y, int z, @NotNull BlockState blockData) {
			Block block = this.targetLevel.getBlockAt(x, y, z);
			this.currentUndo.put(block);
			block.setBlockData(blockData);
			return true;
		}

		@NotNull
		@Override
		public BlockState getBlockData(int x, int y, int z) {
			Block block = this.targetLevel.getBlockAt(x, y, z);
			return block.getBlockData();
		}

		@Override
		public int getHeight() {
			return this.targetLevel.getMaxHeight();
		}

		@Override
		public boolean isEmpty(int x, int y, int z) {
			Block block = this.targetLevel.getBlockAt(x, y, z);
			return Materials.isEmpty(block.getType());
		}
	}*/
}
