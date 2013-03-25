#! /usr/bin/env monkeyrunner

import sys
import os
import getopt

# This must be imported before MonkeyRunner and MonkeyDevice,
# otherwise the import fails.
# PyDev sets PYTHONPATH, use it
try:
    for p in os.environ['PYTHONPATH'].split(':'):
        if not p in sys.path:
            sys.path.append(p)
except:
    pass
    
try:
    sys.path.append(os.path.join(os.environ['ANDROID_VIEW_CLIENT_HOME'], 'src'))
except:
    pass

from com.dtmilano.android.viewclient import ViewClient
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

device = MonkeyRunner.waitForConnection(60, "emulator-5554")
MonkeyRunner.sleep(5)
vc = ViewClient(device, "emulator-5554", forceviewserveruse=True)

vc.dump()
#vc.traverse(transform=ViewClient.TRAVERSE_CIT)
vc.findViewWithTextOrRaise('Create').touch()
vc.dump()
#vc.traverse(transform=ViewClient.TRAVERSE_CIT)
vc.findViewByIdOrRaise('id/home').touch()
vc.findViewWithTextOrRaise('Music Library').touch()
vc.dump()
vc.findViewWithAttribute('accessibility:getContentDescription()','AddToPlaylist_AvalancheRock').touch()
vc.findViewByIdOrRaise('id/home').touch()
vc.findViewWithTextOrRaise('Playlist').touch()
vc.dump()
vc.findViewByIdOrRaise('id/btn_play_pause').touch()
