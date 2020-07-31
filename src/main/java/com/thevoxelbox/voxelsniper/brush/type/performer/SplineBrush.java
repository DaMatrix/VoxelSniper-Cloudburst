package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.utils.TextFormat;
/**
 * FOR ANY BRUSH THAT USES A SPLINE, EXTEND THAT BRUSH FROM THIS BRUSH!!! That way, the spline calculations are already there. Also, the UI for
 * the splines will be included.
 */
public class SplineBrush extends AbstractPerformerBrush {

	private final List<Block> endPts = new ArrayList<>();
	private final List<Block> ctrlPts = new ArrayList<>();
	private List<Point> spline = new ArrayList<>();
	private boolean set;
	private boolean ctrl;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				snipe.createMessageSender()
					.message(TextFormat.GOLD + "Spline brush parameters")
					.message(TextFormat.AQUA + "ss: Enable endpoint selection mode for desired curve")
					.message(TextFormat.AQUA + "sc: Enable control point selection mode for desired curve")
					.message(TextFormat.AQUA + "clear: Clear out the curve selection")
					.message(TextFormat.AQUA + "ren: Render curve from control points")
					.send();
				return;
			} else if (parameter.equalsIgnoreCase("sc")) {
				if (this.ctrl) {
					this.ctrl = false;
					messenger.sendMessage(TextFormat.AQUA + "Control point selection mode disabled.");
				} else {
					this.set = false;
					this.ctrl = true;
					messenger.sendMessage(TextFormat.GRAY + "Control point selection mode ENABLED.");
				}
			} else if (parameter.equalsIgnoreCase("ss")) {
				if (this.set) {
					this.set = false;
					messenger.sendMessage(TextFormat.AQUA + "Endpoint selection mode disabled.");
				} else {
					this.set = true;
					this.ctrl = false;
					messenger.sendMessage(TextFormat.GRAY + "Endpoint selection mode ENABLED.");
				}
			} else if (parameter.equalsIgnoreCase("clear")) {
				clear(snipe);
			} else if (parameter.equalsIgnoreCase("ren")) {
				if (spline(new Point(this.endPts.get(0)), new Point(this.endPts.get(1)), new Point(this.ctrlPts.get(0)), new Point(this.ctrlPts.get(1)), snipe)) {
					render(snipe);
				}
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (this.set) {
			removeFromSet(snipe, true, targetBlock);
		} else if (this.ctrl) {
			removeFromSet(snipe, false, targetBlock);
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (this.set) {
			addToSet(snipe, true, targetBlock);
		}
		if (this.ctrl) {
			addToSet(snipe, false, targetBlock);
		}
	}

	private void addToSet(Snipe snipe, boolean ep, Block targetBlock) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (ep) {
			if (this.endPts.contains(targetBlock) || this.endPts.size() == 2) {
				return;
			}
			this.endPts.add(targetBlock);
			messenger.sendMessage(TextFormat.GRAY + "Added block " + TextFormat.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + TextFormat.GRAY + "to endpoint selection");
			return;
		}
		if (this.ctrlPts.contains(targetBlock) || this.ctrlPts.size() == 2) {
			return;
		}
		this.ctrlPts.add(targetBlock);
		messenger.sendMessage(TextFormat.GRAY + "Added block " + TextFormat.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + TextFormat.GRAY + "to control point selection");
	}

	private void removeFromSet(Snipe snipe, boolean ep, Block targetBlock) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (ep) {
			if (!this.endPts.contains(targetBlock)) {
				messenger.sendMessage(TextFormat.RED + "That block is not in the endpoint selection set.");
				return;
			}
			this.endPts.add(targetBlock);
			messenger.sendMessage(TextFormat.GRAY + "Removed block " + TextFormat.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + TextFormat.GRAY + "from endpoint selection");
			return;
		}
		if (!this.ctrlPts.contains(targetBlock)) {
			messenger.sendMessage(TextFormat.RED + "That block is not in the control point selection set.");
			return;
		}
		this.ctrlPts.remove(targetBlock);
		messenger.sendMessage(TextFormat.GRAY + "Removed block " + TextFormat.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock.getZ() + ") " + TextFormat.GRAY + "from control point selection");
	}

	private boolean spline(Point start, Point end, Point c1, Point c2, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		this.spline.clear();
		try {
			Point c = c1.subtract(start)
				.multiply(3);
			Point b = c2.subtract(c1)
				.multiply(3)
				.subtract(c);
			Point a = end.subtract(start)
				.subtract(c)
				.subtract(b);
			for (double t = 0.0; t < 1.0; t += 0.01) {
				int px = (int) Math.round(a.getX() * (t * t * t) + b.getX() * (t * t) + c.getX() * t + this.endPts.get(0)
					.getX());
				int py = (int) Math.round(a.getY() * (t * t * t) + b.getY() * (t * t) + c.getY() * t + this.endPts.get(0)
					.getY());
				int pz = (int) Math.round(a.getZ() * (t * t * t) + b.getZ() * (t * t) + c.getZ() * t + this.endPts.get(0)
					.getZ());
				if (!this.spline.contains(new Point(px, py, pz))) {
					this.spline.add(new Point(px, py, pz));
				}
			}
			return true;
		} catch (RuntimeException exception) {
			messenger.sendMessage(TextFormat.RED + "Not enough points selected; " + this.endPts.size() + " endpoints, " + this.ctrlPts.size() + " control points");
			return false;
		}
	}

	private void render(Snipe snipe) {
		if (this.spline.isEmpty()) {
			return;
		}
		for (Point point : this.spline) {
			this.performer.perform(clampY(point.getX(), point.getY(), point.getZ()));
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	private void clear(Snipe snipe) {
		this.spline.clear();
		this.ctrlPts.clear();
		this.endPts.clear();
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage(TextFormat.GRAY + "Bezier curve cleared.");
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessageSender messageSender = snipe.createMessageSender()
			.brushNameMessage();
		if (this.set) {
			messageSender.message(TextFormat.GRAY + "Endpoint selection mode ENABLED.");
		} else if (this.ctrl) {
			messageSender.message(TextFormat.GRAY + "Control point selection mode ENABLED.");
		} else {
			messageSender.message(TextFormat.AQUA + "No selection mode enabled.");
		}
		messageSender.send();
	}

	// Vector class for splines
	private static final class Point {

		private int x;
		private int y;
		private int z;

		private Point(Block block) {
			this.x = block.getX();
			this.y = block.getY();
			this.z = block.getZ();
		}

		private Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public Point add(Point point) {
			return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
		}

		public Point multiply(int scalar) {
			return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
		}

		public Point subtract(Point point) {
			return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
		}

		public int getX() {
			return this.x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return this.y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getZ() {
			return this.z;
		}

		public void setZ(int z) {
			this.z = z;
		}
	}
}
