package com.thevoxelbox.voxelsniper.util.message;

import java.util.List;
import java.util.stream.Collectors;

import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;

public class Messenger {

	private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;

	private CommandSender sender;

	public Messenger(CommandSender sender) {
		this.sender = sender;
	}

	public void sendBrushNameMessage(String brushName) {
		sendMessage(TextFormat.AQUA + "Brush Type: " + TextFormat.LIGHT_PURPLE + brushName);
	}

	public void sendPerformerNameMessage(String performerName) {
		sendMessage(TextFormat.DARK_PURPLE + "Performer: " + TextFormat.DARK_GREEN + performerName);
	}

	public void sendBlockTypeMessage(Identifier blockType) {
		BlockState state = BlockState.get(blockType);
		sendMessage(TextFormat.GOLD + "Block Type: " + TextFormat.RED + state);
		new RuntimeException().printStackTrace();
	}

	public void sendBlockDataMessage(BlockState state) {
		sendMessage(TextFormat.BLUE + "Block State: " + TextFormat.DARK_RED + state);
	}

	public void sendReplaceBlockTypeMessage(Identifier replaceBlockType) {
		BlockState state = BlockState.get(replaceBlockType);
		sendMessage(TextFormat.AQUA + "Replace Block Type: " + TextFormat.RED + state);
		new RuntimeException().printStackTrace();
	}

	public void sendReplaceBlockDataMessage(BlockState state) {
		sendMessage(TextFormat.DARK_GRAY + "Replace Block State: " + TextFormat.DARK_RED + state);
	}

	public void sendBrushSizeMessage(int brushSize) {
		sendMessage(TextFormat.GREEN + "Brush Size: " + TextFormat.DARK_RED + brushSize);
		if (brushSize >= BRUSH_SIZE_WARNING_THRESHOLD) {
			sendMessage(TextFormat.RED + "WARNING: Large brush size selected!");
		}
	}

	public void sendCylinderCenterMessage(int cylinderCenter) {
		sendMessage(TextFormat.DARK_BLUE + "Brush Center: " + TextFormat.DARK_RED + cylinderCenter);
	}

	public void sendVoxelHeightMessage(int voxelHeight) {
		sendMessage(TextFormat.DARK_AQUA + "Brush Height: " + TextFormat.DARK_RED + voxelHeight);
	}

	public void sendVoxelListMessage(List<? extends BlockState> voxelList) {
		if (voxelList.isEmpty()) {
			sendMessage(TextFormat.DARK_GREEN + "No blocks selected!");
		}
		String message = voxelList.stream()
			.map(state -> state)
			.map(dataAsString -> dataAsString + " ")
			.collect(Collectors.joining("", TextFormat.DARK_GREEN + "Block Types Selected: " + TextFormat.AQUA, ""));
		sendMessage(message);
	}

	public void sendMessage(String message) {
		this.sender.sendMessage(message);
	}
}
