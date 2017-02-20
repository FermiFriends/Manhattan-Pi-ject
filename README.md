#Manhattan Pi-ject

###IT'S (not) A BOMB!
The attacker hides the bomb and arms it with a custom disarm profile.

The defender has limited time to find (with the help of bluetooth signal strength) and disarm it.

The bomb is complicated.

There are 4 conditions to be simultaneously satisfied before it can become fully disarmed.

Swing by to have a go at disarming it yourself!

##How we built it?
With surprisingly great difficulty...

        Android App 
            |
          (REST)
            |
      Raspberry Pi (Flask)
        |              |
    (serial)     GPIO (7-Segment)
        |
     Arduino
        |
    (electrons?)
        |
      Sensors
  
In particular, co-ordinating a load of device threads with the network stuff on the Pi was a bit harder than we anticipated.

##What's next for The Manhattan Pi-ject
More traps! More interface!
