import RPi.GPIO as GPIO
import time

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
current_num = 0

digit_segments = {
        0 : [1, 1, 0, 1, 1, 1, 1],
        1 : [0, 0, 0, 1, 0, 0, 1],
        2 : [1, 0, 1, 1, 1, 1, 0],
        3 : [1, 0, 1, 1, 0, 1, 1],
        4 : [0, 1, 1, 1, 0, 0, 1],
        5 : [1, 1, 1, 0, 0, 1, 1],
        6 : [1, 1, 1, 0, 1, 1, 1],
        7 : [1, 0, 0, 1, 0, 0, 1],
        8 : [1, 1, 1, 1, 1, 1, 1],
        9 : [1, 1, 1, 1, 0, 1, 1],
        }

if __name__ == '__main__':
    setup()
    change_number(1234)
    while True:
        display()

def setup():
    GPIO.setmode(GPIO.BCM)
    for i in range(11):
        GPIO.setup(pin_offset + i, GPIO.OUT)

def change_number(num):
    current_num = num

def display():
    for digit in range(4):
        num = current_num / (10**digit)
        display_digit(number=num, digit=digit)

def display_digit(number, digit):
    segments = digit_segments[number]
    GPIO.output(pin_offset + digit + 6, 0)
    for i in segments:
        GPIO.output(pin_offset + i, 1)

    time.sleep(delay_time)
    GPIO.output(pin_offset + digit + 6, 1)
