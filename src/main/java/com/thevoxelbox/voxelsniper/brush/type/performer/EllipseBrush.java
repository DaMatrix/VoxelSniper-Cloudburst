package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.utils.TextFormat;
public class EllipseBrush extends AbstractPerformerBrush {

	private static final double TWO_PI = (2 * Math.PI);
	private static final int SCL_MIN = 1;
	private static final int SCL_MAX = 9999;
	private static final int SCL_DEFAULT = 10;
	private static final int STEPS_MIN = 1;
	private static final int STEPS_MAX = 2000;
	private static final int STEPS_DEFAULT = 200;

	private int xscl;
	private int yscl;
	private int steps;
	private double stepSize;
	private boolean fill;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Ellipse brush parameters");
				messenger.sendMessage(TextFormat.AQUA + "x[n]: Set X size modifier to n");
				messenger.sendMessage(TextFormat.AQUA + "y[n]: Set Y size modifier to n");
				messenger.sendMessage(TextFormat.AQUA + "t[n]: Set the amount of time steps");
				messenger.sendMessage(TextFormat.AQUA + "fill: Toggles fill mode");
				return;
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
				Integer tempXScale = NumericParser.parseInteger(parameter.replace("x", ""));
				if (tempXScale == null) {
					messenger.sendMessage(TextFormat.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
					return;
				}
				if (tempXScale < SCL_MIN || tempXScale > SCL_MAX) {
					messenger.sendMessage(TextFormat.AQUA + "Invalid X scale (" + SCL_MIN + "-" + SCL_MAX + ")");
					continue;
				}
				this.xscl = tempXScale;
				messenger.sendMessage(TextFormat.AQUA + "X-scale modifier set to: " + this.xscl);
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
				Integer tempYScale = NumericParser.parseInteger(parameter.replace("y", ""));
				if (tempYScale == null) {
					messenger.sendMessage(TextFormat.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
					return;
				}
				if (tempYScale < SCL_MIN || tempYScale > SCL_MAX) {
					messenger.sendMessage(TextFormat.AQUA + "Invalid Y scale (" + SCL_MIN + "-" + SCL_MAX + ")");
					continue;
				}
				this.yscl = tempYScale;
				messenger.sendMessage(TextFormat.AQUA + "Y-scale modifier set to: " + this.yscl);
			} else if (!parameter.isEmpty() && parameter.charAt(0) == 't') {
				Integer tempSteps = NumericParser.parseInteger(parameter.replace("t", ""));
				if (tempSteps == null) {
					messenger.sendMessage(TextFormat.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
					return;
				}
				if (tempSteps < STEPS_MIN || tempSteps > STEPS_MAX) {
					messenger.sendMessage(TextFormat.AQUA + "Invalid step number (" + STEPS_MIN + "-" + STEPS_MAX + ")");
					continue;
				}
				this.steps = tempSteps;
				messenger.sendMessage(TextFormat.AQUA + "Render step number set to: " + this.steps);
			} else if (parameter.equalsIgnoreCase("fill")) {
				if (this.fill) {
					this.fill = false;
					messenger.sendMessage(TextFormat.AQUA + "Fill mode is disabled");
				} else {
					this.fill = true;
					messenger.sendMessage(TextFormat.AQUA + "Fill mode is enabled");
				}
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		execute(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		execute(snipe, lastBlock);
	}

	private void execute(Snipe snipe, Block targetBlock) {
		this.stepSize = TWO_PI / this.steps;
		if (this.fill) {
			ellipseFill(snipe, targetBlock);
		} else {
			ellipse(snipe, targetBlock);
		}
	}

	private void ellipse(Snipe snipe, Block targetBlock) {
		try {
			for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
				int x = (int) Math.round(this.xscl * Math.cos(steps));
				int y = (int) Math.round(this.yscl * Math.sin(steps));
				Block lastBlock = getLastBlock();
				Direction face = BlockHelper.getSide(getTargetBlock(), lastBlock);
				if (face != null) {
					switch (face) {
						case NORTH:
						case SOUTH:
							this.performer.perform(targetBlock.getRelative(0, x, y));
							break;
						case EAST:
						case WEST:
							this.performer.perform(targetBlock.getRelative(x, y, 0));
							break;
						case UP:
						case DOWN:
							this.performer.perform(targetBlock.getRelative(x, 0, y));
							break;
						default:
							break;
					}
				}
				if (steps >= TWO_PI) {
					break;
				}
			}
		} catch (RuntimeException exception) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Invalid target.");
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	private void ellipseFill(Snipe snipe, Block targetBlock) {
		int ix = this.xscl;
		int iy = this.yscl;
		this.performer.perform(targetBlock);
		try {
			if (ix >= iy) { // Need this unless you want weird holes
				for (iy = this.yscl; iy > 0; iy--) {
					for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
						int x = (int) Math.round(ix * Math.cos(steps));
						int y = (int) Math.round(iy * Math.sin(steps));
						Block lastBlock = getLastBlock();
						Direction face = BlockHelper.getSide(getTargetBlock(), lastBlock);
						if (face != null) {
							switch (face) {
								case NORTH:
								case SOUTH:
									this.performer.perform(targetBlock.getRelative(0, x, y));
									break;
								case EAST:
								case WEST:
									this.performer.perform(targetBlock.getRelative(x, y, 0));
									break;
								case UP:
								case DOWN:
									this.performer.perform(targetBlock.getRelative(x, 0, y));
									break;
								default:
									break;
							}
						}
						if (steps >= TWO_PI) {
							break;
						}
					}
					ix--;
				}
			} else {
				for (ix = this.xscl; ix > 0; ix--) {
					for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
						int x = (int) Math.round(ix * Math.cos(steps));
						int y = (int) Math.round(iy * Math.sin(steps));
						Block lastBlock = getLastBlock();
						Direction face = BlockHelper.getSide(getTargetBlock(), lastBlock);
						if (face != null) {
							switch (face) {
								case NORTH:
								case SOUTH:
									this.performer.perform(targetBlock.getRelative(0, x, y));
									break;
								case EAST:
								case WEST:
									this.performer.perform(targetBlock.getRelative(x, y, 0));
									break;
								case UP:
								case DOWN:
									this.performer.perform(targetBlock.getRelative(x, 0, y));
									break;
								default:
									break;
							}
						}
						if (steps >= TWO_PI) {
							break;
						}
					}
					iy--;
				}
			}
		} catch (RuntimeException exception) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Invalid target.");
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		if (this.xscl < SCL_MIN || this.xscl > SCL_MAX) {
			this.xscl = SCL_DEFAULT;
		}
		if (this.yscl < SCL_MIN || this.yscl > SCL_MAX) {
			this.yscl = SCL_DEFAULT;
		}
		if (this.steps < STEPS_MIN || this.steps > STEPS_MAX) {
			this.steps = STEPS_DEFAULT;
		}
		SnipeMessageSender messageSender = snipe.createMessageSender();
		messageSender.brushNameMessage()
			.message(TextFormat.AQUA + "X-size set to: " + TextFormat.DARK_AQUA + this.xscl)
			.message(TextFormat.AQUA + "Y-size set to: " + TextFormat.DARK_AQUA + this.yscl)
			.message(TextFormat.AQUA + "Render step number set to: " + TextFormat.DARK_AQUA + this.steps)
			.message(TextFormat.AQUA + "Fill mode is " + (this.fill ? "enabled" : "disabled"))
			.send();
	}
}
