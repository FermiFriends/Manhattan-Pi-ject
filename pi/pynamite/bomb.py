import time


def start_bomb_session(options):

    #example_settings = {
    #    "TEMP_MAX": 40,
    #    "TEMP_MIN": 38,
    #    "LIGHT_MAX": idek,
    #    "LIGHT_MIN": idek

    #    # ROCCO clarification
    #    "EXPECTED_PIN_OUT": 3,
    #    "PIN_IN": 3,

    #    "NOB_ANGLE" : idel,
    #    "PROXIMITY_MAX": idek,
    #    "PROXIMITY_MIN": idek,
    #}

    session = {}
    session['start_time'] = time.time()
    session['time_limit'] = options['time_limit']

    # spawn thread for ticking thread
    session['ticking_thread'] = None

    # reset arduino with given params

def session_status(session):
    # SESSION STATUSES:
    #   ticking
    #   disarmed
    #   detonated

    # ask arduino for session update
    # perform relevant actions if we've been defused / exploded
    # killing the thread for timing and beeping first

    # parse sensor data back from arduino
    # return as dict
    pass

def kill_bomb_session(session):
    # kill timer thread
    pass
