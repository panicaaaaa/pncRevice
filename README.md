# 🧩 PncRevice — Advanced Player Check System

**PncRevice** is a modern Minecraft moderation plugin that allows staff to conduct player checks (anti-cheat verifications) easily through commands and a fully configurable GUI panel.  
It supports **MiniMessage**, **HEX colors**, **Brigadier commands**, and **asynchronous formatting** for smooth performance and style.

---

## ✨ Main Features

- ✅ Simple command `/revice <player>` — start a full check process  
- 🧭 `/revice setposs` and `/revice setspawn` — set teleport points  
- ⚙️ `/revice reload` — reloads configuration on the fly  
- 🎮 Moderator GUI panel:
  - Ban suspect
  - Add extra time (`timer.dop_time`)
  - Pause timer
  - Stop verification
- 💬 Private review chat between suspect and moderator
- 🚫 Restriction on all commands & movement during the check
- 🔊 Customizable messages, titles, and sounds
- 🕒 ActionBar timer for both moderator and suspect
- ⚡ Async formatting with MiniMessage & HEX support
- 🧱 Configurable punishment command if the suspect leaves

---

## ⚙️ Commands

| Command | Permission | Description |
|----------|-------------|-------------|
| `/revice <player>` | `pncRevice.start` | Start a check for a player |
| `/revice setposs` | `pncRevice.setposs` | Set the checkpoint location |
| `/revice setspawn` | `pncRevice.setspawn` | Set the spawn location after check |
| `/revice reload` | `pncRevice.reload` | Reload the plugin configuration |

---

## 🗂️ Configuration Example (`config.yml`)

```yaml
checkpoint:
  world: world
  x: 0.5
  y: 100.0
  z: 0.5
  yaw: 0.0
  pitch: 0.0

spawn:
  world: world
  x: 0.5
  y: 64.0
  z: 0.5
  yaw: 0.0
  pitch: 0.0

timer:
  default-seconds: 600
  dop_time: 300
  actionbar:
    format: "<gray>Check time: <#FFD166>%mm:ss%</#FFD166></gray>"
    paused-chat: "<yellow>Check paused.</yellow>"
    async-reformat: true

review-chat:
  prefix: "<gray>[<light_purple>REVICE</light_purple>]</gray>"
  role:
    suspect: "<red>SUSPECT</red>"
    moder: "<green>MODERATOR</green>"
  format: "%prefix% %role% <white>%name%</white>: <gray>%message%</gray>"

punish:
  on-quit:
    enabled: true
    grace-seconds: 60
    command: "ban %player% 7d Left during verification"

messages:
  command:
    reload: "<green>Configuration reloaded successfully.</green>"
    usage: "<yellow>Usage:</yellow> /revice <player> or /revice setposs"
    invalid: "<red>Invalid command or syntax.</red>"
    no-permission: "<red>You don’t have permission.</red>"

  start:
    chat:
      - "<green>You have been called for verification.</green>"
      - "<gray>Follow the moderator’s instructions.</gray>"
    title:
      title: "<yellow>Verification</yellow>"
      subtitle: "<gray>Do not leave the server</gray>"
      fadein: 10
      stay: 60
      fadeout: 10
    sound: "BLOCK_NOTE_BLOCK_PLING,1.0,1.5"

  stop:
    chat:
      - "<green>Verification complete.</green>"
      - "<gray>Thank you for cooperation.</gray>"
    title:
      title: "<green>Clear</green>"
      subtitle: "<gray>Thank you for cooperation</gray>"
      fadein: 10
      stay: 40
      fadeout: 10
    sound: "ENTITY_PLAYER_LEVELUP,1.0,1.0"

  frozen:
    chat: "<gray>You are frozen: movement and commands are blocked.</gray>"

formating_time:
  time-one: "<gray>%s d.</gray> <yellow>%s h.</yellow> <gray>%s m.</gray> <yellow>%s s.</yellow>"
  time-two: "<yellow>%s h.</yellow> <gray>%s m.</gray> <yellow>%s s.</yellow>"
  time-three: "<yellow>%s m.</yellow> <gray>%s s.</gray>"
  time-four: "<yellow>%s s.</yellow>"

GUI:
  size: 27
  title: "&5Revice &7• &fPanel"
  items:
    ban:
      display_name: "&cBan"
      lore:
        - "&7Ban the player and stop verification"
      item: REDSTONE_BLOCK
      slots: ["10"]
    addtime:
      display_name: "&a+Extra Time"
      lore:
        - "&7Adds time from 'timer.dop_time'"
      item: CLOCK
      slots: ["12"]
    pause:
      display_name: "&ePause Timer"
      lore:
        - "&7Toggle timer pause"
      item: LEVER
      slots: ["14"]
    stop:
      display_name: "&cStop"
      lore:
        - "&7Finish the verification"
      item: BARRIER
      slots: ["16"]
    frame:
      display_name: ""
      lore: []
      item: GRAY_STAINED_GLASS_PANE
      slots: ["0-9","17-26"]
```

---

## 🧠 Developer Info

- **Language:** Java 16+  
- **API:** Paper 1.16.5+  
- **Dependencies:**  
  - `com.mojang:brigadier:1.0.18`  
  - `net.kyori:adventure-text-minimessage`  
  - `net.kyori:adventure-platform-bukkit`  

---

## 💜 Credits

Developed by **panicaaaaa **  
Design and concept inspired by panicaaaaa.  

---
