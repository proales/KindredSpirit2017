void udpSetup() {
  udp = new UDP(this, 9901);
}

int updCounter = 0;
void udpDraw() {
  if (updCounter == 0) {
    // Command line broadcast redirect:
    // sudo socat UDP4-RECVFROM:9000,fork UDP4-SENDTO:192.168.86.255:9001,broadcast
    udp.send(str(frameCount), "localhost", 9000);
    udp.send("STFU", "10.1.1.200", 9901);
  }
  updCounter = (updCounter + 1) % 20;
}