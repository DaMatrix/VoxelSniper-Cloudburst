package com.thevoxelbox.voxelsniper.brush.type;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.utils.TextFormat;
public class SignOverwriteBrush extends AbstractBrush {

	private static final int MAX_SIGN_LINE_LENGTH = 15;
	private static final int NUM_SIGN_LINES = 4;
	// these are no array indices
	private static final int SIGN_LINE_1 = 1;
	private static final int SIGN_LINE_2 = 2;
	private static final int SIGN_LINE_3 = 3;
	private static final int SIGN_LINE_4 = 4;

	private File pluginDataFolder;
	private String[] signTextLines = new String[NUM_SIGN_LINES];
	private boolean[] signLinesEnabled = new boolean[NUM_SIGN_LINES];
	private boolean rangedMode;

	public SignOverwriteBrush(File pluginDataFolder) {
		this.pluginDataFolder = pluginDataFolder;
		clearBuffer();
		resetStates();
	}

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		boolean textChanged = false;
		for (int index = 0; index < parameters.length; index++) {
			String parameter = parameters[index];
			try {
				if (parameter.equalsIgnoreCase("info")) {
					snipe.createMessageSender()
						.message(TextFormat.AQUA + "Sign Overwrite Brush Powder/Arrow:")
						.message(TextFormat.BLUE + "The arrow writes the internal line buffer to the tearget sign.")
						.message(TextFormat.BLUE + "The powder reads the text of the target sign into the internal buffer.")
						.message(TextFormat.AQUA + "Sign Overwrite Brush Parameters:")
						.message(TextFormat.GREEN + "-1[:(enabled|disabled)] ... " + TextFormat.BLUE + "-- Sets the text of the first sign line. (e.g. -1 Blah Blah)")
						.message(TextFormat.GREEN + "-2[:(enabled|disabled)] ... " + TextFormat.BLUE + "-- Sets the text of the second sign line. (e.g. -2 Blah Blah)")
						.message(TextFormat.GREEN + "-3[:(enabled|disabled)] ... " + TextFormat.BLUE + "-- Sets the text of the third sign line. (e.g. -3 Blah Blah)")
						.message(TextFormat.GREEN + "-4[:(enabled|disabled)] ... " + TextFormat.BLUE + "-- Sets the text of the fourth sign line. (e.g. -4 Blah Blah)")
						.message(TextFormat.GREEN + "-clear " + TextFormat.BLUE + "-- Clears the line buffer. (Alias: -c)")
						.message(TextFormat.GREEN + "-clearall " + TextFormat.BLUE + "-- Clears the line buffer and sets all lines back to enabled. (Alias: -ca)")
						.message(TextFormat.GREEN + "-multiple [on|off] " + TextFormat.BLUE + "-- Enables or disables ranged mode. (Alias: -m) (see Wiki for more information)")
						.message(TextFormat.GREEN + "-save (name) " + TextFormat.BLUE + "-- Save you buffer to a file named [name]. (Alias: -s)")
						.message(TextFormat.GREEN + "-open (name) " + TextFormat.BLUE + "-- Loads a buffer from a file named [name]. (Alias: -o)")
						.send();
				} else if (parameter.startsWith("-1")) {
					textChanged = true;
					index = parseSignLineFromParam(parameters, SIGN_LINE_1, snipe, index);
				} else if (parameter.startsWith("-2")) {
					textChanged = true;
					index = parseSignLineFromParam(parameters, SIGN_LINE_2, snipe, index);
				} else if (parameter.startsWith("-3")) {
					textChanged = true;
					index = parseSignLineFromParam(parameters, SIGN_LINE_3, snipe, index);
				} else if (parameter.startsWith("-4")) {
					textChanged = true;
					index = parseSignLineFromParam(parameters, SIGN_LINE_4, snipe, index);
				} else if (parameter.equalsIgnoreCase("-clear") || parameter.equalsIgnoreCase("-c")) {
					clearBuffer();
					messenger.sendMessage(TextFormat.BLUE + "Internal text buffer cleard.");
				} else if (parameter.equalsIgnoreCase("-clearall") || parameter.equalsIgnoreCase("-ca")) {
					clearBuffer();
					resetStates();
					messenger.sendMessage(TextFormat.BLUE + "Internal text buffer cleard and states back to enabled.");
				} else if (parameter.equalsIgnoreCase("-multiple") || parameter.equalsIgnoreCase("-m")) {
					if ((index + 1) >= parameters.length) {
						messenger.sendMessage(TextFormat.RED + String.format("Missing parameter after %s.", parameter));
						continue;
					}
					this.rangedMode = (parameters[++index].equalsIgnoreCase("on") || parameters[++index].equalsIgnoreCase("yes"));
					messenger.sendMessage(TextFormat.BLUE + String.format("Ranged mode is %s", TextFormat.GREEN + (this.rangedMode ? "enabled" : "disabled")));
					if (this.rangedMode) {
						messenger.sendMessage(TextFormat.GREEN + "Brush size set to " + TextFormat.RED + toolkitProperties.getBrushSize());
						messenger.sendMessage(TextFormat.AQUA + "Brush height set to " + TextFormat.RED + toolkitProperties.getVoxelHeight());
					}
				} else if (parameter.equalsIgnoreCase("-save") || parameter.equalsIgnoreCase("-s")) {
					if ((index + 1) >= parameters.length) {
						messenger.sendMessage(TextFormat.RED + String.format("Missing parameter after %s.", parameter));
						continue;
					}
					String fileName = parameters[++index];
					saveBufferToFile(snipe, fileName);
				} else if (parameter.equalsIgnoreCase("-open") || parameter.equalsIgnoreCase("-o")) {
					if ((index + 1) >= parameters.length) {
						messenger.sendMessage(TextFormat.RED + String.format("Missing parameter after %s.", parameter));
						continue;
					}
					String fileName = parameters[++index];
					loadBufferFromFile(snipe, fileName);
					textChanged = true;
				}
			} catch (RuntimeException exception) {
				messenger.sendMessage(TextFormat.RED + String.format("Error while parsing parameter %s", parameter));
				exception.printStackTrace();
			}
		}
		if (textChanged) {
			displayBuffer(snipe);
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		if (this.rangedMode) {
			setRanged(snipe);
		} else {
			setSingle(snipe);
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (targetBlock.getState() instanceof Sign) {
			Sign sign = (Sign) targetBlock.getState();
			for (int i = 0; i < this.signTextLines.length; i++) {
				if (this.signLinesEnabled[i]) {
					this.signTextLines[i] = sign.getLine(i);
				}
			}
			displayBuffer(snipe);
		} else {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Target block is not a sign.");
		}
	}

	/**
	 * Sets the text of a given sign.
	 */
	private void setSignText(Sign sign) {
		for (int i = 0; i < this.signTextLines.length; i++) {
			if (this.signLinesEnabled[i]) {
				sign.setLine(i, this.signTextLines[i]);
			}
		}
		sign.update();
	}

	/**
	 * Sets the text of the target sign if the target block is a sign.
	 */
	private void setSingle(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (targetBlock.getState() instanceof Sign) {
			setSignText((Sign) targetBlock.getState());
		} else {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Target block is not a sign.");
		}
	}

	/**
	 * Sets all signs in a range of box{x=z=brushSize*2+1 ; z=voxelHeight*2+1}.
	 */
	private void setRanged(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		Block targetBlock = getTargetBlock();
		int brushSize = toolkitProperties.getBrushSize();
		int voxelHeight = toolkitProperties.getVoxelHeight();
		int minX = targetBlock.getX() - brushSize;
		int maxX = targetBlock.getX() + brushSize;
		int minY = targetBlock.getY() - voxelHeight;
		int maxY = targetBlock.getY() + voxelHeight;
		int minZ = targetBlock.getZ() - brushSize;
		int maxZ = targetBlock.getZ() + brushSize;
		boolean signFound = false; // indicates whether or not a sign was set
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					BlockState blockState = this.getWorld()
						.getBlockAt(x, y, z)
						.getState();
					if (blockState instanceof Sign) {
						setSignText((Sign) blockState);
						signFound = true;
					}
				}
			}
		}
		if (!signFound) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Did not found any sign in selection box.");
		}
	}

	/**
	 * Parses parameter input text of line [param:lineNumber].
	 * Iterates though the given array until the next top level param (a parameter which starts
	 * with a dash -) is found.
	 */
	private int parseSignLineFromParam(String[] params, int lineNumber, Snipe snipe, int i) {
		int index = i;
		SnipeMessenger messenger = snipe.createMessenger();
		int lineIndex = lineNumber - 1;
		String parameter = params[index];
		boolean statusSet = false;
		if (parameter.contains(":")) {
			this.signLinesEnabled[lineIndex] = parameter.substring(parameter.indexOf(':'))
				.equalsIgnoreCase(":enabled");
			messenger.sendMessage(TextFormat.BLUE + "Line " + lineNumber + " is " + TextFormat.GREEN + (this.signLinesEnabled[lineIndex] ? "enabled" : "disabled"));
			statusSet = true;
		}
		if ((index + 1) >= params.length) {
			// return if the user just wanted to set the status
			if (statusSet) {
				return index;
			}
			messenger.sendMessage(TextFormat.RED + "Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
			this.signTextLines[lineIndex] = "";
			return index;
		}
		StringBuilder newTextBuilder = new StringBuilder();
		// go through the array until the next top level parameter is found
		for (index++; index < params.length; index++) {
			String currentParameter = params[index];
			if (!currentParameter.isEmpty() && currentParameter.charAt(0) == '-') {
				index--;
				break;
			} else {
				newTextBuilder.append(currentParameter).append(" ");
			}
		}
		newTextBuilder = new StringBuilder(TextFormat.translateAlternateColorCodes('&', newTextBuilder.toString()));
		// remove last space or return if the string is empty and the user just wanted to set the status
		String newText = newTextBuilder.toString();
		int length = newText.length();
		if (!newText.isEmpty() && newText.charAt(length - 1) == ' ') {
			newTextBuilder = new StringBuilder(newTextBuilder.substring(0, length - 1));
		} else if (newText.isEmpty()) {
			if (statusSet) {
				return index;
			}
			messenger.sendMessage(TextFormat.RED + "Warning: No text after -" + lineNumber + ". Setting buffer text to \"\" (empty string)");
		}
		// check the line length and cut the text if needed
		if (newTextBuilder.length() > MAX_SIGN_LINE_LENGTH) {
			messenger.sendMessage(TextFormat.RED + "Warning: Text on line " + lineNumber + " exceeds the maximum line length of " + MAX_SIGN_LINE_LENGTH + " characters. Your text will be cut.");
			newTextBuilder = new StringBuilder(newTextBuilder.substring(0, MAX_SIGN_LINE_LENGTH));
		}
		this.signTextLines[lineIndex] = newTextBuilder.toString();
		return index;
	}

	private void displayBuffer(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage(TextFormat.BLUE + "Buffer text set to: ");
		for (int index = 0; index < this.signTextLines.length; index++) {
			messenger.sendMessage((this.signLinesEnabled[index] ? TextFormat.GREEN + "(E): " : TextFormat.RED + "(D): ") + TextFormat.BLACK + this.signTextLines[index]);
		}
	}

	/**
	 * Saves the buffer to file.
	 */
	private void saveBufferToFile(Snipe snipe, String fileName) {
		SnipeMessenger messenger = snipe.createMessenger();
		File store = new File(this.pluginDataFolder, fileName + ".vsign");
		if (store.exists()) {
			messenger.sendMessage("This file already exists.");
			return;
		}
		try {
			store.createNewFile();
			FileWriter outFile = new FileWriter(store);
			BufferedWriter outStream = new BufferedWriter(outFile);
			for (int i = 0; i < this.signTextLines.length; i++) {
				outStream.write(this.signLinesEnabled[i] + "\n");
				outStream.write(this.signTextLines[i] + "\n");
			}
			outStream.close();
			outFile.close();
			messenger.sendMessage(TextFormat.BLUE + "File saved successfully.");
		} catch (IOException exception) {
			messenger.sendMessage(TextFormat.RED + "Failed to save file. " + exception.getMessage());
			exception.printStackTrace();
		}
	}

	/**
	 * Loads a buffer from a file.
	 */
	private void loadBufferFromFile(Snipe snipe, String fileName) {
		SnipeMessenger messenger = snipe.createMessenger();
		File store = new File(this.pluginDataFolder, fileName + ".vsign");
		if (!store.exists()) {
			messenger.sendMessage("This file does not exist.");
			return;
		}
		try {
			FileReader inFile = new FileReader(store);
			BufferedReader inStream = new BufferedReader(inFile);
			for (int i = 0; i < this.signTextLines.length; i++) {
				this.signLinesEnabled[i] = Boolean.parseBoolean(inStream.readLine());
				this.signTextLines[i] = inStream.readLine();
			}
			inStream.close();
			inFile.close();
			messenger.sendMessage(TextFormat.BLUE + "File loaded successfully.");
		} catch (IOException exception) {
			messenger.sendMessage(TextFormat.RED + "Failed to load file. " + exception.getMessage());
			exception.printStackTrace();
		}
	}

	/**
	 * Clears the internal text buffer. (Sets it to empty strings)
	 */
	private void clearBuffer() {
		Arrays.fill(this.signTextLines, "");
	}

	/**
	 * Resets line enabled states to enabled.
	 */
	private void resetStates() {
		Arrays.fill(this.signLinesEnabled, true);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.BLUE + "Buffer text: ");
		for (int index = 0; index < this.signTextLines.length; index++) {
			messenger.sendMessage((this.signLinesEnabled[index] ? TextFormat.GREEN + "(E): " : TextFormat.RED + "(D): ") + TextFormat.BLACK + this.signTextLines[index]);
		}
		messenger.sendMessage(TextFormat.BLUE + String.format("Ranged mode is %s", TextFormat.GREEN + (this.rangedMode ? "enabled" : "disabled")));
		if (this.rangedMode) {
			messenger.sendBrushSizeMessage();
			messenger.sendVoxelHeightMessage();
		}
	}
}
