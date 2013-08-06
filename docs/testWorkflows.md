# Using 1 Phone #

## Test -1: Add song to playlist and play (already installed and opened)##

- Press "Create” button
- Press "Music Library”
- Press "Add To Playlist” icon for one song
- Press the "Soundstream” icon in the top corner
- Press "Playlist”
- Press the "Play” icon in the playbar

### Expected Results:###
 The song is in the playlist and plays

## Test 0: Install and open the app ##

- Install the app on the phone
- Open up the app on the phone

### Expected Results: ###
 The screen displays the 2 buttons to create and join a network

## Test 1: Add song to playlist and play ##

- *Complete Test 0*
- Press "Create” button
- Press "Music Library”
- Press "Add To Playlist” icon for one song
- Press the "Soundstream” icon in the top corner
- Press "Playlist”
- Press the "Play” icon in the playbar

### Expected Results: ###
 The song is in the playlist and plays

## Test 2: Songs play in order and loop back ##

- *Complete Test 0*
- Press "Create” button
- Press "Music Library”
- Press "Add To Playlist” icon for one song
- Press "Add To Playlist” icon for a different song
- Press the "Soundstream” icon in the top corner
- Press "Playlist”
- Press the "Play” icon in the playbar

### Expected Results: ###
 The first song plays. Once the first song is done, the second song starts to play

## Test 3: Skip songs ##

- *Complete Test 0*
- Press "Create” button
- Press "Music Library”
- Press "Add To Playlist” icon for one song
- Press "Add To Playlist” icon for a different song
- Press "Add To Playlist” icon for a third song
- Press the "Soundstream” icon in the top corner
- Press "Playlist”
- Press the "Skip” icon in the playbar
- Press the "Play” icon in the playbar
- Press the "Skip” icon in the playbar 3 seconds after

### Expected Results: ###
 The first song skips without playing and the second song plays for a second and then skips to the third once the second song plays for 3 seconds

## Test 4: Remove song from the playlist ##

- *Complete Test 0*
- Press "Create” button
- Press "Music Library”
- Press "Add To Playlist” icon for one song
- Press the "Soundstream” icon in the top corner
- Press "Playlist”
- Swipe the song off the playlist
- Press the "Play” icon in the playbar

### Expected Results: ###
 There is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 5: Remove song from the middle of the playlist ##

- *Complete Test 0*
- Press "Create” button
- Press "Music Library”
- Press "Add To Playlist” icon for one song
- Press "Add To Playlist” icon for a different song
- Press "Add To Playlist” icon for a third song
- Press the "Soundstream” icon in the top corner
- Press "Playlist”
- Swipe the middle song off the playlist
- Press the "Play” icon in the playbar

### Expected Results: ###
 The first song plays. Once it is done, the third song added plays

## Test 6: Remove a song that has already been played ##

- *Complete Test 0*
- Press "Create” button
- Press "Music Library”
- Press "Add To Playlist” icon for one song
- Press "Add To Playlist” icon for a different song
- Press the "Skip” button in the playbar
- Press the "Soundstream” icon in the top corner
- Press "Playlist”
- Swipe the first song off the playlist

### Expected Results: ###
 The second song added should play again when the playlist loops around

## Test 7: Rotation and pausing the application ##

- *Complete Test 0*
- *Complete Test 2*
- Press the "Skip” icon in the playbar
- Rotate the phone in landscape mode
- Press the "Home” button on the phone
- Reopen the app

### Expected Results: ###
 The playlist and the playbar reflect the current state of the app after rotation and reopening the app

## Test 8. About Fragment ##

- Open the app on the phone
- Press "Create” button
- Press "About”

### Expected Results: ###
 The app displays the "About” fragment

# Using 2 Phones #
* Note: Phone A is the host and phone B is a guest *

## Test 9: Connect 2 phones ##

- *Complete Test 0 on phone A*
- *Complete Test 0 on phone B* 
- Press "Create” button on phone A
- Press "Network” on phone A
- Press "Join” on phone B
- Press "Yes” on phone B
- Press "Add Members to Network” button on phone A
- Check the box for phone B on phone A and press "Select”

