# Kammoun API Wiki

A comprehensive Bukkit/Spigot plugin development API providing command handling, configuration management, database utilities, GUI menus, and various helper classes.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Commands System](#commands-system)
3. [Configuration](#configuration)
4. [Database Management](#database-management)
5. [GUI Menus](#gui-menus)
6. [Items (KItem)](#items-kitem)
7. [Chat & Colors](#chat--colors)
8. [Utilities](#utilities)
   - [Effects](#effects)
   - [Sounds](#sounds)
   - [Titles](#titles)
   - [Boss Bars](#boss-bars)
   - [Locations](#locations)
   - [Time Utils](#time-utils)
   - [Placeholders](#placeholders)
9. [BungeeCord Integration](#bungeecord-integration)
10. [Version Compatibility](#version-compatibility)

---

## Getting Started

### Installation

1. Add the API as a dependency to your plugin
2. Set your main class in `plugin.yml`
3. Extend or use the provided utility classes

### Basic Setup

```yaml
# plugin.yml
name: YourPlugin
version: '1.0.0'
main: com.yourpackage.YourMainClass
api-version: '1.21'
```

---

## Commands System

### KCommand - Base Command Handler

Create custom commands by extending `KCommand`:

```java
public class MyCommand extends KCommand {
    
    public MyCommand() {
        super("mycommand", "myplugin.use", true);
        // commandName, permission, playerOnly
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Command executed!");
    }
    
    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return Arrays.asList("option1", "option2");
    }
}
```

**Constructor Parameters:**
- `commandName` - The command name
- `permission` - Required permission (null for no permission)
- `playerOnly` - Whether command can only be executed by players

### KSubCommand - Sub-commands

Create sub-commands by implementing `KSubCommand`:

```java
public class ReloadSubCommand implements KSubCommand {
    
    @Override
    public String getName() {
        return "reload";
    }
    
    @Override
    public String getPermission() {
        return "myplugin.admin";
    }
    
    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage("Reloading...");
    }
    
    @Override
    public List<String> getSubCommandTab(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
```

**Adding Sub-commands:**

```java
public MyCommand() {
    super("mycommand");
    addSubCommand(new ReloadSubCommand(), new HelpSubCommand());
}
```

---

## Configuration

### KConfigLoader - Configuration Manager

Extend `KConfigLoader` to manage configuration files:

```java
public class MyConfig extends KConfigLoader {
    
    public MyConfig(JavaPlugin plugin) {
        super(plugin, "config.yml");
    }
    
    public String getWelcomeMessage() {
        return getFormattedString("messages.welcome");
    }
    
    public int getMaxPlayers() {
        return getInt("settings.max-players");
    }
}
```

**Available Methods:**

```java
getString(String path)                    // Get string with default fallback
getString(String path, String default)    // Get string with custom default
getFormattedString(String path)           // Get colored string
getInt(String path)                       // Get integer (default: 0)
getBoolean(String path)                   // Get boolean (default: false)
getStringList(String path)                // Get string list
getFormattedStringList(String path)       // Get colored string list
save()                                    // Save configuration
load()                                    // Reload configuration
```

---

## Database Management

### KDataBaseManager - Database Connection

Create a database manager by extending `KDataBaseManager`:

```java
public class MyDatabase extends KDataBaseManager {
    
    public MyDatabase(FileConfiguration config) {
        super(config);
        connect();
    }
    
    @Override
    protected void loadDatabaseInfo(FileConfiguration config) {
        this.dbHost = config.getString("database.host");
        this.dbPort = config.getString("database.port");
        this.dbName = config.getString("database.name");
        this.dbUser = config.getString("database.user");
        this.dbPassword = config.getString("database.password");
    }
    
    @Override
    protected void connect() {
        // Your connection logic
        String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### KTables - Table Management

```java
public class PlayersTable extends KTables {
    
    private final KDataBaseManager database;
    
    @Override
    protected void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players (" +
                     "uuid VARCHAR(36) PRIMARY KEY," +
                     "name VARCHAR(16)," +
                     "coins INT DEFAULT 0)";
        try (PreparedStatement ps = database.getPreparedStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void dropTable() {
        // Drop table logic
    }
}
```

**Data Save Types:**
```java
DataSaveType.MYSQL
DataSaveType.SQLITE
DataSaveType.JSON
```

---

## GUI Menus

### KMenu - Custom Inventory GUIs

Create interactive menus by extending `KMenu`:

```java
public class ShopMenu extends KMenu {
    
    public ShopMenu(JavaPlugin plugin) {
        super(plugin, "shop_menu.yml");
    }
    
    @Override
    protected void setMenuItems(String... placeholders) {
        // Add items to inventory
        KItem sword = new KItem(Material.DIAMOND_SWORD)
                .name("&bDiamond Sword")
                .lore("&7Price: &a$100")
                .price(100)
                .commands("give %player% diamond_sword");
        
        inventory.setItem(10, sword.getItemStack());
    }
    
    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        // Handle item clicks
        if (clicked != null && clicked.getType() == Material.DIAMOND_SWORD) {
            player.sendMessage("You purchased a sword!");
        }
    }
}
```

**Configuration Example (shop_menu.yml):**

```yaml
Title: "&6&lShop Menu"
Size: 54

Navigation:
  Go-Back:
    material: ARROW
    slot: 45
    name: "&cGo Back"
  
  Next-Page:
    material: ARROW
    slot: 53
    name: "&aNext Page"
  
  Prev-Page:
    material: ARROW
    slot: 45
    name: "&aPrevious Page"
  
  Close-Page:
    material: BARRIER
    slot: 49
    name: "&cClose"
  
  Filler-Item:
    material: GRAY_STAINED_GLASS_PANE
    name: " "
```

**Opening Menus:**

```java
// Simple open
menu.open(player);

// With placeholders
menu.open(player, "%player%", player.getName(), "%coins%", "1000");

// With pagination
menu.open(player, currentPage, maxPages, placeholders);
```

---

## Items (KItem)

### Creating Items

The `KItem` class provides a fluent API for creating custom items:

```java
KItem item = new KItem(plugin, Material.DIAMOND_SWORD)
    .name("&b&lLegendary Sword")
    .lore("&7Damage: &c+10", "&7Durability: &a1000")
    .amount(1)
    .enchant(Enchantment.SHARPNESS, 5)
    .enchant(Enchantment.FIRE_ASPECT, 2)
    .glow()
    .unbreakable(true)
    .modelData(1001)
    .flag(ItemFlag.HIDE_ATTRIBUTES)
    .nbt("custom_id", "legendary_sword_001");

ItemStack finalItem = item.getItemStack();
```

### Item Features

**Basic Properties:**
```java
.name(String)              // Set display name (supports color codes)
.lore(String...)           // Set lore lines
.lore(List<String>)        // Set lore from list
.amount(int)               // Set stack size
.damage(int)               // Set durability damage
```

**Enchantments & Effects:**
```java
.enchant(Enchantment, level)    // Add enchantment
.glow()                         // Add glow effect
.flag(ItemFlag...)              // Add item flags
.unbreakable(boolean)           // Set unbreakable
```

**Special Items:**
```java
// Player Heads
.skullOwner("PlayerName", plugin)
.texture("base64TextureValue")

// Leather Armor
.armorColor(Color.RED)

// Potions
.potionEffect(PotionEffectType.SPEED, 200, 1)
.potionColor(Color.BLUE)

// Banners
.addBannerPattern(pattern)
.bannerPatterns(List<Pattern>)
```

**Custom Model Data:**
```java
.modelData(1001)  // 1.14+
```

**NBT Data:**
```java
.nbt("key", "string_value")
.nbt("count", 42)
.nbt("price", 99.99)
.nbt("enabled", true)
```

**Command Integration:**
```java
.price(100.0)
.commands("give %player% diamond", "money take %player% 100")
.permission("shop.buy.diamonds")
.commandType(KItem.CommandType.CONSOLE)
```

**Command Types:**
- `PLAYER` - Execute as player
- `CONSOLE` - Execute as console
- `BUNGEE` - Execute on BungeeCord

### Loading from Config

```yaml
diamond_sword:
  material: DIAMOND_SWORD
  name: "&b&lLegendary Sword"
  lore:
    - "&7Damage: &c+10"
    - "&7Durability: &a1000"
  amount: 1
  slot: 10
  price: 100.0
  permission: "shop.buy.sword"
  CommandType: CONSOLE
  commands:
    - "give %player% diamond_sword"
  enchantments:
    - "SHARPNESS:5"
    - "FIRE_ASPECT:2"
  flags:
    - "HIDE_ATTRIBUTES"
    - "HIDE_ENCHANTS"
  custom-model-data: 1001
  unbreakable: true
  glow: true
  nbt:
    custom_id: "string:legendary_sword_001"
    tier: "int:5"
    tradeable: "bool:false"

# Player Head
player_head:
  material: PLAYER_HEAD
  name: "&ePlayer Head"
  texture: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNlYjE3MDhkNTQwNGVmMzI2MTAzZTdiNjA1NTljOTE3OGYzZGNlNzI5MDA3YWM5YTBiNDk4YmRlYmU0NjEwNyJ9fX0="

# Leather Armor
leather_chestplate:
  material: LEATHER_CHESTPLATE
  name: "&cRed Armor"
  color: "255,0,0"  # RGB

# Potion
speed_potion:
  material: POTION
  name: "&bSpeed Potion"
  potion-color: "0,255,255"
  potion-effects:
    - "SPEED:60:1"  # type:duration(seconds):amplifier
```

**Loading in Code:**
```java
ConfigurationSection section = config.getConfigurationSection("diamond_sword");
KItem item = KItem.fromConfig(section);
ItemStack itemStack = item.getItemStack();
```

---

## Chat & Colors

### ChatFormater - Color Formatting

```java
// Basic color codes
String colored = ChatFormater.color("&aGreen &bBlue &cRed");

// Hex colors (1.16+)
String hex = ChatFormater.color("&#FF5733This is orange!");
String hex2 = ChatFormater.color("!#00FF00This is green!");
```

### HexColorCodes - Advanced Formatting

```java
// Rainbow text
String rainbow = HexColorCodes.rainbowText("Rainbow Message!");

// Gradient text
String gradient = HexColorCodes.gradientText(
    "Gradient Message",
    new Color(255, 0, 0),    // Start color (red)
    new Color(0, 0, 255)     // End color (blue)
);

// Parse hex color
Color color = HexColorCodes.parseHexColor("#FF5733");
```

**Color Code Formats:**
- `&` - Standard Minecraft color codes (`&a`, `&b`, etc.)
- `&#RRGGBB` - Hex colors (1.16+)
- `!#RRGGBB` - Alternative hex format

---

## Utilities

### Effects

**KEffect - Potion Effects:**

```java
// Create effect
KEffect effect = new KEffect(PotionEffectType.SPEED)
    .duration(30)      // seconds
    .amplifier(1)      // level 2
    .particles(true)
    .ambient(false);

// Apply to player
effect.apply(player);

// Build PotionEffect
PotionEffect potion = effect.build();

// Clear all effects
KEffect.clearAll(player);
```

**From Config:**
```yaml
speed_effect:
  type: "SPEED"
  duration: 30
  amplifier: 1
  particles: true
  ambient: false
```

```java
KEffect effect = KEffect.fromConfig(config.getConfigurationSection("speed_effect"));
effect.apply(player);
```

---

### Sounds

**KSound - Play Sounds:**

```java
// Create sound
KSound sound = new KSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                        // sound, pitch, volume

// Play to player
sound.playSound(player);
```

**From Config:**
```yaml
level_up_sound:
  sound: "ENTITY_PLAYER_LEVELUP"
  pitch: "1.0"
  volume: "1.0"
```

```java
KSound sound = KSound.getSoundFromConfig(config.getConfigurationSection("level_up_sound"));
sound.playSound(player);
```

---

### Titles

**KTitle - Send Titles:**

```java
// Create title
KTitle title = new KTitle("&6&lWelcome!", "&eEnjoy your stay")
    .fadeIn(10)     // ticks
    .stay(70)       // ticks
    .fadeOut(20);   // ticks

// Send to player
title.send(player);

// With placeholders
KTitle customTitle = title.withPlaceholders(
    "%player%", player.getName(),
    "%rank%", "VIP"
);
customTitle.send(player);
```

**From Config:**
```yaml
welcome_title:
  header: "&6&lWelcome %player%!"
  footer: "&eEnjoy your stay"
  fade_in: 10
  stay: 70
  fade_out: 20
```

```java
KTitle title = KTitle.fromConfig(config.getConfigurationSection("welcome_title"));
title.withPlaceholders("%player%", player.getName()).send(player);
```

---

### Boss Bars

**KBossBar - Display Boss Bars:**

```java
// Create boss bar
KBossBar bossBar = new KBossBar(
    "&6&lEvent Starting Soon!",
    BarColor.YELLOW,
    BarStyle.SOLID,
    1.0  // progress (0.0 - 1.0)
);

// Send to player
bossBar.send(player);

// Update progress
bossBar.send(player, 0.5);

// With placeholders
bossBar.withPlaceholders("%time%", "30").send(player);

// Stop boss bar
KBossBar.stop(player);
```

**From Config:**
```yaml
event_bossbar:
  text: "&6&lEvent: %event%"
  color: "YELLOW"
  style: "SOLID"
  progress: 1.0
```

```java
KBossBar bossBar = KBossBar.fromConfig(config.getConfigurationSection("event_bossbar"));
bossBar.withPlaceholders("%event%", "PvP Arena").send(player);
```

**Bar Colors:**
`BLUE`, `GREEN`, `PINK`, `PURPLE`, `RED`, `WHITE`, `YELLOW`

**Bar Styles:**
`SOLID`, `SEGMENTED_6`, `SEGMENTED_10`, `SEGMENTED_12`, `SEGMENTED_20`

---

### Locations

**Klocation - Location Management:**

```java
// From Bukkit Location
Klocation loc = new Klocation(player.getLocation());

// Manual creation
Klocation loc = new Klocation(
    new Vector(100, 64, 200),
    "world",
    90.0f,    // yaw
    0.0f      // pitch
);

// Get Bukkit Location
Location bukkitLoc = loc.getLocation();

// Access components
double x = loc.getX();
double y = loc.getY();
double z = loc.getZ();
String world = loc.getWorldName();
```

**From Config:**
```yaml
spawn_location:
  world: "world"
  x: 100.5
  y: 64.0
  z: 200.5
  yaw: 90.0
  pitch: 0.0
```

```java
Klocation spawn = Klocation.getLocationFromConfig(
    config.getConfigurationSection("spawn_location")
);
player.teleport(spawn.getLocation());
```

---

### Time Utils

**TimeUtils - Time Formatting & Parsing:**

```java
// Format seconds to HH:MM:SS or MM:SS
String time1 = TimeUtils.format(3665);  // "01:01:05"
String time2 = TimeUtils.format(125);   // "02:05"

// Format milliseconds
String time3 = TimeUtils.formatMillis(125450);  // "02:05.450"

// Parse time string to seconds
long seconds = TimeUtils.parse("1d12h30m45s");  // 131445 seconds
long seconds2 = TimeUtils.parse("5m30s");        // 330 seconds
```

**Time Units:**
- `d` - days
- `h` - hours
- `m` - minutes
- `s` - seconds

---

### Placeholders

**PlaceHolderHelper - Replace Placeholders:**

```java
// In strings
String message = "Welcome %player% to %server%!";
String result = PlaceHolderHelper.parsePlaceholders(
    message,
    "%player%", player.getName(),
    "%server%", "Survival"
);
// Result: "Welcome Steve to Survival!"

// In ItemStacks
ItemStack item = new ItemStack(Material.DIAMOND);
ItemMeta meta = item.getItemMeta();
meta.setDisplayName("&b%player%'s Sword");
meta.setLore(Arrays.asList("&7Owner: %player%", "&7Kills: %kills%"));
item.setItemMeta(meta);

ItemStack parsed = PlaceHolderHelper.parsePlaceholders(
    item,
    "%player%", player.getName(),
    "%kills%", "42"
);
```

---

## BungeeCord Integration

**BungeeHelper - BungeeCord Communication:**

```java
BungeeHelper bungee = new BungeeHelper(plugin);

// Connect player to server
bungee.connectToServer(player, "lobby");

// Send BungeeCord command
bungee.sendBungeeCommand(player, "alert Hello everyone!");

// Send message to player
bungee.sendPlayerMessage(player, "TargetPlayer", "Hello!");

// Kick player
bungee.kickPlayer(player, "TargetPlayer", "You have been kicked");

// Cleanup
bungee.cleanup();
```

**Important:** Player must be online to send BungeeCord messages.

---

## Version Compatibility

The API automatically detects server version and adjusts functionality:

```java
// Check version features
if (VersionHelper.HAS_PLAYER_PROFILES) {
    // Use 1.18.1+ PlayerProfile API
}

if (VersionHelper.IS_HEX_VERSION) {
    // Use hex colors (1.16+)
}

if (VersionHelper.IS_CUSTOM_MODEL_DATA) {
    // Use custom model data (1.14+)
}

if (VersionHelper.HAS_DATA_COMPONENTS) {
    // Use data components (1.20.5+)
}
```

**Version Constants:**
- `CURRENT_VERSION` - Current server version
- `HAS_TOOLTIP_STYLE` - 1.21.2+ features
- `HAS_DATA_COMPONENTS` - 1.20.5+ features
- `HAS_ARMOR_TRIMS` - 1.19.4+ features
- `HAS_PLAYER_PROFILES` - 1.18.1+ features
- `IS_HEX_VERSION` - 1.16+ hex colors
- `IS_CUSTOM_MODEL_DATA` - 1.14+ custom model data
- `IS_PDC_VERSION` - 1.14+ persistent data
- `IS_ITEM_LEGACY` - Pre-1.13 items
- `IS_SKULL_OWNER_LEGACY` - Pre-1.12 skull API

---

## Best Practices

### 1. Always provide plugin instance
```java
// Good
KItem item = new KItem(plugin, Material.DIAMOND);

// Bad (features may not work)
KItem item = new KItem(Material.DIAMOND);
```

### 2. Use color codes consistently
```java
// Use ChatFormater for all colored strings
String msg = ChatFormater.color("&aSuccess!");
```

### 3. Handle null returns
```java
Location loc = klocation.getLocation();
if (loc != null) {
    player.teleport(loc);
}
```

### 4. Close database connections
```java
@Override
public void onDisable() {
    database.disconnect();
}
```

### 5. Use configuration for flexibility
```java
// Store items, menus, messages in config files
// Makes it easy to customize without code changes
```

---

## Example Plugin

```java
public class ExamplePlugin extends JavaPlugin {
    
    private MyConfig config;
    private MyDatabase database;
    private BungeeHelper bungee;
    
    @Override
    public void onEnable() {
        // Load config
        config = new MyConfig(this);
        
        // Setup database
        database = new MyDatabase(config.getConfig());
        
        // Register commands
        getCommand("shop").setExecutor(new ShopCommand(this));
        
        // Setup BungeeCord
        bungee = new BungeeHelper(this);
        
        getLogger().info("Plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        database.disconnect();
        bungee.cleanup();
        getLogger().info("Plugin disabled!");
    }
}
```

---

## Support & Credits

This API provides a solid foundation for Bukkit/Spigot plugin development with modern Java practices and extensive version compatibility (1.12 - 1.21.4+).

For issues or questions, refer to the source code documentation or create an issue in your repository.
