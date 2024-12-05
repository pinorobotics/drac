# Version 1

- Adding documentation for DracMetrics
- Moving Message class under impl package
- Adding javadoc
- Moving velocity, acceleration and jerk to DornaClientConfig
- Support async jmove
- Cache last serialized message
- Adding metrics
- Adding Joints::toArrayOfRadians
- Verify joint limits before each move command
- Adding Joints constructor for 5 joints
- Adding support for different Dorna models
- Adding warning before turning off the motor
- Implementing play()
- Allow users to change velocity/accel/jerk offline
- Adding support of outputLog to let users record all commands sent to Dorna arm into log file
- Implementing motor command
- Fixing jmove waiting result indefinitely
- Fixing getLastMotion returns null when no motion yet received
- Adding jmove command with tests
- Adding list of predefined Dorna command statuses
- Implement proper shutdown of all CommandServerWebSocketMock internal threads when socket is closed
- Implementing CommandServerWebSocketMock, adding client tests for version and joint
- Handle all WebSocket logic (logging, exception handling) in DracSocket
- Adding test project
- Move client implementation behind interface
- Implementing "joint" command
- It is time

[drac-v1.0.zip](https://github.com/pinorobotics/drac/raw/main/drac/release/drac-v1.0.zip)