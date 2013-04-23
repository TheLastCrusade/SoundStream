#! /usr/bin/env monkeyrunner

# Connect fragment
CREATE = 'Create'

# Menu fragment
NAVIGATE_UP = 'Navigate up'
MUSIC_LIBRARY = 'Music Library'
PLAYLIST = 'Playlist'

# Playbar fragment
PLAY_PAUSE = 'Play/Pause'
SKIP = 'Skip'

# Add to Playlist buttons
# NOTE: these require the sdcard.img file to be loaded on your emulator/phone
ATP_SHORT_1 = 'Add To Playlist_Oh Xmas'
ATP_SHORT_2 = 'Add To Playlist_Beach Party'
ATP_SHORT_3 = 'Add To Playlist_At The Shore'
ATP_MED_1 = 'Add To Playlist_Ether Disco'
ATP_MED_2 = 'Add To Playlist_Montego'
ATP_LONG_1 = 'Add To Playlist_Piece for Disaffected Piano Two'
ATP_LONG_2 = 'Add To Playlist_Controlled Chaos'
ATP_XLONG = 'Add To Playlist_Music for Funeral Home - Part 11'

def print_layout(view_client):
    """prints out a hierarchical view of the current screen with all its components

    Keyword arguments:
    view_client -- the connected android device/emulator

    """
    view_client.traverse(transform=ViewClient.TRAVERSE_CITCD)

def touch_with_cd(view_client, content_description):
    """presses the first component with a matching content description

    Keyword arguments:
    view_client -- the connected android device/emulator
    content_description -- content description of the component you want to press/touch/click
    """
    view_client.findViewWithContentDescriptionOrRaise(content_description).touch()
    view_client.dump()
