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

def usage(exitVal=1):
    print >> sys.stderr, 'usage: dump.py [-H|--%s] [-V|--%s] [-I|--%s] [-F|--%s] [-S|--%s] [-u|--%s] [-x|--%s] [-d|--%s] [-c|--%s] [serialno]' % \
        tuple(LONG_OPTS)
    sys.exit(exitVal)

#try:
#    opts, args = getopt.getopt(sys.argv[1:], 'HVIFSuxdc', LONG_OPTS)
#except getopt.GetoptError, e:
#    print >>sys.stderr, 'ERROR:', str(e)
#    usage()

kwargs1 = {'verbose': True, 'ignoresecuredevice': False}
kwargs2 = {'forceviewserveruse': False, 'startviewserver': True}

vc = ViewClient(*ViewClient.connectToDeviceOrExit(**kwargs1), **kwargs2)
vc.traverse(transform=ViewClient.TRAVERSE_CITCD)
vc.findViewWithContentDescriptionOrRaise('Create').touch()
vc.dump()
vc.traverse(transform=ViewClient.TRAVERSE_CITCD)
vc.findViewWithContentDescriptionOrRaise('Navigate up').touch()
vc.findViewWithTextOrRaise('Music Library').touch()
vc.dump()
vc.traverse(transform=ViewClient.TRAVERSE_CITCD)
vc.findViewByIdOrRaise('id/btn_add_to_playlist').touch()
vc.findViewByIdOrRaise('id/home').touch()
vc.findViewWithTextOrRaise('Playlist').touch()
vc.dump()
vc.findViewByIdOrRaise('id/btn_play_pause').touch()