### Expected Results: ###
 Phone B goes into the drawer of the app. Both phones see both phones in the userlist in the menu fragment and the network fragment. Both phones see the collective music library. Songs in the music library should have the same color as the user. Both phones see the same playlist with song colors matching user colors. "Disband” button displays in the Network fragment on phone A. "Disconnect” button displays in the Network fragment on phone B.

## Test 10a: Connect 2 phones and play a song from the host phone’s music library ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” for one song from phone A’s music library using phone A
- Press "Play” in the playbar of phone A

### Expected Results: ###
 The song plays on the host phone. Both phones see the song in the playlist. Both phones see the title of the song and the "Pause” icon in their playbars.

## Test 10b: Connect 2 phones and play a song from the host’s music library with action(s) initiated by guest ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” for one song from phone A’s music library using phone B
- Press "Play” in the playbar of phone B

### Expected Results: ###
 The song plays on the host phone. Both phones see the song in the playlist. Both phones see the title of the song and the "Pause” icon in their playbars.

## Test 11: Songs play in order and loop back with 2 phones ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” for one song from phone A’s music library using phone A
- Press "Add To Playlist” for a different song from phone A’s music library using phone A
- Press "Play” in the playbar of phone A

### Expected Results: ###
 The first song plays on phone A. Once the first song is done, the second song starts to play. The state of the playlist and playbar are reflected on both phones.

## Test 12a: Skip songs with 2 phones ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library using phone A
- Press "Add To Playlist” icon for a different song from phone A’s music library using phone A
- Press "Add To Playlist” icon for a third song from phone A’s music library using phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Press the "Skip” icon in the playbar on phone A
- Press the "Play” icon in the playbar on phone A
- Press the "Skip” icon in the playbar 3 seconds after on phone A

### Expected Results: ###
 The first song skips without playing and the second song plays for a second and then skips to the third once the second song plays for 3 seconds. The state of the playlist and playbar are reflected on both phones.

## Test 12b: Skip songs with action(s) initiated by guest ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone B
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for one song from phone A’s music library using phone B
- Press "Add To Playlist” icon for a different song from phone A’s music library using phone B
- Press "Add To Playlist” icon for a third song from phone A’s music library using phone B
- Press the "Soundstream” icon in the top corner on phone B
- Press "Playlist” on phone B
- Press the "Skip” icon in the playbar on phone B
- Press the "Play” icon in the playbar on phone B
- Press the "Skip” icon in the playbar 3 seconds after on phone B

### Expected Results: ###
 The first song skips without playing and the second song plays for a second and then skips to the third once the second song plays for 3 seconds. The state of the playlist and playbar are reflected on both phones.

## Test 13a: Remove song from the playlist with 2 phones ##

- *Complete Test 9
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song on phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Swipe the song off the playlist on phone A
- Press the "Play” icon in the playbar on phone A

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 13b: Remove song from the playlist with action(s) initiated by guest ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for one song from phone A’s music library using phone B
- Press the "Soundstream” icon in the top corner on phone B
- Press "Playlist” on phone B
- Swipe the song off the playlist on phone B
- Press the "Play” icon in the playbar on phone B

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 14a: Remove song from the middle of the playlist with 2 phones ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library using phone A
- Press "Add To Playlist” icon for a different song from phone A’s music library using phone A
- Press "Add To Playlist” icon for a third song from phone A’s music library using phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Swipe the middle song off the playlist on phone A
- Press the "Play” icon in the playbar on phone A

### Expected Results: ###
 The first song plays. Once it is done, the third song added plays

## Test 14b: Remove song from the middle of the playlist with action(s) initiated by guest ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for one song from phone A’s music library using phone B
- Press "Add To Playlist” icon for a different song from phone A’s music library using phone B
- Press "Add To Playlist” icon for a third song from phone A’s music library using phone B
- Press the "Soundstream” icon in the top corner on phone B
- Press "Playlist” on phone B
- Swipe the middle song off the playlist on phone B
- Press the "Play” icon in the playbar on phone B

### Expected Results: ###
 The first song plays. Once it is done, the third song added plays


## Test 15: Add a guest into an existing, single-device network ##

