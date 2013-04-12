#! /usr/bin/env monkeyrunner
"""this requires ui_test.py to run"""

def printLayout(vc):
    vc.traverse(transform=ViewClient.TRAVERSE_CITCD)

def touchWithCD(vc, contentDescription):
    vc.findViewWithContentDescriptionOrRaise(contentDescription).touch()
    vc.dump()
