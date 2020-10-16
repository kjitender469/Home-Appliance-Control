/*
   WebSocketServer.ino

    Created on: 22.05.2015

*/

#include <Arduino.h>

#include <WiFi.h>
//#include <WiFiServer.h>
//#include<WiFi8266.h>
#include <WiFiClientSecure.h>
//#include <WiFiMulti.h>
#include <EEPROM.h>

#include <WebSocketsServer.h>

WebSocketsServer webSocket = WebSocketsServer(8080);

//WiFiMulti wiFiMulti;
//WiFiServer ws = WiFiServer(80);

int L1_status = 0, L2_status = 0, L3_status = 0;
long t1 = 0;
int client_id = 0;

int light_1 = 4;
int light_2 = 5;
int light_3 = 2;

// Constants
const char* ssid = "Jitu";
const char* password = "123456789";

// the current address in the EEPROM (i.e. which byte
// we're going to write to next)
int addr = 0;
#define EEPROM_SIZE 64

#define USE_SERIAL Serial

void hexdump(const void *mem, uint32_t len, uint8_t cols = 16) {
  const uint8_t* src = (const uint8_t*) mem;
  USE_SERIAL.printf("\n[HEXDUMP] Address: 0x%08X len: 0x%X (%d)", (ptrdiff_t)src, len, len);
  for (uint32_t i = 0; i < len; i++) {
    if (i % cols == 0) {
      USE_SERIAL.printf("\n[0x%08X] 0x%08X: ", (ptrdiff_t)src, i);
    }
    USE_SERIAL.printf("%02X ", *src);
    src++;
  }
  USE_SERIAL.printf("\n");
}

void webSocketEvent(uint8_t num, WStype_t type, uint8_t * payload, size_t length) {

  switch (type) {
    case WStype_DISCONNECTED:
      USE_SERIAL.printf("[%u] Disconnected!\n", num);
      break;
    case WStype_CONNECTED:
      {
        IPAddress ip = webSocket.remoteIP(num);
        USE_SERIAL.printf("[%u] Connected from %d.%d.%d.%d url: %s\n", num, ip[0], ip[1], ip[2], ip[3], payload);

        // send message to client
        //webSocket.sendTXT(num, "Connected");
        webSocket.sendTXT(num, "Connected");
      }
      break;
    case WStype_TEXT:
      {
        USE_SERIAL.printf("[%u] get Text: %s\n", num, payload);

        // send message to client
        // webSocket.sendTXT(num, "message here");

        // send data to all connected clients
        // webSocket.broadcastTXT("message here");

        String msg = (char*)payload;
        Serial.print("Messege received is : ");
        Serial.println(msg);
        if (msg == "J")
        {
          //Serial.println("I am Here 11");
          if (L1_status == 1)
          {
            //Serial.println("I am Here 1");
            webSocket.sendTXT(num, "L1_T");
          }
          else
          {
            webSocket.sendTXT(num, "L1_F");
            //Serial.println("I am Here 2");
          }
          if (L2_status == 1)
          {
            //Serial.println("I am Here 3");
            webSocket.sendTXT(num, "L2_T");
          }
          else
          {
            //Serial.println("I am Here 4");
            webSocket.sendTXT(num, "L2_F");
          }
          if (L3_status == 1)
          {
            //Serial.println("I am Here 5");
            webSocket.sendTXT(num, "L3_T");
          }
          else
          {
            //Serial.println("I am Here 6");
            webSocket.sendTXT(num, "L3_F");
          }
        }
        else
        {
          //Serial.println("I am Here 9");
          if (msg == "L1_On")
          {
            //Serial.println("I am Here 7");
            digitalWrite(light_1, HIGH);
            L1_status = 1;
            addr = 0;
            EEPROM.write(addr, 1);
            EEPROM.commit();
          }
          else if (msg == "L1_Off")
          {
            digitalWrite(light_1, LOW);
            L1_status = 0;
            addr = 0;
            EEPROM.write(addr, 0);
            EEPROM.commit();
          }
          else if (msg == "L2_On")
          {
            //Serial.println("I am Here 8");
            digitalWrite(light_2, HIGH);
            L2_status = 1;
            addr = 1;
            EEPROM.write(addr, 1);
            EEPROM.commit();
          }
          else if (msg == "L2_Off")
          {
            digitalWrite(light_2, LOW);
            L2_status = 0;
            addr = 1;
            EEPROM.write(addr, 0);
            EEPROM.commit();
          }
          else if (msg == "L3_On")
          {
            //Serial.println("I am Here 9");
            digitalWrite(light_3, HIGH);
            L3_status = 1;
            addr = 2;
            EEPROM.write(addr, 1);
            EEPROM.commit();
          }
          else if (msg == "L3_Off")
          {
            digitalWrite(light_3, LOW);
            L3_status = 0;
            addr = 2;
            EEPROM.write(addr, 0);
            EEPROM.commit();
          }
          else
          {
          }
        }
      }
      break;
    case WStype_BIN:
      {
        USE_SERIAL.printf("[%u] get binary length: %u\n", num, length);
        hexdump(payload, length);

        // send message to client
        // webSocket.sendBIN(num, payload, length);
      }
      break;
    case WStype_ERROR:

    case WStype_FRAGMENT_TEXT_START:

    case WStype_FRAGMENT_BIN_START:

    case WStype_FRAGMENT:

    case WStype_FRAGMENT_FIN:
      break;
  }

}

