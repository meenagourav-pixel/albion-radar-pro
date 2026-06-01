Albion Radar Pro
A powerful radar application for Albion Online that displays nearby entities including resources, mobs, players, chests, and more.

Features
Entity Detection
Resources: All resource types (Fiber, Hide, Wood, Ore, Rock) with tier (T1-T8) and enchantment level (E0-E4) filtering
Living Resources: Special living resource variants with filtering
Mobs: 9 mob categories including Bosses, Mini Bosses, Mist Bosses, Drones, and more
Players: Detection with hostile/faction/passive status identification
Chests: Various chest types with tier information
Fishing Nodes: Fishing spots with tier and enchantment
Mists: Mist portals (Solo, Duo, Group, Corrupted, Avalonian)
Dungeons: All dungeon types including Hellgates and Expeditions
Alert System
Hostile Player Alerts: Sound + vibration when hostile players are detected
Resource Alerts: Configurable vibration alerts for high-tier resources
Boss Alerts: Alerts for boss-type mobs
Customizable: Full control over which entities trigger alerts
Overlay Radar
Drawn over the game for real-time visibility
Adjustable size and opacity
Draggable positioning
Configurable scanner range
Filtering
Resource filter by type, tier, and enchantment level
Mob filter by category
Player filter with party/guild/alliance exclusion
Export logs functionality
Requirements
Android 7.0 (API 24) or higher
No root required (uses VPN for packet capture)
Overlay permission
VPN permission
Installation
Download the APK from Releases
Enable "Install from unknown sources" in your device settings
Install the APK
Grant overlay permission when prompted
Grant VPN permission when starting the radar
Building from Source
Prerequisites
Android Studio Hedgehog (2023.1.1) or later
JDK 17
Android SDK 34
Build Steps
Clone the repository:
git clone https://github.com/yourusername/albion-radar-pro.gitcd albion-radar-pro
Open in Android Studio
Build the APK:
bash

./gradlew assembleRelease
The APK will be in app/build/outputs/apk/release/
Configuration
Scanner Range
Adjust the detection range (default: 50 units). Higher values may impact performance.

Overlay Settings
Size: Adjust radar size (150-500 pixels)
Opacity: Adjust transparency (20-100%)
Alert Settings
Sound: Enable/disable sound alerts
Vibration: Enable/disable vibration alerts
Hostile Players: Alert for hostile players
Resources: Alert for high-tier resources (configurable minimum tier/enchant)
Mobs: Alert for boss-type mobs
Player Exclusions
Exclude party members from hostile alerts
Exclude guild members from hostile alerts
Exclude alliance members from hostile alerts
Technical Details
Packet Capture
Uses Android VPNService API to capture network packets without root access. Only Albion Online traffic (port 5056) is processed.

Protocol
Supports GpBinaryV18 protocol used by current Albion Online version.

Events Handled
NewCharacter (29): Player spawn
Move (3): Entity movement
Leave (1): Entity despawn
NewHarvestableObject (40): Resource spawn
NewSimpleHarvestableList (38): Resource batch
NewHarvestableObjectBatch (59): Resource batch
HarvestableChangeState (46): Resource state change
HarvestFinished (61): Resource harvested
NewMob (123): Mob spawn
NewCagedObject (530): Chests, Mists, Dungeons
HealthUpdate (6): Entity health
Disclaimer
This application is for educational purposes only. Use at your own risk. The developers are not responsible for any consequences resulting from the use of this application, including but not limited to account bans.

License
This project is licensed under the MIT License - see the LICENSE file for details.

Contributing
Fork the repository
Create your feature branch (git checkout -b feature/amazing-feature)
Commit your changes (git commit -m 'Add some amazing feature')
Push to the branch (git push origin feature/amazing-feature)
Open a Pull Request
Acknowledgments
OpenRadar project for protocol reference
Albion Online community
