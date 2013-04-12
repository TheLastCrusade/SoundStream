#! /usr/bin/env monkeyrunner
import sys
import os
import getopt

vc = None
device = None

def usage(exitVal=1):
    print >> sys.stderr, 'usage: <test-name>.py [-d|--device]'
    sys.exit(exitVal)

def setup():
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

def parseArgs():
    deviceArg = None
    try:
        opts, args = getopt.getopt(sys.argv[1:], "hd:", ["device="])
    except getopt.GetoptError:
        usage()

    for opt, arg in opts:
        if opt == '-h':
            usage()
        elif opt in ("-d", "--device"):
            deviceArg = arg

    return deviceArg

def connect(deviceID = 'emulator-5554'):
    from com.dtmilano.android.viewclient import ViewClient

    kwargs1 = {'verbose': True, 'serialno': deviceID}
    kwargs2 = {'forceviewserveruse': False, 'startviewserver': True}

    device, serialno = ViewClient.connectToDeviceOrExit(**kwargs1)
    vc = ViewClient(device, serialno, **kwargs2)

def init():
    setup()
    deviceArg = parseArgs()
    if (deviceArg is not None):
        connect(deviceArg)
    else:
        connect()


def printLayout():
    vc.traverse(transform=ViewClient.TRAVERSE_CITCD)

def touchWithCD(contentDescription):
    vc.findViewWithContentDescriptionOrRaise(contentDescription).touch()
    vc.dump()