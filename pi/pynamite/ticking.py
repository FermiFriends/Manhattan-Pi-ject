import RPi.GPIO as GPIO
import time
import threading

"""
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

        number = _time
        for position in reversed(range(4)):
            digit = number % 10
            number = number / 10
            display_digit(digit, position)

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

    GPIO.cleanup()

if __name__ == '__main__':
    e = threading.Event()
    t = threading.Thread(target=countdown, args=(9, e))
    t.start()
    time.sleep(5)
    e.set()

#    try:
#        countdown(9999)
#    except KeyboardInterrupt:
#        GPIO.cleanup()
