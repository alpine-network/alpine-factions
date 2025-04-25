# Alpine Factions

A lightweight factions core for creating the ultimate Minecraft Factions server.

> This plugin is currently in an early state of development and breaking changes **will** occur.

### For Developers
The library can be added as a dependency to your Gradle buildscript like so:

```
repositories {
    maven {
        name 'Alpine Public'
        url 'https://lib.alpn.cloud/alpine-public'
    }
}

dependencies {
    compileOnly 'co.crystaldev:alpine-factions:0.4.9'
}
```

All classes and methods that are part of the API should have Javadocs. If one does not, open an issue.

Keep in mind that using this library will require it to be added as a plugin on any server using your plugin. **DO NOT** shade it into your own plugin.

### For Server Admins
Any extension plugin built using this plugin will require you to add it as a plugin to your server. 

This plugin requires the [AlpineCore](https://github.com/alpine-network/alpine-core) plugin.

The plugin has been explicitly verified to work on releases ranging from `1.8.8` to `1.21.1`.

### License
This library is licensed under the Mozilla Public License v2.0. For information regarding your requirements in the use of this library, please see [Mozilla's FAQ](https://www.mozilla.org/en-US/MPL/2.0/FAQ/).