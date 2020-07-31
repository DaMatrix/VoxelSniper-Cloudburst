package com.thevoxelbox.voxelsniper.util.painter;

import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Location;

public class CubePainter implements Painter {

	private VectorVS center;
	private int radius;
	private BlockSetter blockSetter;

	public CubePainter center(Block block) {
		VectorVS center = Vectors.of(block);
		return center(center);
	}

	public CubePainter center(Location location) {
		VectorVS center = Vectors.of(location);
		return center(center);
	}

	public CubePainter center(VectorVS center) {
		this.center = center;
		return this;
	}

	public CubePainter radius(int radius) {
		this.radius = radius;
		return this;
	}

	public CubePainter blockSetter(BlockSetter blockSetter) {
		this.blockSetter = blockSetter;
		return this;
	}

	@Override
	public void paint() {
		if (this.center == null) {
			throw new RuntimeException("Center must be specified");
		}
		if (this.blockSetter == null) {
			throw new RuntimeException("Block setter must be specified");
		}
		paintCube();
	}

	private void paintCube() {
		Painters.block(this)
			.at(0, 0, 0)
			.paint();
		for (int first = 1; first <= this.radius; first++) {
			Painters.block(this)
				.at(first, 0, 0)
				.at(-first, 0, 0)
				.at(0, first, 0)
				.at(0, -first, 0)
				.at(0, 0, first)
				.at(0, 0, -first)
				.paint();
			for (int second = 1; second <= this.radius; second++) {
				Painters.block(this)
					.at(first, second, 0)
					.at(first, -second, 0)
					.at(-first, second, 0)
					.at(-first, -second, 0)
					.at(first, 0, second)
					.at(first, 0, -second)
					.at(-first, 0, second)
					.at(-first, 0, -second)
					.at(0, first, second)
					.at(0, first, -second)
					.at(0, -first, second)
					.at(0, -first, -second)
					.paint();
				for (int third = 1; third <= this.radius; third++) {
					Painters.block(this)
						.at(first, second, third)
						.at(first, second, -third)
						.at(first, -second, third)
						.at(first, -second, -third)
						.at(-first, second, third)
						.at(-first, second, -third)
						.at(-first, -second, third)
						.at(-first, -second, -third)
						.paint();
				}
			}
		}
	}

	@Override
	public VectorVS getCenter() {
		return this.center;
	}

	public int getRadius() {
		return this.radius;
	}

	@Override
	public BlockSetter getBlockSetter() {
		return this.blockSetter;
	}
}
