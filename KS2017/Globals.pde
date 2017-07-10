// Canvas setup
PImage screenshot; 
int canvasWidth = 825; 
int canvasHeight = 350;
int topSectionHeight = canvasHeight - 150;
int colorPickerHeight = 30;
int buttonRowHeight = 30;
int screenshotHeight = topSectionHeight - colorPickerHeight - buttonRowHeight;
int colorDisplayBandWidth = 25;
int screenshotYAdjustment = 150;

// Pixel Pusher setup.
DeviceRegistry registry;
PixelPusherObserver pixelPusherObserver;
List<Strip> strips;

// Effects setup
int brightnessAdjustment = 0;
// -50 is just a good starting point 
// this number will move around automatically
int avgerageBrightnessAdjustment = -50;
int colorOverlay = color(255, 0, 0);
int colorSelected = color(0, 255, 0);
int blendColorMode = OVERLAY;
boolean overlayColor = false;
ColorPicker colorPicker;

// Hand draw effects 
boolean effect1 = false;
boolean effect2 = false;
boolean effect3 = false;
boolean effect4 = false;
boolean effect5 = false;

// Beat detector setup
BeatDetect beatDetect;
BeatListener beatListener;
Minim minim;
AudioInput audioInput;
int beatLevel = 0;
int beatBrightness = 0;
boolean beatDetectionOn = true;

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
      // To avoid the header row and any rows with issues
      if (lineArray.length > 1) {
        // PixelPusher
        pd.controller = Integer.parseInt(lineArray[0]);
        // Strip
        pd.strip = Integer.parseInt(lineArray[1]);
        // LED
        pd.number = Integer.parseInt(lineArray[2]);
        // X Value
        pd.x = Integer.parseInt(lineArray[3]);
        // Y Value
        pd.y = Integer.parseInt(lineArray[4]);
        pixelMap.add(pd);
      }
    }
  }
}
  