package com.thevoxelbox.voxelsniper.sniper;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.event.player.PlayerInteractEvent;
import org.cloudburstmc.server.inventory.PlayerInventory;
import org.cloudburstmc.server.item.Item;
import org.cloudburstmc.server.item.ItemIds;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.Nullable;

public class Sniper {

	private static final String DEFAULT_TOOLKIT_NAME = "default";

	private UUID uuid;
	private boolean enabled = true;
	private int undoCacheSize;
	private Deque<Undo> undoList = new LinkedList<>();
	private List<Toolkit> toolkits = new ArrayList<>();

	public Sniper(UUID uuid, int undoCacheSize) {
		this.uuid = uuid;
		this.undoCacheSize = undoCacheSize;
		Toolkit defaultToolkit = createDefaultToolkit();
		this.toolkits.add(defaultToolkit);
	}

	private Toolkit createDefaultToolkit() {
		Toolkit toolkit = new Toolkit("default");
		toolkit.addToolAction(ItemIds.ARROW, ToolAction.ARROW);
		toolkit.addToolAction(ItemIds.GUNPOWDER, ToolAction.GUNPOWDER);
		return toolkit;
	}

	public Player getPlayer() {
		return Server.getInstance().getPlayer(this.uuid).orElseThrow(UnknownSniperPlayerException::new);
	}

	@Nullable
	public Toolkit getCurrentToolkit() {
		Player player = getPlayer();
		PlayerInventory inventory = player.getInventory();
		Item itemInHand = inventory.getItemInHand();
		Identifier itemType = itemInHand.getId();
		if (Materials.isEmpty(itemType)) {
			return getToolkit(DEFAULT_TOOLKIT_NAME);
		}
		return getToolkit(itemType);
	}

	public void addToolkit(Toolkit toolkit) {
		this.toolkits.add(toolkit);
	}

	@Nullable
	public Toolkit getToolkit(Identifier itemType) {
		return this.toolkits.stream()
			.filter(toolkit -> toolkit.hasToolAction(itemType))
			.findFirst()
			.orElse(null);
	}

	@Nullable
	public Toolkit getToolkit(String toolkitName) {
		return this.toolkits.stream()
			.filter(toolkit -> toolkitName.equals(toolkit.getToolkitName()))
			.findFirst()
			.orElse(null);
	}

	public void removeToolkit(Toolkit toolkit) {
		this.toolkits.remove(toolkit);
	}

