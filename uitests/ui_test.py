#! /usr/bin/env monkeyrunner
import sys
import os
import getopt

class UITest(object):
    """Base class for building UI tests"""
    def __init__(self):
        #super(UITest, self)
        self.setup()
        deviceArg = self.parseArgs()
        if (deviceArg is not None):
            self.connect(deviceArg)
        else:
            self.connect()

    def setup(self):
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

    def print_usage(exitVal=1):
        print >> sys.stderr, 'usage: <test-name>.py [-d|--device]'
        sys.exit(exitVal)

    def parseArgs(self):
        deviceArg = None
        try:
            opts, args = getopt.getopt(sys.argv[1:], "hd:", ["device="])
        except getopt.GetoptError:
            usage()

        for opt, arg in opts:
            if opt == '-h':
                print_usage()
            elif opt in ("-d", "--device"):
                deviceArg = arg

        return deviceArg

    def connect(self, deviceID = 'emulator-5554'):
        #check how we can move this out of this function
        from com.dtmilano.android.viewclient import ViewClient

        kwargs1 = {'verbose': True, 'serialno': deviceID}
        kwargs2 = {'forceviewserveruse': False, 'startviewserver': True}

        self.device, serialno = ViewClient.connectToDeviceOrExit(**kwargs1)
        self.vc = ViewClient(self.device, serialno, **kwargs2)
        
    def execute_test(self):
        raise NotImplementedError( "Should have implemented this in test class" )
