# Cozy Plugins

A collection of Minecraft server plugins built with Paper API 1.21.4.

## Modules

| Module | Description |
|--------|-------------|
| **cozy-core** | Shared library (shaded into other modules) with ConfigManager, MessageManager, and UpdateChecker |
| **cozy-hub** | Full-featured hub/lobby plugin with spawn, navigator, sidebar, hotbar, cosmetics, and protection |
| **cozy-holograms** | Hologram management plugin (WIP) |

## Requirements

- Java 21+
- Paper 1.21.4 or compatible fork
- Maven 3.9+

## Building

```bash
git clone https://github.com/TheMFCRaft1/CozyPlugins.git
cd CozyPlugins
mvn clean package
```

Built JARs will be in each module's `target/` directory.

## Project Structure

```
CozyPlugins/
├── cozy-core/          # Shared library (shaded)
├── cozy-hub/           # Hub/lobby plugin
├── cozy-holograms/     # Hologram plugin (WIP)
├── pom.xml             # Parent POM
├── LICENSE
└── .github/            # CI/CD workflows
```

## License

See [LICENSE](LICENSE) for details.