void setup() {
  // USE_SERIAL.begin(921600);
  USE_SERIAL.begin(115200);

  //Serial.setDebugOutput(true);
  USE_SERIAL.setDebugOutput(true);

  USE_SERIAL.println();
  USE_SERIAL.println();
  USE_SERIAL.println();

  for (uint8_t t = 4; t > 0; t--) {
    USE_SERIAL.printf("[SETUP] BOOT WAIT %d...\n", t);
    USE_SERIAL.flush();
    delay(1000);
  }

  //WiFiMulti.addAP("Jitu", "123456789");
  //ws.softAP("Jitu", "123456789");
  //wiFiMulti.addAP("Jitu", "123456789");
  //WiFi.mode(WIFI_STA);
  WiFi.softAP("Jitu", "123456789");
  //WiFi.begin(ssid, password);

//  wiFiMulti.addAP("SSID", "passpasspass");
//
//    while(wiFiMulti.run() != WL_CONNECTED) {
//        delay(100);
//    }

  IPAddress IP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(IP);

  webSocket.begin();
  //webSocket.connect();
  webSocket.onEvent(webSocketEvent);

  Serial.print("Everything is done!!");

  Serial.println("EEPROM start...");
  if (!EEPROM.begin(EEPROM_SIZE))
  {
    Serial.println("failed to initialise EEPROM"); delay(1000000);
  }
  Serial.println(" bytes read from Flash . Values are:");
  for (int i = 0; i < EEPROM_SIZE; i++)
  {
    //    Serial.print("EEPROM Location : ");
    //    Serial.print(i);
    //    Serial.println(" : ");
    //    Serial.print(byte(EEPROM.read(i)));
    //    Serial.println(" ");
  }
  Serial.println();
  Serial.println("Reading of all 64 bytes are done...!!");

  L1_status = EEPROM.read(0);
  L2_status = EEPROM.read(1);
  L3_status = EEPROM.read(2);

  digitalWrite(light_1, L1_status);
  digitalWrite(light_2, L2_status);
  digitalWrite(light_3, L3_status);

  Serial.println(L1_status);
  Serial.println(L2_status);
  Serial.println(L3_status);

  Serial.println("Reading of initial 3 locations of EEPROM are done...!!");

  pinMode(light_1, OUTPUT);
  pinMode(light_2, OUTPUT);

}

void loop() {
  webSocket.loop();
  //Serial.println("loop");
  //if(millis()-t1>700)
  //{
  //  webSocket.sendTXT(client_id, "ping");
  //  t1 = millis();
  //  }
}