- *Complete Test 0 on phone A*
- Press "Create” button on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song on phone A
- Press "Add To Playlist” icon for a different song on phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Press the "Play” icon in the playbar on phone A
- *Complete Test 0 for phone B
- Press "Network” on phone A
- Press "Join” on phone B
- Press "Yes” on phone B
- Press "Add Members to Network” button on phone A
- Check the box for phone B on phone A and press "Select”

### Expected Results: ###
 Both phones see the same state in the playlist and playbar.

## Test 16a: Transfer a song from the guest phone to the host and play ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” for one song from phone B’s music library using phone A
- Once the song is finished transferring, press "Play” in the playbar of phone A

### Expected Results: ###
 The song plays on the host phone. Both phones see the song in the playlist. Both phones see the title of the song and the "Pause” icon in their playbars.

## Test 16b: Transfer a song from the guest phone to the host and play with action(s) initiated by guest ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” for one song from phone B’s music library using phone B
- Once the song is finished transferring, press "Play” in the playbar of phone B

### Expected Results: ###
 The song plays on the host phone. Both phones see the song in the playlist. Both phones see the title of the song and the "Pause” icon in their playbars.

## Test 17a: Play partially transferred song ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” for one song from phone B’s music library using phone A
- Press "Play” in the playbar of phone A

### Expected Results: ###
 The song plays on the host phone. Both phones see the song in the playlist. Both phones see the title of the song and the "Pause” icon in their playbars.

## Test 17b: Play partially transferred song with action(s) initiated by guest ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” for one song from phone B’s music library using phone B
- Once the song is finished transferring, press "Play” in the playbar of phone B

### Expected Results: ###
 The song plays on the host phone. Both phones see the song in the playlist. Both phones see the title of the song and the "Pause” icon in their playbars.

## Test 18a: Bump transferring behind next available song ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library using phone A
- Press "Add To Playlist” icon for one song from phone B’s music library using phone A
- Press "Add To Playlist” icon for a second song from phone A’s music library using phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Press the "Play” icon in the playbar on phone A
- In 3 seconds; press the "Skip” icon in the playbar on phone A
- In 3 seconds; press the "Skip” icon in the playbar on phone A

### Expected Results: ###
 The first song plays for 3 seconds. If the second song has not finished transferring, then it is bumped down past the next available song. If the only songs next in the playlist are songs that are not finished transferring at the end of the playlist, then the playlist loops to the top and starts playing from the beginning.

## Test 18b: Bump transferring song behind next available when next available is more than 2 songs away ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library using phone A
- Press "Add To Playlist” icon for one song from phone B’s music library using phone A
- Press "Add To Playlist” icon for another song from phone B’s music library using phone A
- Press "Add To Playlist” icon for a second song from phone A’s music library using phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Press the "Play” icon in the playbar on phone A
- In 3 seconds; press the "Skip” icon in the playbar on phone A
- In 3 seconds; press the "Skip” icon in the playbar on phone A

### Expected Results: ###
 The first song plays for 3 seconds. If the second song has not finished transferring, then it is bumped down past the next available song. If the only songs next in the playlist are songs that are not finished transferring at the end of the playlist, then the playlist loops to the top and starts playing from the beginning.

## Test 19a: Remove transferred song added by host ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone B’s music library on phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Once the song is finished transferring, swipe the song off the playlist on phone A
- Press the "Play” icon in the playbar on phone A

### Expected Results: ###
 There is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 19b: Remove transferred song added by host with action(s) initiated by guest ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone B’s music library on phone A
- Press "Playlist” on phone B
- Once the song is finished transferring, swipe the song off the playlist on phone B
- Press the "Play” icon in the playbar on phone B

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 19c: Remove transferred song added by guest with action(s) initiated by host ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for one song from phone B’s music library on phone B
- Press the "Soundstream” icon on phone A
- Press "Playlist” on phone A
- Once the song is finished transferring, swipe the song off the playlist on phone A
- Press the "Play” icon in the playbar on phone A

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”


