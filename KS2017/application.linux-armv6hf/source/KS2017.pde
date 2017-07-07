void setup() {
  // Define window size
  // Size will only take numbers so keep
  // in sync with global height & width
  size(825, 350);
  
  // No lines
  noStroke();
  
  // Load the map of the pixel locations from the CSV
  loadPixelMap();
  
  // Initialize the color picker
  colorPicker = new ColorPicker(0, 30, 830, 30, 255);
  
  // Initialize the buttons
  loadButtons();
  
  // Setup pixel pusher
  registry = new DeviceRegistry();
  pixelPusherObserver = new PixelPusherObserver();
  registry.addObserver(pixelPusherObserver);
  registry.setLogging(false);
  registry.setAntiLog(true);
  
  // Start sending pixels to PixelPushers
  registry.startPushing();
  registry.setAutoThrottle(true);  
}