package com.thevoxelbox.voxelsniper.util.material;

import org.cloudburstmc.server.block.BlockTypes;

public final class MaterialSets {

	public static final MaterialSet AIRS = MaterialSet.builder()
		.add(BlockTypes.AIR)
		.build();

	public static final MaterialSet CHESTS = MaterialSet.builder()
		.add(BlockTypes.CHEST)
		.add(BlockTypes.TRAPPED_CHEST)
		.add(BlockTypes.ENDER_CHEST)
		.build();

	public static final MaterialSet FENCE_GATES = MaterialSet.builder()
		.add(BlockTypes.ACACIA_FENCE_GATE)
		.add(BlockTypes.BIRCH_FENCE_GATE)
		.add(BlockTypes.DARK_OAK_FENCE_GATE)
		.add(BlockTypes.JUNGLE_FENCE_GATE)
		.add(BlockTypes.FENCE_GATE)
		.add(BlockTypes.SPRUCE_FENCE_GATE)
		.build();

	public static final MaterialSet SNOWS = MaterialSet.builder()
		.add(BlockTypes.SNOW)
		.add(BlockTypes.SNOW_LAYER)
		.build();

	public static final MaterialSet WOODEN_FENCES = MaterialSet.builder()
		.add(BlockTypes.FENCE)
		.build();

	public static final MaterialSet FENCES = MaterialSet.builder()
		.with(WOODEN_FENCES)
		.add(BlockTypes.NETHER_BRICK_FENCE)
		.build();

	public static final MaterialSet TORCHES = MaterialSet.builder()
		.add(BlockTypes.TORCH)
		.add(BlockTypes.UNDERWATER_TORCH)
		.build();

	public static final MaterialSet REDSTONE_TORCHES = MaterialSet.builder()
		.add(BlockTypes.REDSTONE_TORCH)
		.add(BlockTypes.UNLIT_REDSTONE_TORCH)
		.build();

	public static final MaterialSet FLOWERS = MaterialSet.builder()
		.add(BlockTypes.RED_FLOWER)
		.add(BlockTypes.YELLOW_FLOWER)
		.build();

	public static final MaterialSet MUSHROOMS = MaterialSet.builder()
		.add(BlockTypes.BROWN_MUSHROOM)
		.add(BlockTypes.RED_MUSHROOM)
		.build();

	public static final MaterialSet STEMS = MaterialSet.builder()
		.add(BlockTypes.MELON_STEM)
		.add(BlockTypes.PUMPKIN_STEM)
		.build();

	public static final MaterialSet FLORA = MaterialSet.builder()
		.with(FLOWERS)
		.with(MUSHROOMS)
		.with(STEMS)
		.add(BlockTypes.TALL_GRASS)
		.add(BlockTypes.TALL_GRASS)
		.add(BlockTypes.DEADBUSH)
		.add(BlockTypes.WHEAT)
		.add(BlockTypes.REEDS)
		.add(BlockTypes.VINE)
		.add(BlockTypes.WATERLILY)
		.add(BlockTypes.CACTUS)
		.add(BlockTypes.NETHER_WART)
		.build();

	public static final MaterialSet STONES = MaterialSet.builder()
		.add(BlockTypes.STONE)
		.build();

	public static final MaterialSet GRASSES = MaterialSet.builder()
		.add(BlockTypes.GRASS)
		.add(BlockTypes.PODZOL)
		.build();

	public static final MaterialSet DIRT = MaterialSet.builder()
		.add(BlockTypes.DIRT)
		.build();

	public static final MaterialSet LIQUIDS = MaterialSet.builder()
		.add(BlockTypes.WATER)
		.add(BlockTypes.LAVA)
		.build();

	public static final MaterialSet FALLING = MaterialSet.builder()
		.with(LIQUIDS)
		.add(BlockTypes.SAND)
		.add(BlockTypes.GRAVEL)
		.build();

	public static final MaterialSet SANDSTONES = MaterialSet.builder()
		.add(BlockTypes.SANDSTONE)
		.build();

	public static final MaterialSet RED_SANDSTONES = MaterialSet.builder()
		.add(BlockTypes.RED_SANDSTONE)
		.build();

	public static final MaterialSet OVERRIDEABLE = MaterialSet.builder()
		.with(STONES)
		.with(GRASSES)
		.with(DIRT)
		.with(SANDSTONES)
		.with(RED_SANDSTONES)
		.add(BlockTypes.SAND)
		.add(BlockTypes.GRAVEL)
		.add(BlockTypes.MOSSY_COBBLESTONE)
		.add(BlockTypes.OBSIDIAN)
		.add(BlockTypes.SNOW)
		.add(BlockTypes.CLAY)
		.build();

	public static final MaterialSet ORES = MaterialSet.builder()
		.add(BlockTypes.COAL_ORE)
		.add(BlockTypes.DIAMOND_ORE)
		.add(BlockTypes.EMERALD_ORE)
		.add(BlockTypes.GOLD_ORE)
		.add(BlockTypes.IRON_ORE)
		.add(BlockTypes.LAPIS_ORE)
		.add(BlockTypes.QUARTZ_ORE)
		.add(BlockTypes.REDSTONE_ORE)
		.build();

	public static final MaterialSet OVERRIDEABLE_WITH_ORES = MaterialSet.builder()
		.with(OVERRIDEABLE)
		.with(ORES)
		.build();

	public static final MaterialSet PISTONS = MaterialSet.builder()
		.add(BlockTypes.PISTON_ARM_COLLISION)
		.add(BlockTypes.PISTON)
		.add(BlockTypes.STICKY_PISTON_ARM_COLLISION)
		.add(BlockTypes.STICKY_PISTON)
		.build();

	public static final MaterialSet PRESSURE_PLATES = MaterialSet.builder()
		.add(BlockTypes.ACACIA_PRESSURE_PLATE)
		.add(BlockTypes.BIRCH_PRESSURE_PLATE)
		.add(BlockTypes.DARK_OAK_PRESSURE_PLATE)
		.add(BlockTypes.JUNGLE_PRESSURE_PLATE)
		.add(BlockTypes.WOODEN_PRESSURE_PLATE)
		.add(BlockTypes.SPRUCE_PRESSURE_PLATE)
		.add(BlockTypes.STONE_PRESSURE_PLATE)
		.add(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE)
		.add(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE)
		.build();

	public static final MaterialSet SIGNS = MaterialSet.builder()
		.with(BlockTypes.STANDING_SIGN, BlockTypes.WALL_SIGN)
		.with(BlockTypes.ACACIA_STANDING_SIGN, BlockTypes.ACACIA_WALL_SIGN)
		.with(BlockTypes.BIRCH_STANDING_SIGN, BlockTypes.BIRCH_WALL_SIGN)
		.with(BlockTypes.DARK_OAK_STANDING_SIGN, BlockTypes.DARK_OAK_WALL_SIGN)
		.with(BlockTypes.JUNGLE_STANDING_SIGN, BlockTypes.JUNGLE_WALL_SIGN)
		.with(BlockTypes.SPRUCE_STANDING_SIGN, BlockTypes.SPRUCE_WALL_SIGN)
		.build();

	public static final MaterialSet BEDS = MaterialSet.builder()
		.add(BlockTypes.BED)
		.build();

	private MaterialSets() {
		throw new UnsupportedOperationException("Cannot create instance of utility class");
	}
}
