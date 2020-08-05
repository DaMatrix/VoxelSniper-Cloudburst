package com.thevoxelbox.voxelsniper;

/*import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfigLoader;
import com.thevoxelbox.voxelsniper.listener.PlayerInteractListener;
import com.thevoxelbox.voxelsniper.listener.PlayerJoinListener;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;*/

import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfigLoader;
import com.thevoxelbox.voxelsniper.listener.PlayerInteractListener;
import com.thevoxelbox.voxelsniper.listener.PlayerJoinListener;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.cloudburstmc.server.plugin.PluginBase;
import org.cloudburstmc.server.plugin.PluginManager;
import org.cloudburstmc.server.utils.Config;
import org.cloudburstmc.server.utils.Identifier;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VoxelSniperPlugin extends PluginBase {

	private VoxelSniperConfig voxelSniperConfig;
	private BrushRegistry brushRegistry;
	private PerformerRegistry performerRegistry;
	private SniperRegistry sniperRegistry;

	@Override
	public void onLoad() {
		this.voxelSniperConfig = loadConfig();
		this.brushRegistry = loadBrushRegistry();
		this.performerRegistry = loadPerformerRegistry();
		this.sniperRegistry = new SniperRegistry();
		loadCommands();
	}

	@Override
	public void onEnable() {
		loadListeners();
		System.out.println("a");
	}

	private VoxelSniperConfig loadConfig() {
		saveDefaultConfig();
		Config config = getConfig();
		VoxelSniperConfigLoader voxelSniperConfigLoader = new VoxelSniperConfigLoader(config);
		int undoCacheSize = voxelSniperConfigLoader.getUndoCacheSize();
		boolean messageOnLoginEnabled = voxelSniperConfigLoader.isMessageOnLoginEnabled();
		int litesniperMaxBrushSize = voxelSniperConfigLoader.getLitesniperMaxBrushSize();
		List<Identifier> litesniperRestrictedMaterials = voxelSniperConfigLoader.getLitesniperRestrictedMaterials()
			.stream()
			.map(Identifier::fromString)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		return new VoxelSniperConfig(undoCacheSize, messageOnLoginEnabled, litesniperMaxBrushSize, litesniperRestrictedMaterials);
	}

	private BrushRegistry loadBrushRegistry() {
		BrushRegistry brushRegistry = new BrushRegistry();
		File dataFolder = getDataFolder();
		BrushRegistrar brushRegistrar = new BrushRegistrar(brushRegistry, dataFolder);
		brushRegistrar.registerBrushes();
		return brushRegistry;
	}

	private PerformerRegistry loadPerformerRegistry() {
		PerformerRegistry performerRegistry = new PerformerRegistry();
		PerformerRegistrar performerRegistrar = new PerformerRegistrar(performerRegistry);
		performerRegistrar.registerPerformers();
		return performerRegistry;
	}

	private void loadCommands() {
		CommandRegistry commandRegistry = new CommandRegistry(this);
		CommandRegistrar commandRegistrar = new CommandRegistrar(this, commandRegistry);
		commandRegistrar.registerCommands();
	}

	private void loadListeners() {
		PluginManager pluginManager = this.getServer().getPluginManager();
		pluginManager.registerEvents(new PlayerJoinListener(this), this);
		pluginManager.registerEvents(new PlayerInteractListener(this), this);
	}

	public VoxelSniperConfig getVoxelSniperConfig() {
		return this.voxelSniperConfig;
	}

	public BrushRegistry getBrushRegistry() {
		return this.brushRegistry;
	}

	public PerformerRegistry getPerformerRegistry() {
		return this.performerRegistry;
	}

	public SniperRegistry getSniperRegistry() {
		return this.sniperRegistry;
	}
}
