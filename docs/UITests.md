## Run UI tests

If members what to verify that their changes did not affect the User Interface you can run the UI tests by following the steps below.

1. Add the monkeyrunner command to your path.  It is located in your android skds' tools directory
2. Connect a phone or start the emulator
3. From the root directory run the command `monkeyrunner monkeyTest.py`
4. If no errors appear then your good!

Test output will be placed in the test_results. 

## Writing UI Tests Guidelines

1. Tests should put all output in the test_results folder
2. All output should be written to not overwrite previous tests
3. Tests should be added when new screens are added
4. If you break the UI and the tests pass add a new test
