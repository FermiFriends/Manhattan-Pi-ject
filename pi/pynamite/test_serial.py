import serial

if __name__ == '__main__':
    ser = serial.Serial('/dev/ttyACM0', 9600)
    while True:
        print ser.readline() 
