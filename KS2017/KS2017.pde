void setup() {
  // Define window size
  // Size will only take numbers not variables
  // so keep in sync with global width & height
  size(825, 350);
  
  // No lines
  noStroke();
  
  // Load the map of the pixel locations from the CSV
  loadPixelMap();
  
  // Initialize the color picker, 255 is number of colors
  colorPicker = new ColorPicker(0, colorPickerHeight, canvasWidth + 5, colorPickerHeight, 255);
  
  // Initialize the buttons
  loadButtons();
  
  // Setup the beat detection
  beatSetup();
  
  // UDP Setup
  udpSetup();
  
  // Setup pixel pusher
  registry = new DeviceRegistry();
  pixelPusherObserver = new PixelPusherObserver();
  registry.addObserver(pixelPusherObserver);
  registry.setLogging(false);
  registry.setAntiLog(true);
  
  // Start sending pixels to PixelPushers
  registry.startPushing();
  registry.setAutoThrottle(true);  
  
  frameRate(60);
}