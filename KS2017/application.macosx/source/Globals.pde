// Canvas setup
PImage screenshot; 
int canvasWidth = 825; 
int canvasHeight = 350;
int topSectionHeight = canvasHeight - 150;
int colorPickerHeight = 30;
int buttonRowHeight = 30;
int screenshotHeight = topSectionHeight - colorPickerHeight - buttonRowHeight;
int colorDisplayBandWidth = 25;
ArrayList<PixelDefinition> pixelMap = new ArrayList<PixelDefinition>();

// Pixel Pusher setup.
DeviceRegistry registry;
PixelPusherObserver pixelPusherObserver;
List<Strip> strips;

// Effects setup
int screenshotYAdjustment = 30;
int brightnessAdjustment = 0;
// -50 is just a good starting point 
// this number will move around automatically
int avgerageBrightnessAdjustment = -50;
int colorOverlay = color(255, 0, 0);
int colorSelected = color(0, 255, 0);
int blendColorMode = OVERLAY;
boolean overlayColor = false;
ColorPicker colorPicker;

// Hand draw effects setup
boolean effect0 = false;
boolean effect1 = false;
boolean effect2 = false;
boolean effect3 = false;
boolean effect4 = false;
boolean effect5 = false;
ArrayList<EffectPoint> headList = new ArrayList<EffectPoint>();
ArrayList<EffectPoint> djList = new ArrayList<EffectPoint>();
ArrayList<RainDrop> rainList = new ArrayList<RainDrop>();
int colorWalkValue = 0;

// Beat detector setup
BeatDetect beatDetect;
BeatListener beatListener;
Minim minim;
AudioInput audioInput;
int beatLevel = 0;
int beatBrightness = 0;
boolean beatDetectionOn = true;
int gainSetting = 35;

// UDP setup
UDP udp;

void loadDefaults() {  
  // Setup colorPicker
  colorPicker = new ColorPicker(0, colorPickerHeight, canvasWidth + 5, colorPickerHeight, 255);
  
  // Effects defaults
  screenshotYAdjustment = 30;
  brightnessAdjustment = 0;
  avgerageBrightnessAdjustment = -50;
  colorOverlay = color(255, 0, 0);
  colorSelected = color(0, 255, 0);
  blendColorMode = OVERLAY;
  overlayColor = false;

  // Hand draw effects defaults
  effect0 = false;
  effect1 = false;
  effect2 = false;
  effect3 = false;
  effect4 = false;
  effect5 = false;
  headList = new ArrayList<EffectPoint>();
  djList = new ArrayList<EffectPoint>();
  rainList = new ArrayList<RainDrop>();
  colorWalkValue = 0;
  
  // Beat detector defaults
  beatLevel = 0;
  beatBrightness = 0;
  beatDetectionOn = true;
  gainSetting = 35;
}

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
  