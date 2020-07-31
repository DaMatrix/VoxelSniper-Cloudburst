package com.thevoxelbox.voxelsniper.config;

import org.cloudburstmc.server.utils.Identifier;

import java.util.List;
public class VoxelSniperConfig {

	private int undoCacheSize;
	private boolean messageOnLoginEnabled;
	private int litesniperMaxBrushSize;
	private List<Identifier> litesniperRestrictedMaterials;

	public VoxelSniperConfig(int undoCacheSize, boolean messageOnLoginEnabled, int litesniperMaxBrushSize, List<Identifier> litesniperRestrictedMaterials) {
		this.undoCacheSize = undoCacheSize;
		this.messageOnLoginEnabled = messageOnLoginEnabled;
		this.litesniperMaxBrushSize = litesniperMaxBrushSize;
		this.litesniperRestrictedMaterials = litesniperRestrictedMaterials;
	}

	public int getUndoCacheSize() {
		return this.undoCacheSize;
	}

	public boolean isMessageOnLoginEnabled() {
		return this.messageOnLoginEnabled;
	}

	public int getLitesniperMaxBrushSize() {
		return this.litesniperMaxBrushSize;
	}

	public List<Identifier> getLitesniperRestrictedMaterials() {
		return this.litesniperRestrictedMaterials;
	}
}
