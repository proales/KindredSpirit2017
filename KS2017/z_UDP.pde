import hypermedia.net.*;
UDP udp;

void udpSetup() {
  udp = new UDP(this, 9901);
}

int updCounter = 0;
void udpDraw() {
  if (updCounter == 0) {
    udp.send("STFU", "10.1.1.200", 9901);
  }
  updCounter = (updCounter + 1) % 20;
}