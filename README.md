# Android-Clean-Arch-Room-Koin(MusicPlayer)

A Simple Android Mobile Application which has been implemented using Clean Architecture alongside MVVM design
and consists of the music player for playing (online/offline) songs.


### Technologies & Methodologies which used:

- Koin
- Clean Architecture
- MVVM Pattern
- LiveData
- Coil(Image Loader)
- ExoPlayer


### The flow of player which I implemented:

<br>
<p align="center">
  <img src="https://github.com/ZahraHeydari/MusicPlayer/blob/master/diagram.png" width="600"/>
</p>
<br>


### The App Scenario

To add songs from the device, save them in database and display them in a list. And when a list item
is clicked, the app displays a player page and a player service run. (This player also supports all actions on played song.)
And if you click long on the song item you will be able to remove it from your stored playlist.


### Supported Android Versions

android versions targeted:

- Android 4.0.3 Ice Cream Sandwich(API level 16) or higher


### Used libraries

1. [Github](https://github.com/casidiablo/multidex) - Multidex
2. [Github](https://github.com/InsertKoinIO/koin) - Koin
3. [Github](https://github.com/coil-kt/coil) - Coil
4. [Github](https://github.com/google/ExoPlayer) - ExoPlayer


## Version History

* 1.0 (11/20/2019)- Initial implementation


## License

```
Copyright (c) 2019 ZARA (https://github.com/ZahraHeydari/).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
