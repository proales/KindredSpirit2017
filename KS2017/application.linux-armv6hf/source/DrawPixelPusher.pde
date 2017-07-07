// Pixel Pusher setup class.
class PixelPusherObserver implements Observer {
  public boolean hasStrips = false;
  public void update(Observable registry, Object updatedDevice) {
    this.hasStrips = true;
  }
}

void drawPixelPusher(){
  if (pixelPusherObserver.hasStrips) {
    List<PixelPusher> pusherList = registry.getPushers();
    PixelPusher[] pixelPusherArray = new PixelPusher[8];
    for(PixelPusher pusher : pusherList) {
      int controllerNumber = pusher.getControllerOrdinal();
      pixelPusherArray[controllerNumber] = pusher;
    }
 
    for(PixelDefinition pixel : pixelMap) {      
      // All the PixelPushers may not always be present so check first
      if (pixelPusherArray[pixel.controller] != null) {
        PixelPusher pusher = pixelPusherArray[pixel.controller];
        Strip strip = pusher.getStrip(pixel.strip);
        strip.setPixel(pixel.colorValue, pixel.number);
      }
    } 
  }
}

void colorPixels() {
  for(PixelDefinition pixel : pixelMap) {  
    int averageColor = pixels[(pixel.y+60)*canvasWidth+pixel.x];
    pixel.colorValue = averageColor;
  }
}

void drawDisplay() {
  for(PixelDefinition pixel : pixelMap) { 
      fill(pixel.colorValue);
      rect(pixel.x, pixel.y + 210, 3, 3);
  }
}

//int getAverageColor(int sheet, int pixelX) {
//  int x = pixelX % sheetWidth;
//  int y = pixelX / sheetHeight;
//  return avgColors[sheet + x][y];
//}