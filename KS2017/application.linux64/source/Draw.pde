void draw() {
  // Setup canvas defaults
  background(0);
  fill(255);
 
  // Get screenshot
  try {
    Robot robot_Screenshot = new Robot();                        
    // 50 for MacOS Header, 0 for offset from top adjustment
    screenshot = new PImage(robot_Screenshot.createScreenCapture(new Rectangle(0, screenshotYAdjustment + 50, 600, 200 - 60)));
  } catch (AWTException e) {
    
  }
  
  // Copy screenshot to canvas
  image(screenshot, 0, 60, canvasWidth, pixelRows - 60);
  
  // Load the canvas pixel buffer
  loadPixels();
  
  // Iterate through to average pixels and apply adjustments
  addEffects();
 
  // Extract the color value from the display and add it to the Pixel objects
  colorPixels();
  
  // Send the pixels to the PixelPushers
  drawPixelPusher();
  
  // Display what was output to PixelPushers
  drawDisplay();

  // Draw box on canvas for current selected color
  fill(colorSelected);
  rect(canvasWidth - colorBandWidth, 0, colorBandWidth, canvasHeight / 2);
  
  // Draw box on canvas for current displayed color
  fill(colorOverlay);
  rect(canvasWidth - colorBandWidth, canvasHeight / 2, colorBandWidth, canvasHeight / 2);
  
  // Draw the color picker
  colorPicker.render();
  
  // Draw the buttons
  drawButtons();
} 