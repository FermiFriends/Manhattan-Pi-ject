import RPi.GPIO as GPIO
import time
import threading

"""
Manages the displaying the time on the 7-segment
display and plaing beeping audio.

7-seven segment display settings:
    0
    _
 1 | | 3 
    -    <---- 2
 4 | | 6
    -    <---- 5

PIN 2-8 = segments
PIN 9-12 = GND
"""
pin_offset = 2
delay_time = .004 # s

setup = False

digit_segments = [
        [1, 1, 0, 1, 1, 1, 1],
        [0, 0, 0, 1, 0, 0, 1],
        [1, 0, 1, 1, 1, 1, 0],
        [1, 0, 1, 1, 0, 1, 1],
        [0, 1, 1, 1, 0, 0, 1],
        [1, 1, 1, 0, 0, 1, 1],
        [1, 1, 1, 0, 1, 1, 1],
        [1, 0, 0, 1, 0, 0, 1],
        [1, 1, 1, 1, 1, 1, 1],
        [1, 1, 1, 1, 0, 1, 1],
]

def setup():
    GPIO.setmode(GPIO.BCM)
    for i in range(2, 13):
        GPIO.setup(i, GPIO.OUT)
    GPIO.setup(16, GPIO.OUT)



def beep(duration):
    setup()
    GPIO.output(16, 1)
    time.sleep(duration)
    GPIO.output(16, 0)

def display(_time, stahp):
    # Clamp
    if _time > 9999:
        _time = 9999
    elif _time < 0:
        _time = 0

    start_time = time.time()
    ticks = 1
    while _time > 0 and not stahp.is_set():
        current_time = time.time()

        if current_time - start_time > ticks:
            _time -= 1
            ticks += 1
            beep(0.2)


        number = _time
        for position in reversed(range(4)):
            digit = number % 10
            number = number / 10
            display_digit(digit, position)

    GPIO.cleanup()

def display_digit(digit, position):
    segments = digit_segments[digit]

    GPIO.output(pin_offset + position + 1 + 6, 0)

    for index, on in enumerate(segments):
        GPIO.output(index + pin_offset, on)

    time.sleep(delay_time)
    GPIO.output(pin_offset + position + 1 + 6, 1)


def countdown(time_limit, stahp):
    setup()

    display(time_limit, stahp)


def start_ticking(time_limit):
    ticking_stopper = threading.Event()
    t = threading.Thread(target=countdown, args=(time_limit, ticking_stopper))
    t.start()
    return ticking_stopper


if __name__ == '__main__':
    e = threading.Event()
    t = threading.Thread(target=countdown, args=(9, e))
    t.start()
    time.sleep(5)
    e.set()
