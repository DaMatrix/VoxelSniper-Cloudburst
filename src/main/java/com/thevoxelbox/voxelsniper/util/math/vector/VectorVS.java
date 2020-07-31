package com.thevoxelbox.voxelsniper.util.math.vector;

import java.util.Objects;

public class VectorVS {

	private int x;
	private int y;
	private int z;

	public VectorVS() {
		this(0, 0, 0);
	}

	public VectorVS(VectorVS vector) {
		this(vector.x, vector.y, vector.z);
	}

	public VectorVS(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public VectorVS plusX(int x) {
		return plus(x, 0, 0);
	}

	public VectorVS plusY(int y) {
		return plus(0, y, 0);
	}

	public VectorVS plusZ(int z) {
		return plus(0, 0, z);
	}

	public VectorVS plus(VectorVS vector) {
		return plus(vector.x, vector.y, vector.z);
	}

	public VectorVS plus(int x, int y, int z) {
		return new VectorVS(this.x + x, this.y + y, this.z + z);
	}

	public VectorVS timesX(int x) {
		return times(x, 1, 1);
	}

	public VectorVS timesY(int y) {
		return times(1, y, 1);
	}

	public VectorVS timesZ(int z) {
		return times(1, 1, z);
	}

	public VectorVS times(VectorVS vector) {
		return times(vector.x, vector.y, vector.z);
	}

	public VectorVS times(int x, int y, int z) {
		return new VectorVS(this.x * x, this.y * y, this.z * z);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		VectorVS vector3i = (VectorVS) object;
		return this.x == vector3i.x &&
			this.y == vector3i.y &&
			this.z == vector3i.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y, this.z);
	}

	@Override
	public String toString() {
		return "Vector3i{" +
			"x=" + this.x +
			", y=" + this.y +
			", z=" + this.z +
			"}";
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}
}
