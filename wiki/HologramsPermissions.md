# CozyHolograms Permissions

## Permission Nodes

| Permission | Description | Default |
|------------|-------------|---------|
| `cozyholo.use` | Allows using the /holo command | `true` |
| `cozyholo.create` | Allows creating holograms | `op` |
| `cozyholo.delete` | Allows deleting holograms | `op` |
| `cozyholo.edit` | Allows editing holograms (edit, addline, removeline, teleport) | `op` |
| `cozyholo.list` | Allows listing and viewing hologram info | `true` |
| `cozyholo.admin` | Allows admin commands (reload) | `op` |

## Setup Examples

### LuckPerms

```
/lp user <player> permission set cozyholo.create true
/lp user <player> permission set cozyholo.edit true
/lp group default permission set cozyholo.use true
/lp group default permission set cozyholo.list true
/lp group admin permission set cozyholo.create true
/lp group admin permission set cozyholo.delete true
/lp group admin permission set cozyholo.edit true
/lp group admin permission set cozyholo.admin true
```

### Player can create and edit, but not delete

```
cozyholo.use: true
cozyholo.create: true
cozyholo.edit: true
cozyholo.list: true
cozyholo.delete: false
cozyholo.admin: false
```