	/**
	 * Sniper execution call.
	 *
	 * @param action Action player performed
	 * @param usedItem Item in hand of player
	 * @param clickedBlock Block that the player targeted/interacted with
	 * @param clickedBlockFace Face of that targeted Block
	 * @return true if command visibly processed, false otherwise.
	 */
	public boolean snipe(Player player, PlayerInteractEvent.Action action, Identifier usedItem, @Nullable Block clickedBlock, Direction clickedBlockFace) {
		Toolkit toolkit = getToolkit(usedItem);
		if (toolkit == null) {
			return false;
		}
		ToolAction toolAction = toolkit.getToolAction(usedItem);
		if (toolAction == null) {
			return false;
		}
		BrushProperties currentBrushProperties = toolkit.getCurrentBrushProperties();
		String permission = currentBrushProperties.getPermission();
		if (permission != null && !player.hasPermission(permission)) {
			player.sendMessage("You are not allowed to use this brush. You're missing the permission node '" + permission + "'");
			return false;
		}
		ToolkitProperties toolkitProperties = toolkit.getProperties();
		BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
		Block targetBlock = clickedBlock == null ? blockTracer.getTargetBlock() : clickedBlock;
		if (player.isSneaking()) {
			SnipeMessenger messenger = new SnipeMessenger(toolkitProperties, currentBrushProperties, player);
			if (action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || action == PlayerInteractEvent.Action.LEFT_CLICK_AIR) {
				if (toolAction == ToolAction.ARROW) {
					if (Materials.isEmpty(targetBlock.getState().getType())) {
						toolkitProperties.resetBlockData();
					} else {
						Identifier type = targetBlock.getState().getType();
						toolkitProperties.setBlockType(type);
					}
					messenger.sendBlockTypeMessage();
					return true;
				} else if (toolAction == ToolAction.GUNPOWDER) {
					if (Materials.isEmpty(targetBlock.getState().getType())) {
						toolkitProperties.resetBlockData();
					} else {
						BlockState blockData = targetBlock.getState();
						toolkitProperties.setBlockData(blockData);
					}
					messenger.sendBlockDataMessage();
					return true;
				}
				return false;
			} else if (action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
				if (toolAction == ToolAction.ARROW) {
					if (targetBlock == null) {
						toolkitProperties.resetReplaceBlockData();
					} else {
						Identifier type = targetBlock.getState().getType();
						toolkitProperties.setReplaceBlockType(type);
					}
					messenger.sendReplaceBlockTypeMessage();
					return true;
				} else if (toolAction == ToolAction.GUNPOWDER) {
					if (targetBlock == null) {
						toolkitProperties.resetReplaceBlockData();
					} else {
						BlockState blockData = targetBlock.getState();
						toolkitProperties.setReplaceBlockData(blockData);
					}
					messenger.sendReplaceBlockDataMessage();
					return true;
				}
				return false;
			}
			return false;
		} else {
			if (action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
				if (Materials.isEmpty(targetBlock.getState().getType())) {
					player.sendMessage(TextFormat.RED + "Snipe target block must be visible.");
					return true;
				}
				Brush currentBrush = toolkit.getCurrentBrush();
				if (currentBrush == null) {
					return false;
				}
				Snipe snipe = new Snipe(this, toolkit, toolkitProperties, currentBrushProperties, currentBrush);
				if (currentBrush instanceof PerformerBrush) {
					PerformerBrush performerBrush = (PerformerBrush) currentBrush;
					performerBrush.initialize(snipe);
				}
				Block lastBlock = clickedBlock == null ? blockTracer.getLastBlock() : clickedBlock.getSide(clickedBlockFace);
				currentBrush.perform(snipe, toolAction, targetBlock, lastBlock);
				return true;
			}
		}
		return false;
	}

	public void storeUndo(Undo undo) {
		if (this.undoCacheSize <= 0) {
			return;
		}
		if (undo.isEmpty()) {
			return;
		}
		while (this.undoList.size() >= this.undoCacheSize) {
			this.undoList.pollLast();
		}
		this.undoList.push(undo);
	}

	public void undo(CommandSender sender, int amount) {
		if (this.undoList.isEmpty()) {
			sender.sendMessage(TextFormat.GREEN + "There's nothing to undo.");
			return;
		}
		int sum = 0;
		for (int index = 0; index < amount && !this.undoList.isEmpty(); index++) {
			Undo undo = this.undoList.pop();
			undo.undo();
			sum += undo.getSize();
		}
		sender.sendMessage(TextFormat.GREEN + "Undo successful:  " + TextFormat.RED + sum + TextFormat.GREEN + " blocks have been replaced.");
	}

	public void sendInfo(CommandSender sender) {
		Toolkit toolkit = getCurrentToolkit();
		if (toolkit == null) {
			sender.sendMessage("Current toolkit: none");
			return;
		}
		sender.sendMessage("Current toolkit: " + toolkit.getToolkitName());
		BrushProperties brushProperties = toolkit.getCurrentBrushProperties();
		Brush brush = toolkit.getCurrentBrush();
		if (brush == null) {
			sender.sendMessage("No brush selected.");
			return;
		}
		ToolkitProperties toolkitProperties = toolkit.getProperties();
		Snipe snipe = new Snipe(this, toolkit, toolkitProperties, brushProperties, brush);
		brush.sendInfo(snipe);
		if (brush instanceof PerformerBrush) {
			PerformerBrush performer = (PerformerBrush) brush;
			performer.sendPerformerInfo(snipe);
		}
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
