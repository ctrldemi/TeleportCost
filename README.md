# TeleportCost  
A lightweight Minecraft plugin for PaperMC that adds experience costs to teleport commands, complete with some visual flair and sound effects!  
(Works seamlessly with Xaero's Minimap Waypoints and such, no extra setup needed.)

## 🚀 How it works  
TeleportCost hooks into Minecraft’s built-in `/tp` command and automatically subtracts experience when used. That’s it! No extra permissions or commands — just make sure your players have `minecraft.command.teleport` in their permission group.

## ⚙️ Configuration  
Here’s what you can tweak:

Teleport cost settings.
Explanation:
levels = experience levels (the green number)
exp = raw experience points (the bar progress)

```
teleport-cost: 5         # How much it costs to teleport
cost-type: levels        # Choose 'levels' or 'exp'
```

Sound and particle feedback
```
particle-count: 50       # Number of particles on teleport
particle-offset: 1.0     # Particle spread
success-sound-pitch: 1.0 # Sound pitch on success
fail-sound-pitch: 1.2    # Sound pitch on failure
```
To edit:  
1. Start your PaperMC server once (this generates the config).  
2. Open `/plugins/TeleportCost/config.yml` with your favorite text editor.  
3. Restart the server to apply changes.

## 📦 Installation  
1. Download the latest `.jar` file.  
2. Drop it into your PaperMC server’s `/plugins/` folder.  
3. Restart the server. That’s it! You're good to go.

## 🛠️ Building from source  
If you're feeling adventurous or want to tinker:
```
git clone https://github.com/ctrldemi/TeleportCost.git && cd TeleportCost
mvn clean package
```
Requires Maven.

## ⭐ Thanks
A huge "Thank You!" to the creator(s) of [TPPlugin](https://www.spigotmc.org/resources/tp-exp-cost.117452/) who broadly inspired this plugin.
