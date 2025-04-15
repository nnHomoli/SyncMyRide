# SyncMyRide

<img src="gallery/icon_wide.png" width="960" height="480" alt="mod icon, but wider">

This mod synchronizes and implements some riding fixes server-side for "[Better Than Adventure!](https://www.betterthanadventure.net/)" fork-mod

## Config

```
vehicle-delay=int
```
Delay after which the server will send a vehicle update to the new entity already sent to the player

This is delayed because the client needs time to create the entity

By default, this value is 5
```
dummy-id=int
```
Item ID that dummies are using to fill the gaps

By default, this value is 16415, which is a "String" item
