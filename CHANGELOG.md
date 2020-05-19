# Changelog

## [1.1] - May 19, 2020
- remove some redundant codes
- start service when a song is played
- avoid to close the notification when a song is ended (it can be optional)
- update exoplayer from `2.9.2` to '2.11.4'
- update buildToolsVersion from `29.0.1` to `29.0.3`
- update kotlin version from `1.3.61` to `1.3.71`
- modify play function in ExoPlayerManager
- android:requestLegacyExternalStorage="true" in AndroidManifest.xml due to external storage access limitation in android 10
- solve issues relating MediaNotificationManager


## [1.0] - March 30, 2020
- solved some issues related to:
   - close the notification when a song is ended
   - removed calling updateNotification() when not needed
   - handled skip to next operation when list ended
   - added clear queue function
   - handled add parent stack in MediaNotificationManager
   
