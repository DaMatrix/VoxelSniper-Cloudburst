package com.thevoxelbox.voxelsniper.performer.type.ink;

import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.utils.Identifier;

public class InkMaterialNoPhysicsPerformer extends AbstractPerformer {

	private BlockState blockData;
	private Identifier replaceMaterial;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.blockData = toolkitProperties.getBlockData();
		this.replaceMaterial = toolkitProperties.getReplaceBlockType();
	}

	@Override
	public void perform(Block block) {
		if (block.getState().getType() == this.replaceMaterial) {
			Undo undo = getUndo();
			undo.put(block);
			block.set(this.blockData, true, false);
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.blockDataMessage()
			.replaceBlockTypeMessage()
			.send();
	}
}
