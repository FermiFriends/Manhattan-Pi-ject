import Queue
import threading
import ticking
import time
import serial
import json
import copy
from ticking import beep

def start_bomb_session(options):
    
    if options is None:
        options = {
            "TIME_LIMIT": 180,
        
            "TEMP_DELTA": 4,
            "TEMP_RANGE": 3,

            "LIGHT_DELTA": -50,
            "LIGHT_RANGE": 25,

            "PINS_IN_BITS": 0x3,
            "EXPECTED_PINS_OUT_BITS": 0x6,

            "PROXIMITY_DELTA": 200,
            "PROXIMITY_RANGE": 40,

            "NOB_ANGLE": 4,
        }

    """
    EXAMPLE:

    options = {
        "TIME_LIMIT": 180,
    
        "TEMP_DELTA": 4,
        "TEMP_RANGE": 3,

        "LIGHT_DELTA": -50,
        "LIGHT_RANGE": 25,

        "PINS_IN_BITS": 0x3,
        "EXPECTED_PINS_OUT_BITS": 0x6,

        "PROXIMITY_DELTA": 200,
        "PROXIMITY_RANGE": 40,

        "NOB_ANGLE": 4,
    }
    """

    session = {}
    session['start_time'] = time.time()
    session['options'] = options

    session['ticking_stopper'] = ticking.start_ticking(options['TIME_LIMIT'])

    q = Queue.Queue(maxsize=2)
    session['serial_queue'] = q
    stopper = threading.Event()
    t = threading.Thread(target=arduino_serial_reader, args=(q, stopper))
    t.start()
    session['serial_stopper'] = stopper

    line = session['serial_queue'].get()
    while '{' not in line and '}' not in line:
        time.sleep(1)
        print(line)
        line = session['serial_queue'].get()

    print(line)
    session['initial_readings'] = json.loads(line)

    beep(1)

    return session

def session_status(session):

    elapsed = time.time() - session['start_time']

    line = session['serial_queue'].get()
    while line == "":
        time.sleep(1)
        line = session['serial_queue'].get()

    status = json.loads(line)

    # logic checks
    temp = status['TEMPERATURE']
    brightness = status['BRIGHTNESS']
    twisty = status['TWISTY']
    proximity = status['DISTANCE']

    temp_actual_delta = temp - session['initial_readings']['TEMPERATURE']
    brightness_actual_delta = brightness - session['initial_readings']['BRIGHTNESS']
    proximity_actual_delta = proximity - session['initial_readings']['DISTANCE']

    def in_range(actual_delta, target_delta, target_range):
        return actual_delta >= target_delta and actual_delta <= target_delta + target_range

    if elapsed > session['options']['TIME_LIMIT']:
        state = 'DETONATED'

    elif in_range(temp_actual_delta, session['options']['TEMP_DELTA'], session['options']['TEMP_RANGE']) and \
         in_range(brightness_actual_delta, session['options']['LIGHT_DELTA'], session['options']['LIGHT_RANGE']) and \
         in_range(proximity_actual_delta, session['options']['PROXIMITY_DELTA'], session['options']['PROXIMITY_RANGE']) and \
         (twisty / 100) == session['options']['NOB_ANGLE']:

        state = "DISARMED"

    else:
        state = "TICKING"


    if state in ["DISARMED", "DETONATED"]:
        session['ticking_stopper'].set()
        session['serial_stopper'].set()

        if state == "DISARMED":
            #beep(1)
            pass

        elif state == "DETONATED":
            #beep(1)
            pass

    newstate = copy.copy(session['options'])
    newstate["STATE"] = state
    newstate["TIME_ELAPSED"] = elapsed
    newstate["TEMP_ACTUAL_DELTA"] = temp_actual_delta
    newstate["LIGHT_ACTUAL_DELTA"] = brightness_actual_delta
    newstate["PROXIMITY_ACTUAL_DELTA"] = proximity_actual_delta
    newstate["ACTUAL_NOB_ANGLE"] = twisty/100

    return newstate

    """
    Example status JSON:

    options = {
        "STATE": "TICKING"  # (or "DETONATED" or "DISARMED")

        "TIME_LIMIT": 180,
        "TIME_ELAPSED": 108,
    
        "TEMP_DELTA": 4,
        "TEMP_RANGE": 3,
        "TEMP_ACTUAL_DELTA": 2,  # i.e. too low - it's below 4

        "LIGHT_DELTA": -50,
        "LIGHT_RANGE": 25,
        "LIGHT_ACTUAL_DELTA": -26,  # i.e. too high - above (-50 + 25)

        "PINS_IN_BITS": 0x3,
        "EXPECTED_PINS_OUT_BITS": 0x6,
        "ACTUAL_PINS_SET": 0x4, # i.e. missing output on pin number 1 (starting from 0)

        "PROXIMITY_DELTA": 200,
        "PROXIMITY_RANGE": 40,
        "ACTUAL_PROXIMITY_DELTA": 220, # i.e. in range!

        "NOB_ANGLE": 4,
        "ACTUAL_NOB_ANGLE": 2, # i.e. needs more rotation
    }
    """

def kill_bomb_session(session):
    session['ticking_stopper'].set()
    session['serial_stopper'].set()


def arduino_serial_reader(queue, stopper):
    arduino_serial = serial.Serial(port='/dev/ttyACM0', baudrate=9600, timeout=5)
    # arduino_serial.flush()
    while not stopper.is_set():
        try:
            arduino_serial.write('x')
            line = arduino_serial.readline().strip()
            queue.put(line)
        except serial.SerialException:
            pass