## Test 19d: Remove transferred song added by guest with action(s) initiated by guest ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for one song from phone B’s music library on phone B
- Press the "Soundstream” icon on phone B
- Press "Playlist” on phone B
- Once the song is finished transferring, swipe the song off the playlist on phone B
- Press the "Play” icon in the playbar on phone B

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 20a: Remove partially transferred song added by host ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone B’s music library on phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Swipe the song off the playlist on phone A
- Press the "Play” icon in the playbar on phone A

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 20b: Remove partially transferred song added by host with action(s) initiated by guest ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone B’s music library on phone A
- Press "Playlist” on phone B
- Swipe the song off the playlist on phone B
- Press the "Play” icon in the playbar on phone B

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 20c: Remove partially transferred song added by guest with action(s) initiated by host ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for one song from phone B’s music library on phone B
- Press the "Soundstream” icon on phone A
- Press "Playlist” on phone A
- Swipe the song off the playlist on phone A
- Press the "Play” icon in the playbar on phone A

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 20d: Remove partially transferred song added by guest with action(s) initiated by guest ##

- *Complete Test 9*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for one song from phone B’s music library on phone B
- Press the "Soundstream” icon on phone B
- Press "Playlist” on phone B
- Once the song is finished transferring, swipe the song off the playlist on phone B
- Press the "Play” icon in the playbar on phone B

### Expected Results: ###
 Upon removal of last song, a toast appears instructing the user to add songs to the playlist. Upon pressing play, there is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 21: Disconnect guest with user action (pressing disconnect button) ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library on phone A
- Press "Network” on phone B
- Press the "Disconnect” button on phone B
- Press "Create” on phone B

### Expected Results: ###
 Phone B goes back to the start screen and then creates a new network. The playlist on phone B is empty. The userlist on both phones only show the individual phones. The music libraries on both phones only show music from the individual phones. There is no "Disband” button in the network fragment for phone A.

## Test 22: Disconnect guest by dropping the bluetooth socket ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library on phone A
- Phone B removes the app from memory
- Phone B opens the app
- Press "Create” on phone B

### Expected Results: ###
 Phone B goes back to the start screen and then creates a new network. The playlist on phone B is empty. The userlist on both phones only show the individual phones. The music libraries on both phones only show music from the individual phones. There is no "Disband” button in the network fragment for phone A.

## Test 23: Play songs transferred from disconnected guest ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone B’s music library on phone A
- Once the song is finished transferring, press "Network” on phone B
- Press the "Disconnect” button on phone B
- Press "Create” on phone B
- Press the "Play” icon in the playbar on phone A

### Expected Results: ###
 The song has no color associated with it, but it plays

## Test 24: Disconnect guest while song is transferring from guest ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone B’s music library on phone A
- Press "Network” on phone B
- Press the "Disconnect” button on phone B
- Press "Create” on phone B
- Press the "Play” icon in the playbar on phone A
- Reconnect phone B to phone A's Network

### Expected Results: ###
 The partially transferred song is removed from the playlist when phone B disconnects.  Phone B reconnects succesfully to phone A's network.

## Test 25: Disband ##

- *Complete Test 9*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library on phone A
- Press "Network” on phone A
- Press the "Disband” button on phone A
- Press "Create” on phone A

### Expected Results: ###
 Phone A has a new network with no other users and no songs in the playlist. There is no "Disband” button in the network fragment for phone A. Phone B is transferred back to the start screen and has a new network with no other users and no songs in the playlist. There is no "Disband” button in the network fragment for phone B. 

# Using 3 Phones #

## Test 26: Connect 3 phones using host to add ##

- *Complete Test 9 with phone A and phone B*
- Press the "Add Members to Network” button on phone A
- Press "Join” on phone C
- Press "Yes” on phone C
- Check the box for phone C on phone A and press "Select”

### Expected Results: ###
 Phone B are C go into the drawer of the app. All phones see both phones in the userlist in the menu fragment and the network fragment. All phones see the collective music library. Songs in the music library should have the same color as the user. Both phones see the same playlist with song colors matching user colors. "Disband” button displays in the Network fragment on phone A. "Disconnect” button displays in the Network fragment on phone B and C.

## Test 27: Remotely add a third phone ##

- *Complete Test 9 with phone A and phone B*
- Press "Network” on phone B
- Press the "Add Members to Network” button on phone B
- Press "Join” on phone C
- Press "Yes” on phone C
- Check the box for phone C on phone B and press "Select”

