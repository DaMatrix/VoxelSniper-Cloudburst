package com.thevoxelbox.voxelsniper.performer.type.ink;

import java.util.List;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;

public class IncludeInkPerformer extends AbstractPerformer {

	private List<BlockState> includeList;
	private BlockState blockData;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.blockData = toolkitProperties.getBlockData();
		this.includeList = toolkitProperties.getVoxelList();
	}

	@Override
	public void perform(Block block) {
		BlockState blockData = block.getState();
		if (this.includeList.contains(blockData)) {
			Undo undo = getUndo();
			undo.put(block);
			block.set(this.blockData);
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.voxelListMessage()
			.blockDataMessage()
			.send();
	}
}
