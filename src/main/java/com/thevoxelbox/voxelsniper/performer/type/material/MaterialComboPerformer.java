package com.thevoxelbox.voxelsniper.performer.type.material;

import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;

public class MaterialComboPerformer extends AbstractPerformer {

	private BlockState blockData;
	private BlockState replaceBlockData;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceBlockData = toolkitProperties.getReplaceBlockData();
	}

	@Override
	public void perform(Block block) {
		BlockState blockData = block.getState();
		if (blockData.equals(this.replaceBlockData)) {
			Undo undo = getUndo();
			undo.put(block);
			block.set(this.blockData.defaultState());
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.blockTypeMessage()
			.replaceBlockTypeMessage()
			.replaceBlockDataMessage()
			.send();
	}
}
