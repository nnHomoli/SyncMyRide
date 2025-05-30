# SyncMyRide

<img src="gallery/icon_wide.png" width="960" height="480" alt="mod icon, but wider">

This mod is intended for "[Better Than Adventure!](https://www.betterthanadventure.net/)" fork-mod 

While there could be better implementation by modding both client & server, this one is oriented to be handled by server only

Because of the above, it might affect performance a bit

## Features

- Players now receive vehicle packets when they load a new entity
- Ride Height synchronization by using dummies


- /ride command
- Also supports entities


- Other small related improvements & fixes

<img src="gallery/comparison.gif" width="480" height="250" alt="comparison gif">

## Config

```
vehicle-delay=int
```
Delay after which the server will send an initial vehicle update to the player for the new entity

By default, this value is 5
```
dummy-id=int
```
Item ID that dummies are using to fill the gaps

By default, this value is 16415, which is a "String" item

## Gallery

<img src="gallery/zomobu.png" width="480" height="270" alt="Zombie riding on player">
<img src="gallery/careepo.png" width="480" height="270" alt="Creeper riding on player">

<img src="gallery/playeru.png" width="497" height="198" alt="BetterTogether with SyncMyRide">
