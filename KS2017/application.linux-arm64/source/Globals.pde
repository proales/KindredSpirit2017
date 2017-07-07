// Canvas setup
PImage screenshot; 
int pixelSize = 10;
int sheetWidth = 20;
int sheetHeight = 20;
int numberColumns = 60;
int numberRows = 20;
int pixelColumns = pixelSize * numberColumns;
int pixelRows = pixelSize * numberRows;
int canvasWidth = 825; 
int canvasHeight = 200;
int colorBandWidth = 25;
int screenshotYAdjustment = 150;

// Pixel Pusher setup.
DeviceRegistry registry;
PixelPusherObserver pixelPusherObserver;

// Effects setup
int brightnessAdjustment = 0;
int colorNumber = 0;
int colorOverlay = color(255, 0, 0);
int colorSelected = color(0, 255, 0);
int blendColorMode = OVERLAY;
boolean overlayColor = false;

List<Strip> strips;
ColorPicker colorPicker;

ArrayList<PixelDefinition> pixelMap = new ArrayList<PixelDefinition>();
class PixelDefinition { 
  int controller; 
  int strip;
  int number;
  int x;
  int y;
  int colorValue;
} 

void loadPixelMap() {
  BufferedReader reader;
  String line;
  reader = createReader("led.csv");       
  
  while(true) {
    try {
      line = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      line = null;
    }
    if (line == null) {
      return;
    } else {
      PixelDefinition pd = new PixelDefinition();
      String[] lineArray = line.split(",");
      if (lineArray.length > 1) {
        pd.controller = Integer.parseInt(lineArray[0]);
        pd.strip = Integer.parseInt(lineArray[1]);
        pd.number = Integer.parseInt(lineArray[2]);
        pd.x = Integer.parseInt(lineArray[3]);
        pd.y = Integer.parseInt(lineArray[4]);
        pixelMap.add(pd);
      }
    }
  }
}
  