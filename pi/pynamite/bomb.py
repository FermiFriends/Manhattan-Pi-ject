import ticking
import time


def start_bomb_session(options):

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
    session['time_limit'] = options['time_limit']
    session['ticking_stopper'] = ticking.start_ticking(session['time_limit'])

    # reset arduino with given params

def session_status(session):

    # TODO: get & parse status from Arduino

    if state == "TICKING":
        pass

    elif state in ["DISARMED", "DETONATED"]:
        session['ticking_stopper'].set()

        if state == "DISARMED":
            # TODO: success feedback
            pass
        elif state == "DETONATED":
            # TODO: failure feedback
            pass

    # TODO: return status as dict

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
    pass

def kill_bomb_session(session):
    session['ticking_stopper'].set()

    # TODO: do we actually need to anything to the Arduino here?
    # We can probably leave it be.
