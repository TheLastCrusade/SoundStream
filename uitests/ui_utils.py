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
ATP_AVALANCHE_ROCK = 'Add To Playlist_Avalanche Rock'
ATP_I_M_SO_SAD = 'Add To Playlist_I\'m So Sad, So Very, Very, Sad'
ATP_ROLLING_PAPERS = 'Add To Playlist_Rolling Papers (feat Wolf Haley)'
ATP_THE_IRONMAN_TAKEOVER = 'Add To Playlist_The Ironman Takeover (Skit)'
ATP_DONUTS = 'Add To Playlist_Donuts (Outro)'
ATP_ONE_LAST = 'Add To Playlist_One Last "Whoo-Hoo!" for the Pullman'

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