### Expected Results: ###
 Phone B are C go into the drawer of the app. All phones see both phones in the userlist in the menu fragment and the network fragment. All phones see the collective music library. Songs in the music library should have the same color as the user. Both phones see the same playlist with song colors matching user colors. "Disband” button displays in the Network fragment on phone A. "Disconnect” button displays in the Network fragment on phone B and C.

## Test 28: Play events and status synced between all phones ##

- *Complete Test 26*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for a song from phone A’s music library using phone B
- Press "Add To Playlist” icon for a different song from phone A’s music library using phone B
- Press "Play” in the playbar on phone B
- Press "Skip” in the playbar on phone B
- Press "Pause” in the playbar on phone C

Expected Resutls: The phones should reflect the current state of the playing song. The song should play, skip, then pause.

## Test 29: Add and remove events synced between all phones ##

- *Complete Test 26*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for a song from phone A’s music library using phone B
- Press "Add To Playlist” icon for a different song from phone A’s music library using phone B
- Press the "Soundstream” icon on phone B
- Press "Playlist” on phone B
- Press "Playlist” on phone C
- Swipe the second song off the playlist on phone B
- Swipe the first song off the playlist on phone C
- Press the "Play” icon on phone A

### Expected Results: ###
 There is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 30: Add a second guest into an existing network ##

- *Complete Test 9 with phone A and phone B*
- Press the "Soundstream” icon in the top corner on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song on phone A
- Press "Add To Playlist” icon for a different song on phone A
- Press the "Soundstream” icon in the top corner on phone A
- Press "Playlist” on phone A
- Press the "Play” icon in the playbar on phone A
- Press the "Soundstream” icon in the top corner on phone A
- *Complete Test 0 with phone C
- Press "Network” on phone A
- Press "Join” on phone C
- Press "Yes” on phone C
- Press "Add Members to Network” button on phone A
- Check the box for phone C on phone A and press "Select”

### Expected Results: ###
 All phones see the same state in the playlist and playbar.

## Test 31: Transfer song events synced between all phones ##

- *Complete Test 26*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for a song from phone A’s music library using phone B
- Press "Add To Playlist” icon for a song from phone B’s music library using phone B

### Expected Results: ###
 The added song should display on all phones as transferring while it is transferring. Once it is done transferring, all phones should display the song as ready to play.

## Test 32: Transfer a song from a different guest ##

- *Complete Test 26*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for a song from phone B’s music library using phone C

### Expected Results: ###
 Song transfers to the host and displays as ready to play on all phones

## Test 33: Remove a transferred song added by a different guest ##

- *Complete Test 26*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for a song from phone B’s music library using phone C
- Press "Playlist” on phone B
- Once the song is finished transferring, swipe the song off the playlist on phone B
- Press the "Play” icon on phone A

### Expected Results: ###
 There is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 34: Remove a partially transferred song added by a different guest ##

- *Complete Test 26*
- Press "Music Library” on phone B
- Press "Add To Playlist” icon for a song from phone B’s music library using phone C
- Press "Playlist” on phone B
- Swipe the song off the playlist on phone B
- Press the "Play” icon on phone A

### Expected Results: ###
 There is no song in the playlist and a toast appears saying "Playlist is empty”

## Test 35: Disconnect one guest with user action (pressing disconnect button) ##

- *Complete Test 26*
- Press "Network” on phone B
- Press the "Disconnect” button on phone B
- Press the "Create” button on phone B

### Expected Results: ###
 Phone B goes back to the start screen and then creates a new network. The playlist on phone B is empty. The userlist on phone B only shows phone B while the userlist on phones A and C show phones A and C. The music libraries on both phones only show music from the currently connected phones.

## Test 36: Disconnect one guest by dropping the bluetooth socket ##

- *Complete Test 26*
- Press the "Soundstream” icon on phone A
- Press "Music Library” on phone A
- Press "Add To Playlist” icon for one song from phone A’s music library on phone A
- Phone B removes the app from memory
- Phone B opens the app
- Press "Create” on phone B

### Expected Results: ###
 Phone B goes back to the start screen and then creates a new network. The playlist on phone B is empty. The userlist on phone B only shows phone B while the userlist on phones A and C show phones A and C. The music libraries on both phones only show music from the currently connected phones. There is no "Disband” button in the network fragment for phone B.
