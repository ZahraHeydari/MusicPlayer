# Android-Clean-Arch-Room-Koin(MusicPlayer)

A Simple Audio Player Android Application which has been implemented using Clean Architecture alongside MVVM design
to play (online/offline) songs by running a service in the background and displaying a notification at top of the screen.



### The flow of player module which implemented:

<br>
<p align="center">
  <img src="https://github.com/ZahraHeydari/MusicPlayer/blob/master/diagram.png" width="600"/>
</p>
<br>



### The App Scenario:

After selecting songs from your device, they will be saved in database and will be displayed in a playlist. Besides when a list item
is clicked, the song player page is displayed whereas player service is run in the background
and notification will be displayed at top of the screen.(Clicking on notification has been handled!)
Also if you click long on the song item of playlist, it will be removed both from your stored and displayed playlist.


<br>
<p align="center">
  <img src="https://github.com/ZahraHeydari/MusicPlayer/blob/master/player_list_page.jpg" width="250"/>
  <img src="https://github.com/ZahraHeydari/MusicPlayer/blob/master/song_player_page.jpg" width="250"/>
  <img src="https://github.com/ZahraHeydari/MusicPlayer/blob/master/player_notification.jpg" width="250"/>
</p>
<br>



### Technologies & Methodologies which used:

- Koin
- Clean Architecture
- MVVM Pattern
- LiveData
- Coil(Image Loader)
- ExoPlayer
- Mockito



### The features of player module:

- The player service run in the background and can`t be killed until user stops a song.
- The notification of playing song will be diplayed at top of screen. (both collapse/Expand views were supported)
- Player actions in use consists of play/pause, skip to next/previous, repeat one song, repeat all songs,
shuffle songs,...
- Supported swiping to left/right side.



### Supported Android Versions:

- Android 4.0.3 Ice Cream Sandwich(API level 16) or higher



### Used libraries:

1. [Github](https://github.com/casidiablo/multidex) - Multidex
2. [Github](https://github.com/InsertKoinIO/koin) - Koin
3. [Github](https://github.com/coil-kt/coil) - Coil
4. [Github](https://github.com/google/ExoPlayer) - ExoPlayer
5. [Github](https://github.com/mockito/mockito) - Mockito



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
