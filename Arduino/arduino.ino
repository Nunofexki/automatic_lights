#define LAMP_PIN 7

// Analog input pin that the LDR sensor is attached to
const int LDRPin = A0;  

// Luminosity of the room
int ldr = 0;

// Status of the light 
int lamp_status = 0;

// # of people in the room
int room_count = 0;

// Photoresistors output pin
int pd1 = 4;
int pd2 = 2;

// IR analog pins
int senRead1 = 1;
int senRead2 = 2;

// Initialize counting variables
int entry = 0;
int leave = 0;

// Threshold range of an obstacle
int limit = 970;


void printStatus()
{
  // print the results to the Serial Monitor:
  Serial.print("\"Luminosity\": ");
  Serial.println(ldr);

  //Serial.print("Status = ");
  //Serial.println(lamp_status);
  
  //Serial.print("\"nPessoas\": ");
  //Serial.println(room_count);
}

void setup() 
{
  // initialize serial communications at 9600 bps:
  Serial.begin(9600);
  
  pinMode(LDRPin, INPUT);
  pinMode(LAMP_PIN,OUTPUT);
  pinMode(pd1,OUTPUT); 
  pinMode(pd2,OUTPUT);    

  //turn off the lights
  digitalWrite(LAMP_PIN,LOW);  

  //turn on the photoresistors
  digitalWrite(pd1,HIGH);
  digitalWrite(pd2,HIGH);
}

void loop() 
{
  // if Raspberry Pi sent some data
  if (Serial.available() > 0) 
  {
    lamp_status = Serial.read() - '0';
    Serial.print("Arduino --> ");
    Serial.println(lamp_status);

    Serial.print("\"Luminosity\": ");
    Serial.println(ldr);
  }

  int val1=analogRead(senRead1);
  //Serial.print("Sensor1 = ");
  //Serial.println(val1);         

  int val2=analogRead(senRead2);  
  //Serial.print("Sensor2 = ");
  //Serial.println(val2);         
  
  if(val1 > limit)              
  {
    if (entry == 0)
    {      
      if (leave == 0)
      {
          //Serial.print("Entry detected!!");  
          entry=1;        
      }
      else
      {
          //Serial.print("Leaving detected!!");  
          entry=2;                
      }
    }  
  }  

  if(val2 > limit)
  {
    if (leave == 0)
    {
      if (entry == 0)
      {
          //Serial.print("Leaving detected!!");  
          leave=1;           
      }
      else
      {
          //Serial.print("Entry detected!!");  
          leave=2;                        
      }
    }
  }

  if (entry != 0)
  {
    if (leave != 0)
    {
      if (entry < leave)
      {
        room_count ++;
      }
      
      if (entry > leave)
      {
        if (room_count > 0)
        {
            room_count --;
        }
      }
      entry=0;
      leave=0;

      //Serial.print("\"nPessoas\": ");
      //Serial.println(room_count); 

      delay(500);
    }
  }
  
  // get luminostity of the room
  ldr = analogRead(LDRPin);

  if (room_count == 0)
  {
    if (lamp_status == 0)
    {
      digitalWrite(LAMP_PIN,LOW);
    }

    if (lamp_status == 1)
    {
      if (ldr > 700)
      {
        digitalWrite(LAMP_PIN,HIGH);
      }
      else
      {
        digitalWrite(LAMP_PIN,LOW);
      }
    }
  }

  if (room_count > 0)
  {
    if (lamp_status == 1)
    {
      if (ldr > 700)
      {
        digitalWrite(LAMP_PIN,HIGH);
      }
    }
  }

  //printStatus();

  delay(200);
}
