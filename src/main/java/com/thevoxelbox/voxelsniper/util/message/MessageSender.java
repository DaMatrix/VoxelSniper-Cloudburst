package com.thevoxelbox.voxelsniper.util.message;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;

public class MessageSender {

	private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;

	private CommandSender sender;
	private List<String> messages = new ArrayList<>(0);

	public MessageSender(CommandSender sender) {
		this.sender = sender;
	}

	public MessageSender brushNameMessage(String brushName) {
		this.messages.add(TextFormat.AQUA + "Brush Type: " + TextFormat.LIGHT_PURPLE + brushName);
		return this;
	}

	public MessageSender performerNameMessage(String performerName) {
		this.messages.add(TextFormat.DARK_PURPLE + "Performer: " + TextFormat.DARK_GREEN + performerName);
		return this;
	}

	public MessageSender blockTypeMessage(Identifier blockType) {
		BlockState state = BlockState.get(blockType);
		this.messages.add(TextFormat.GOLD + "Voxel: " + TextFormat.RED + Server.getInstance().getLanguage().get(state.getBehavior().getDescriptionId(state)));
		return this;
	}

	public MessageSender blockDataMessage(BlockState state) {
		this.messages.add(TextFormat.BLUE + "Data Variable: " + TextFormat.DARK_RED + Server.getInstance().getLanguage().get(state.getBehavior().getDescriptionId(state)));
		return this;
	}

	public MessageSender replaceBlockTypeMessage(Identifier replaceBlockType) {
		BlockState state = BlockState.get(replaceBlockType);
		this.messages.add(TextFormat.AQUA + "Replace Material: " + TextFormat.RED + Server.getInstance().getLanguage().get(state.getBehavior().getDescriptionId(state)));
		return this;
	}

	public MessageSender replaceBlockDataMessage(BlockState state) {
		this.messages.add(TextFormat.DARK_GRAY + "Replace Data Variable: " + TextFormat.DARK_RED + Server.getInstance().getLanguage().get(state.getBehavior().getDescriptionId(state)));
		return this;
	}

	public MessageSender brushSizeMessage(int brushSize) {
		this.messages.add(TextFormat.GREEN + "Brush Size: " + TextFormat.DARK_RED + brushSize);
		if (brushSize >= BRUSH_SIZE_WARNING_THRESHOLD) {
			this.messages.add(TextFormat.RED + "WARNING: Large brush size selected!");
		}
		return this;
	}

	public MessageSender cylinderCenterMessage(int cylinderCenter) {
		this.messages.add(TextFormat.DARK_BLUE + "Brush Center: " + TextFormat.DARK_RED + cylinderCenter);
		return this;
	}

	public MessageSender voxelHeightMessage(int voxelHeight) {
		this.messages.add(TextFormat.DARK_AQUA + "Brush Height: " + TextFormat.DARK_RED + voxelHeight);
		return this;
	}

	public MessageSender voxelListMessage(List<? extends BlockState> voxelList) {
		if (voxelList.isEmpty()) {
			this.messages.add(TextFormat.DARK_GREEN + "No blocks selected!");
		}
		String message = voxelList.stream()
			.map(blockData -> Server.getInstance().getLanguage().get(blockData.getBehavior().getDescriptionId(blockData)))
			.map(dataAsString -> dataAsString + " ")
			.collect(Collectors.joining("", TextFormat.DARK_GREEN + "Block Types Selected: " + TextFormat.AQUA, ""));
		this.messages.add(message);
		return this;
	}

	public MessageSender message(String message) {
		this.messages.add(message);
		return this;
	}

	public void send() {
		this.messages.forEach(this.sender::sendMessage);
	}
}
