# Permissions Reference

## CozyHub Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `cozyhub.use` | Allows using the `/hub` command | `true` |
| `cozyhub.setspawn` | Allows setting the hub spawn location | `op` |
| `cozyhub.admin` | Allows reloading the plugin configuration | `op` |
| `cozyhub.visibility` | Allows toggling player visibility | `true` |
| `cozyhub.doublejump` | Allows using the double jump feature | `true` |

## Permission Nodes

### cozuhub.use
- **Type:** boolean
- **Default:** true
- **Description:** Required to use `/hub` for teleportation

### cozyhub.setspawn
- **Type:** boolean
- **Default:** op
- **Description:** Required to use `/hub setspawn`

### cozyhub.admin
- **Type:** boolean
- **Default:** op
- **Description:** Required to use `/hub reload`

### cozyhub.visibility
- **Type:** boolean
- **Default:** true
- **Description:** Required to use `/visibility`

### cozyhub.doublejump
- **Type:** boolean
- **Default:** true
- **Description:** Required for double jump to work (must also have `protection.hub-fly` enabled in config)

## Setup Permissions

### LuckPerms Example

```lp user <player> permission set cozyhub.admin true
lp group default permission set cozyhub.use true
lp group default permission set cozyhub.visibility true
lp group default permission set cozyhub.doublejump true
lp group admin permission set cozyhub.setspawn true
lp group admin permission set cozyhub.admin true
```

### EssentialsX Example

```
essentials:perm set <player> cozyhub.admin true
```
