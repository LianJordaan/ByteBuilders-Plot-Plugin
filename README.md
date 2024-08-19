# ByteBuilders Plot Plugin

ByteBuilders Plot Plugin is a Bukkit/Spigot plugin designed to manage plots within the ByteBuilders Minecraft server.

## Getting Started

### Prerequisites

- Java 17 or higher
- [Spigot](https://www.spigotmc.org/) or [Paper](https://papermc.io/) server

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/LianJordaan/ByteBuilders-Plot-Plugin.git
   ```

2. Navigate to the project directory:
   ```sh
   cd ByteBuilders-Plot-Plugin
   ```

3. Rename the `.env.template` file to `.env`:
   ```sh
   mv .env.template .env
   ```

4. Add your configuration values to the `.env` file.

5. Build the plugin using Maven:
   ```sh
   mvn clean install
   ```

6. Copy the generated JAR file from `target` to the `plugins` directory of your Spigot/Paper server.

## Configuration

1. Start your server to generate the default configuration files.
2. Edit the configuration file located in the `plugins/ByteBuildersPlotPlugin` directory to match your setup.

## Features

- Dynamic plot management
- WebSocket communication for status updates
- Integration with external server control systems

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

**ByteBuilders Plot Plugin** © 2024 by Lian Jordaan. All rights reserved.