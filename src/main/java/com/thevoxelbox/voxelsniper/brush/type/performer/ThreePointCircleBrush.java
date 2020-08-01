package com.thevoxelbox.voxelsniper.brush.type.performer;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.nukkitx.math.vector.Vector3f;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import net.daporkchop.lib.math.primitive.PMath;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.Nullable;

public class ThreePointCircleBrush extends AbstractPerformerBrush {

	@Nullable
	private Vector3f coordinatesOne;
	@Nullable
	private Vector3f coordinatesTwo;
	@Nullable
	private Vector3f coordinatesThree;
	private Tolerance tolerance = Tolerance.DEFAULT;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		if (parameters[0].equalsIgnoreCase("info")) {
			messenger.sendMessage(TextFormat.YELLOW + "3-Point Circle Brush instructions: Select three corners with the arrow brush, then generate the Circle with the powder brush.");
			String toleranceOptions = Arrays.stream(Tolerance.values())
				.map(tolerance -> tolerance.name()
					.toLowerCase())
				.collect(Collectors.joining("|"));
			messenger.sendMessage(TextFormat.GOLD + "/b tpc " + toleranceOptions + " -- Toggle the calculations to emphasize accuracy or smoothness");
			return;
		}
		for (String s : parameters) {
			try {
				String parameter = s.toUpperCase();
				this.tolerance = Tolerance.valueOf(parameter);
				messenger.sendMessage(TextFormat.AQUA + "Brush set to " + this.tolerance.name()
					.toLowerCase() + " tolerance.");
				return;
			} catch (IllegalArgumentException exception) {
				messenger.sendMessage(TextFormat.LIGHT_PURPLE + "No such tolerance.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		Block targetBlock = getTargetBlock();
		if (this.coordinatesOne == null) {
			this.coordinatesOne = targetBlock.getPosition().toFloat();
			messenger.sendMessage(TextFormat.GRAY + "First Corner set.");
		} else if (this.coordinatesTwo == null) {
			this.coordinatesTwo = targetBlock.getPosition().toFloat();
			messenger.sendMessage(TextFormat.GRAY + "Second Corner set.");
		} else if (this.coordinatesThree == null) {
			this.coordinatesThree = targetBlock.getPosition().toFloat();
			messenger.sendMessage(TextFormat.GRAY + "Third Corner set.");
		} else {
			this.coordinatesOne = targetBlock.getPosition().toFloat();
			this.coordinatesTwo = null;
			this.coordinatesThree = null;
			messenger.sendMessage(TextFormat.GRAY + "First Corner set.");
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		if (this.coordinatesOne == null || this.coordinatesTwo == null || this.coordinatesThree == null) {
			return;
		}
		// Calculate triangle defining vectors
		Vector3f vectorOne = this.coordinatesTwo.sub(this.coordinatesOne);
		Vector3f vectorTwo = this.coordinatesThree.sub(this.coordinatesOne);
		Vector3f vectorThree = this.coordinatesThree.sub(vectorTwo);
		SnipeMessenger messenger = snipe.createMessenger();
		// Redundant data check
		if (vectorOne.length() == 0 || vectorTwo.length() == 0 || vectorThree.length() == 0/* || vectorOne.angle(vectorTwo) == 0 || vectorOne.angle(vectorThree) == 0 || vectorThree.angle(vectorTwo) == 0*/) {
			messenger.sendMessage(TextFormat.RED + "ERROR: Invalid points, try again.");
			this.coordinatesOne = null;
			this.coordinatesTwo = null;
			this.coordinatesThree = null;
			return;
		}
		// Calculate normal vector of the plane.
		Vector3f normalVector = vectorOne.cross(vectorTwo);
		// Calculate constant term of the plane.
		double planeConstant = normalVector.getX() * this.coordinatesOne.getX() + normalVector.getY() * this.coordinatesOne.getY() + normalVector.getZ() * this.coordinatesOne.getZ();
		Vector3f midpointOne = this.coordinatesOne.add(this.coordinatesTwo).mul(.5f);
		Vector3f midpointTwo = this.coordinatesOne.add(this.coordinatesThree).mul(.5f);
		// Find perpendicular vectors to two sides in the plane
		Vector3f perpendicularOne = normalVector.cross(vectorOne);
		Vector3f perpendicularTwo = normalVector.cross(vectorTwo);
		// determine value of parametric variable at intersection of two perpendicular bisectors
		Vector3f tNumerator = midpointTwo.sub(midpointOne).cross(perpendicularTwo);
		Vector3f tDenominator = perpendicularOne.cross(perpendicularTwo);
		double t = tNumerator.length() / tDenominator.length();
		// Calculate Circumcenter and Brushcenter.
		Vector3f circumcenter = perpendicularOne.mul(t).add(midpointOne);
		Vector3f brushCenter = Vector3f.from(Math.round(circumcenter.getX()), Math.round(circumcenter.getY()), Math.round(circumcenter.getZ()));
		// Calculate radius of circumcircle and determine brushsize
		double radius = circumcenter.distance(Vector3f.from(this.coordinatesOne.getX(), this.coordinatesOne.getY(), this.coordinatesOne.getZ()));
		int brushSize = PMath.ceilI(radius) + 1;
		for (int x = -brushSize; x <= brushSize; x++) {
			for (int y = -brushSize; y <= brushSize; y++) {
				for (int z = -brushSize; z <= brushSize; z++) {
					// Calculate distance from center
					double tempDistance = Math.pow(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2), 0.5);
					// gets corner-on blocks
					double cornerConstant = normalVector.getX() * (circumcenter.getX() + x) + normalVector.getY() * (circumcenter.getY() + y) + normalVector.getZ() * (circumcenter.getZ() + z);
					// gets center-on blocks
					double centerConstant = normalVector.getX() * (circumcenter.getX() + x + 0.5) + normalVector.getY() * (circumcenter.getY() + y + 0.5) + normalVector.getZ() * (circumcenter.getZ() + z + 0.5);
					// Check if point is within sphere and on plane (some tolerance given)
					if (tempDistance <= radius && (Math.abs(cornerConstant - planeConstant) < this.tolerance.getValue() || Math.abs(centerConstant - planeConstant) < this.tolerance.getValue())) {
						this.performer.perform(this.clampY(brushCenter.getFloorX() + x, brushCenter.getFloorY() + y, brushCenter.getFloorZ() + z));
					}
				}
			}
		}
		messenger.sendMessage(TextFormat.GREEN + "Done.");
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
		// Reset Brush
		this.coordinatesOne = null;
		this.coordinatesTwo = null;
		this.coordinatesThree = null;
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessageSender messageSender = snipe.createMessageSender()
			.brushNameMessage();
		switch (this.tolerance) {
			case ACCURATE:
				messageSender.message(TextFormat.GOLD + "Mode: Accurate");
				break;
			case DEFAULT:
				messageSender.message(TextFormat.GOLD + "Mode: Default");
				break;
			case SMOOTH:
				messageSender.message(TextFormat.GOLD + "Mode: Smooth");
				break;
			default:
				messageSender.message(TextFormat.GOLD + "Mode: Unknown");
				break;
		}
		messageSender.send();
	}

	private enum Tolerance {

		DEFAULT(1000),
		ACCURATE(10),
		SMOOTH(2000);

		private int value;

		Tolerance(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}
}
