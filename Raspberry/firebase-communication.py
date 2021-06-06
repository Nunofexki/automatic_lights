#!/usr/bin/env python3

import serial
import time
import pyrebase

config = {
  "apiKey": "WSZY4ZPu6odDZUdi9ZeK6YPZ5oZuHicrHcibOeYK",
  "authDomain": "automatic-lights1.firebaseapp.com",
  "databaseURL": "https://automatic-lights1-default-rtdb.europe-west1.firebasedatabase.app",
  "storageBucket": "automatic-lights1.appspot.com"
}

firebase = pyrebase.initialize_app(config)
db = firebase.database()

if __name__ == '__main__':

    ser = serial.Serial('/dev/ttyACM0', 9600, timeout=1)
    ser.flush()

    lastStatus = 0;

    while True:

      status = db.child("Status").get().val()
      
      #if status != lastStatus:
      #      lastStatus = status
      #      print("test")
      
      ser.write(str(status).encode('utf-8'))
      
      print("PYTHON - Status = " + str(status))

      if ser.in_waiting > 0:
            line = ser.readline().decode('utf-8').rstrip()
            print("Read Line: " + line)
            
            if line.find("Luminosity"):
                value = line.partition(':')[2]

                if int(value) > 0:
                    db.update({"Luminosity: " + value })
                    
            if line.find("nPessoas"):
                value = line.partition(':')[2]
                
                if int(value) > 0:
                    db.update({"nPessoas: " + value })

      time.sleep(1)     
