package com.thevoxelbox.voxelsniper.brush.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.BukkitBlockState;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.Nullable;

/**
 * Moves a selection blockPositionY a certain amount.
 */
public class MoveBrush extends AbstractBrush {

	/**
	 * Saved direction.
	 */
	private int[] moveDirections = {0, 0, 0};
	/**
	 * Saved selection.
	 */
	@Nullable
	private Selection selection;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		BrushProperties brushProperties = snipe.getBrushProperties();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + brushProperties.getName() + " Parameters:");
				messenger.sendMessage(TextFormat.AQUA + "/b mv x[int] -- set the x direction (positive => east)");
				messenger.sendMessage(TextFormat.AQUA + "/b mv y[int] -- set the y direction (positive => up)");
				messenger.sendMessage(TextFormat.AQUA + "/b mv z[int] -- set the z direction (positive => south)");
				messenger.sendMessage(TextFormat.AQUA + "/b mv reset -- reset the brush (x:0 y:0 z:0)");
				messenger.sendMessage(TextFormat.AQUA + "Use arrow and gunpowder to define two points.");
			}
			if (parameter.equalsIgnoreCase("reset")) {
				this.moveDirections[0] = 0;
				this.moveDirections[1] = 0;
				this.moveDirections[2] = 0;
				messenger.sendMessage(TextFormat.AQUA + "X direction set to: " + this.moveDirections[0]);
				messenger.sendMessage(TextFormat.AQUA + "Y direction set to: " + this.moveDirections[1]);
				messenger.sendMessage(TextFormat.AQUA + "Z direction set to: " + this.moveDirections[2]);
			}
			String parameterLowered = parameter.toLowerCase();
			if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'x') {
				this.moveDirections[0] = Integer.parseInt(parameter.substring(1));
				messenger.sendMessage(TextFormat.AQUA + "X direction set to: " + this.moveDirections[0]);
			} else if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'y') {
				this.moveDirections[1] = Integer.parseInt(parameter.substring(1));
				messenger.sendMessage(TextFormat.AQUA + "Y direction set to: " + this.moveDirections[1]);
			} else if (!parameterLowered.isEmpty() && parameterLowered.charAt(0) == 'z') {
				this.moveDirections[2] = Integer.parseInt(parameter.substring(1));
				messenger.sendMessage(TextFormat.AQUA + "Z direction set to: " + this.moveDirections[2]);
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation2(Location.from(this.getTargetBlock().getPosition(), this.getTargetBlock().getLevel()));
		messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Point 1 set.");
		try {
			if (this.selection.calculateRegion()) {
				moveSelection(snipe, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (RuntimeException exception) {
			messenger.sendMessage(exception.getMessage());
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (this.selection == null) {
			this.selection = new Selection();
		}
		this.selection.setLocation2(Location.from(this.getTargetBlock().getPosition(), this.getTargetBlock().getLevel()));
		messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Point 2 set.");
		try {
			if (this.selection.calculateRegion()) {
				this.moveSelection(snipe, this.selection, this.moveDirections);
				this.selection = null;
			}
		} catch (RuntimeException exception) {
			messenger.sendMessage(exception.getMessage());
		}
	}

	/**
	 * Moves the given selection blockPositionY the amount given in direction and saves an undo for the player.
	 */
	private void moveSelection(Snipe snipe, Selection selection, int[] direction) {
		SnipeMessenger messenger = snipe.createMessenger();
		Sniper sniper = snipe.getSniper();
		List<BukkitBlockState> blockStates = selection.getBlockStates();
		if (!blockStates.isEmpty()) {
			BukkitBlockState firstState = blockStates.get(0);
			Level world = firstState.level;
			Undo undo = new Undo();
			Selection newSelection = new Selection();
			Location movedLocation1 = selection.getLocation1();
			movedLocation1.add(direction[0], direction[1], direction[2]);
			Location movedLocation2 = selection.getLocation2();
			movedLocation2.add(direction[0], direction[1], direction[2]);
			newSelection.setLocation1(movedLocation1);
			newSelection.setLocation2(movedLocation2);
			try {
				newSelection.calculateRegion();
			} catch (RuntimeException exception) {
				messenger.sendMessage(TextFormat.LIGHT_PURPLE + "The new Selection has more blocks than the original selection. This should never happen!");
			}
			Set<Block> undoSet = blockStates.stream()
				.map(BukkitBlockState::getBlock)
				.collect(Collectors.toSet());
			newSelection.getBlockStates()
				.stream()
				.map(BukkitBlockState::getBlock)
				.forEach(undoSet::add);
			undoSet.forEach(undo::put);
			sniper.storeUndo(undo);
			blockStates.stream()
				.map(BukkitBlockState::getBlock)
				.forEach(block -> block.set(BlockState.AIR));
			for (BukkitBlockState blockState : blockStates) {
				Block affectedBlock = world.getBlock(blockState.position.getX() + direction[0], blockState.position.getY() + direction[1], blockState.position.getZ() + direction[2]);
				affectedBlock.set(blockState.state, true, !blockState.state.getBehavior().canBeReplaced());
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.BLUE + "Move selection blockPositionY " + TextFormat.GOLD + "x:" + this.moveDirections[0] + " y:" + this.moveDirections[1] + " z:" + this.moveDirections[2]);
	}

	private static class Selection {

		/**
		 * Maximum amount of Blocks allowed blockPositionY the Selection.
		 */
		private static final int MAX_BLOCK_COUNT = 5000000;
		/**
		 * Calculated BlockStates of the selection.
		 */
		private List<BukkitBlockState> blockStates = new ArrayList<>();
		private Location location1;
		private Location location2;

		/**
		 * Calculates region, then saves all Blocks as BlockState.
		 *
		 * @return boolean success.
		 * @throws RuntimeException Messages to be sent to the player.
		 */
		public boolean calculateRegion() {
			if (this.location1 != null && this.location2 != null) {
				Level world1 = this.location1.getLevel();
				Level world2 = this.location2.getLevel();
				if (world1.equals(world2)) {
					int x1 = this.location1.getFloorX();
					int x2 = this.location2.getFloorX();
					int y1 = this.location1.getFloorY();
					int y2 = this.location2.getFloorY();
					int z1 = this.location1.getFloorZ();
					int z2 = this.location2.getFloorZ();
					int lowX = Math.min(x1, x2);
					int lowY = Math.min(y1, y2);
					int lowZ = Math.min(z1, z2);
					int highX = Math.max(x1, x2);
					int highY = Math.max(y1, y2);
					int highZ = Math.max(z1, z2);
					if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > MAX_BLOCK_COUNT) {
						throw new RuntimeException(TextFormat.RED + "Selection size above hardcoded limit, please use a smaller selection.");
					}
					for (int y = lowY; y <= highY; y++) {
						for (int x = lowX; x <= highX; x++) {
							for (int z = lowZ; z <= highZ; z++) {
								Block block = world1.getBlock(x, y, z);
								this.blockStates.add(new BukkitBlockState(block));
							}
						}
					}
					return true;
				}
			}
			return false;
		}

		/**
		 * @return calculated BlockStates of defined region.
		 */
		public List<BukkitBlockState> getBlockStates() {
			return this.blockStates;
		}

		public Location getLocation1() {
			return this.location1;
		}

		public void setLocation1(Location location1) {
			this.location1 = location1;
		}

		public Location getLocation2() {
			return this.location2;
		}

		public void setLocation2(Location location2) {
			this.location2 = location2;
		}
	}
}
