package com.thevoxelbox.voxelsniper.sniper.snipe.message;

import java.util.List;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.Identifier;

public class SnipeMessenger {

	private ToolkitProperties toolkitProperties;
	private BrushProperties brushProperties;
	private Messenger messenger;

	public SnipeMessenger(ToolkitProperties toolkitProperties, BrushProperties brushProperties, Player player) {
		this.toolkitProperties = toolkitProperties;
		this.brushProperties = brushProperties;
		this.messenger = new Messenger(player);
	}

	public void sendBrushNameMessage() {
		String brushName = this.brushProperties.getName();
		this.messenger.sendBrushNameMessage(brushName);
	}

	public void sendBlockTypeMessage() {
		Identifier blockType = this.toolkitProperties.getBlockType();
		this.messenger.sendBlockTypeMessage(blockType);
	}

	public void sendBlockDataMessage() {
		BlockState blockData = this.toolkitProperties.getBlockData();
		this.messenger.sendBlockDataMessage(blockData);
	}

	public void sendReplaceBlockTypeMessage() {
		Identifier replaceBlockType = this.toolkitProperties.getReplaceBlockType();
		this.messenger.sendReplaceBlockTypeMessage(replaceBlockType);
	}

	public void sendReplaceBlockDataMessage() {
		BlockState replaceBlockData = this.toolkitProperties.getReplaceBlockData();
		this.messenger.sendReplaceBlockDataMessage(replaceBlockData);
	}

	public void sendBrushSizeMessage() {
		int brushSize = this.toolkitProperties.getBrushSize();
		this.messenger.sendBrushSizeMessage(brushSize);
	}

	public void sendCylinderCenterMessage() {
		int cylinderCenter = this.toolkitProperties.getCylinderCenter();
		this.messenger.sendCylinderCenterMessage(cylinderCenter);
	}

	public void sendVoxelHeightMessage() {
		int voxelHeight = this.toolkitProperties.getVoxelHeight();
		this.messenger.sendVoxelHeightMessage(voxelHeight);
	}

	public void sendVoxelListMessage() {
		List<BlockState> voxelList = this.toolkitProperties.getVoxelList();
		this.messenger.sendVoxelListMessage(voxelList);
	}

	public void sendMessage(String message) {
		this.messenger.sendMessage(message);
	}

	public Messenger getMessenger() {
		return this.messenger;
	}
}
