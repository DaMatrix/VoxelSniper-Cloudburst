package com.thevoxelbox.voxelsniper.sniper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.nukkitx.math.vector.Vector3i;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockSnapshot;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.level.Location;

/**
 * Holds {@link BlockState}s that can be later on used to reset those block
 * locations back to the recorded states.
 */
public class Undo {
	private Set<Vector3i> positions = new HashSet<>();
	private List<Snapshot> blockStates = new LinkedList<>();

	/**
	 * Adds a Block to the collection.
	 *
	 * @param block Block to be added
	 */
	public void put(Block block) {
		if (this.positions.add(block.getPosition())) {
			this.blockStates.add(new Snapshot(block));
		}
	}

	public boolean isEmpty() {
		return this.positions.isEmpty();
	}

	/**
	 * Get the number of blocks in the collection.
	 *
	 * @return size of the Undo collection
	 */
	public int getSize() {
		return this.positions.size();
	}

	/**
	 * Set the block states of all recorded blocks back to the state when they
	 * were inserted.
	 */
	public void undo() {
		for (Snapshot blockState : this.blockStates) {
			blockState.restore();
			//TODO: updateSpecialBlocks(blockState);
		}
	}

	/*private void updateSpecialBlocks(BlockState previousState) {
		Block block = previousState.getBlock();
		BlockState currentState = block.getState();
		if (previousState instanceof InventoryHolder && currentState instanceof InventoryHolder) {
			updateInventoryHolder((InventoryHolder) previousState, (InventoryHolder) currentState);
		}
		if (previousState instanceof Chest && currentState instanceof Chest) {
			updateChest((Chest) previousState, (Chest) currentState);
		}
		if (previousState instanceof CreatureSpawner && currentState instanceof CreatureSpawner) {
			updateCreatureSpawner((CreatureSpawner) previousState, (CreatureSpawner) currentState);
		}
		if (previousState instanceof Furnace && currentState instanceof Furnace) {
			updateFurnace((Furnace) previousState, (Furnace) currentState);
		}
		if (previousState instanceof Sign && currentState instanceof Sign) {
			updateSign((Sign) previousState, (Sign) currentState);
		}
		currentState.update();
	}

	private void updateInventoryHolder(InventoryHolder previousState, InventoryHolder currentState) {
		Inventory currentInventory = currentState.getInventory();
		Inventory previousInventory = previousState.getInventory();
		ItemStack[] previousContents = previousInventory.getContents();
		currentInventory.setContents(previousContents);
	}

	private void updateChest(Chest previousState, Chest currentState) {
		Inventory currentBlockInventory = currentState.getBlockInventory();
		Inventory previousBlockInventory = previousState.getBlockInventory();
		ItemStack[] previousBlockContents = previousBlockInventory.getContents();
		currentBlockInventory.setContents(previousBlockContents);
		currentState.update();
	}

	private void updateCreatureSpawner(CreatureSpawner previousState, CreatureSpawner currentState) {
		EntityType spawnedType = previousState.getSpawnedType();
		currentState.setSpawnedType(spawnedType);
	}

	private void updateFurnace(Furnace previousState, Furnace currentState) {
		short previousBurnTime = previousState.getBurnTime();
		currentState.setBurnTime(previousBurnTime);
		short previousCookTime = previousState.getCookTime();
		currentState.setCookTime(previousCookTime);
	}

	private void updateSign(Sign previousState, Sign currentState) {
		String[] previousLines = previousState.getLines();
		for (int index = 0; index < previousLines.length; index++) {
			String previousLine = previousLines[index];
			currentState.setLine(index, previousLine);
		}
	}*/

	private static final class Snapshot	{
		private final Block block;
		private final BlockSnapshot snapshot;

		public Snapshot(Block block)	{
			this.block = block;
			this.snapshot = block.snapshot();
		}

		public void restore()	{
			this.block.set(this.snapshot.getState());
			this.block.setExtra(this.snapshot.getExtra());
		}
	}
}
