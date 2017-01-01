package net.mohron.skyclaims.config;

import net.mohron.skyclaims.SkyClaims;
import net.mohron.skyclaims.config.type.GlobalConfig;
import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
	private final SkyClaims PLUGIN = SkyClaims.getInstance();
	private final Logger LOGGER = PLUGIN.getLogger();
	public static final int CONFIG_VERSION = 1;

	private ObjectMapper<GlobalConfig>.BoundInstance configMapper;
	private ConfigurationLoader<CommentedConfigurationNode> loader;

	public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
		this.loader = loader;
		try {
			this.configMapper = ObjectMapper.forObject(PLUGIN.getConfig());
		} catch (ObjectMappingException e) {
			e.printStackTrace();
		}

		this.load();
		this.initializeSchematic();
	}

	public void save() {
		try {
			SimpleConfigurationNode out = SimpleConfigurationNode.root();
			this.configMapper.serialize(out);
			this.loader.save(out);
		} catch (ObjectMappingException | IOException e) {
			LOGGER.error(String.format("Failed to save config.\r\n %s", e.getMessage()));
		}
	}

	private void load() {
		try {
			this.configMapper.populate(this.loader.load());
		} catch (ObjectMappingException | IOException e) {
			LOGGER.error(String.format("Failed to load config.\r\n %s", e.getMessage()));
		}
	}

	private void initializeSchematic() {
		Path defaultSchematic = Paths.get(String.format("%s%sisland.schematic", PLUGIN.getConfigDir(), File.separator));
		if (!Files.exists(defaultSchematic)) {
			try {
				Files.createFile(defaultSchematic);
				InputStream schemStream = this.getClass().getResourceAsStream("/island.schematic");
				FileOutputStream fileWriter = new FileOutputStream(defaultSchematic.toFile());
				int read = 0;
				byte[] buffer = new byte[1024];
				while ((read = schemStream.read(buffer)) != -1) {
					fileWriter.write(buffer, 0, read);
				}
				schemStream.close();
				fileWriter.close();
			} catch (IOException e) {
				LOGGER.error(String.format("Failed to create default schematic.\r\n %s", e.getMessage()));
			}
		}
	}
}
