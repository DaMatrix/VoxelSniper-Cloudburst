package com.thevoxelbox.voxelsniper.util.painter;

import java.util.ArrayList;
import java.util.List;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;

public class BlockPainter implements Painter {

	private VectorVS center;
	private BlockSetter blockSetter;
	private List<VectorVS> shifts = new ArrayList<>();

	public BlockPainter(VectorVS center, BlockSetter blockSetter) {
		this.center = center;
		this.blockSetter = blockSetter;
	}

	public BlockPainter at(int xShift, int yShift, int zShift) {
		VectorVS shift = new VectorVS(xShift, yShift, zShift);
		return at(shift);
	}

	public BlockPainter at(VectorVS shift) {
		this.shifts.add(shift);
		return this;
	}

	@Override
	public void paint() {
		this.shifts.forEach(this::paintBlock);
	}

	private void paintBlock(VectorVS shift) {
		VectorVS position = this.center.plus(shift);
		this.blockSetter.setBlockAt(position);
	}

	@Override
	public VectorVS getCenter() {
		return this.center;
	}

	@Override
	public BlockSetter getBlockSetter() {
		return this.blockSetter;
	}
}
