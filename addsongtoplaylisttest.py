#! /usr/bin/env monkeyrunner
'''
Copyright (C) 2012  Diego Torres Milano
Created on Feb 3, 2012

@author: diego
'''


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

HELP = 'help'
VERBOSE = 'verbose'
IGNORE_SECURE_DEVICE = 'ignore-secure-device'
FORCE_VIEW_SERVER_USE = 'force-view-server-use'
DO_NOT_START_VIEW_SERVER = 'do-not-start-view-server'
UNIQUE_ID = 'uniqueId'
POSITION = 'position'
CONTENT_DESCRIPTION = 'content-description'
CENTER = 'center'
MAP = {'u':ViewClient.TRAVERSE_CITUI, UNIQUE_ID:ViewClient.TRAVERSE_CITUI,
       'x':ViewClient.TRAVERSE_CITPS, POSITION:ViewClient.TRAVERSE_CITPS,
       'd':ViewClient.TRAVERSE_CITCD, CONTENT_DESCRIPTION:ViewClient.TRAVERSE_CITCD,
       'c':ViewClient.TRAVERSE_CITC, CENTER:ViewClient.TRAVERSE_CITC,
       }
LONG_OPTS =  [HELP, VERBOSE, IGNORE_SECURE_DEVICE, FORCE_VIEW_SERVER_USE, DO_NOT_START_VIEW_SERVER,
                UNIQUE_ID, POSITION, CONTENT_DESCRIPTION, CENTER]

def usage(exitVal=1):
    print >> sys.stderr, 'usage: dump.py [-H|--%s] [-V|--%s] [-I|--%s] [-F|--%s] [-S|--%s] [-u|--%s] [-x|--%s] [-d|--%s] [-c|--%s] [serialno]' % \
        tuple(LONG_OPTS)
    sys.exit(exitVal)

try:
    opts, args = getopt.getopt(sys.argv[1:], 'HVIFSuxdc', LONG_OPTS)
except getopt.GetoptError, e:
    print >>sys.stderr, 'ERROR:', str(e)
    usage()

kwargs1 = {VERBOSE: False, 'ignoresecuredevice': False}
kwargs2 = {'forceviewserveruse': True, 'startviewserver': True}
transform = ViewClient.TRAVERSE_CIT
for o, a in opts:
    o = o.strip('-')
    if o in ['H', HELP]:
        usage(0)
    elif o in ['V', VERBOSE]:
        kwargs1[VERBOSE] = True
    elif o in ['I', IGNORE_SECURE_DEVICE]:
        kwargs1['ignoresecuredevice'] = True
    elif o in ['F', FORCE_VIEW_SERVER_USE]:
        kwargs2['forceviewserveruse'] = True
    elif o in ['S', DO_NOT_START_VIEW_SERVER]:
        kwargs2['startviewserver'] = False
    else:
        transform = MAP[o]

vc = ViewClient(*ViewClient.connectToDeviceOrExit(**kwargs1), **kwargs2)
# vc.traverse(transform=transform)

vc.findViewByIdOrRaise('id/btn_playlist').touch()
vc.dump()
vc.traverse(transform=transform)
vc.findViewByIdOrRaise('id/home').touch()
vc.findViewWithTextOrRaise('Music Library').touch()
vc.dump()
vc.findViewByIdOrRaise('id/add_to_playlist').touch()
vc.findViewByIdOrRaise('id/home').touch()
vc.findViewWithTextOrRaise('Playlist').touch()
vc.dump()
vc.findViewByIdOrRaise('id/btn_play_pause').touch()
