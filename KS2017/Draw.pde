void draw() {
  // Setup canvas defaults
  background(0);
  fill(255);
 
  // Get screenshot
  try {
    Robot robot_Screenshot = new Robot();                        
    screenshot = new PImage(robot_Screenshot.createScreenCapture(
        new Rectangle(0, screenshotYAdjustment, 640, screenshotHeight)));
  } catch (AWTException e) {}
  
  // Copy screenshot to canvas
  image(screenshot, 0, colorPickerHeight + buttonRowHeight, canvasWidth - colorDisplayBandWidth, screenshotHeight);
  
  // 
  handDrawnEffects();
  
  // Load the canvas pixel buffer
  loadPixels();
  
  // Detect the beat
  beatDetect();

  // Iterate through to average pixels and apply adjustments
  addEffects();
 
  // Extract the color value from the display and add it to the Pixel objects
  colorPixels();
  
  // Send the pixels to the PixelPushers
  drawPixelPusher();
  
  // Display what was output to PixelPushers
  drawDisplay();
  
  // UDP Update
  udpDraw();

  // Draw box on canvas for current selected color
  fill(colorSelected);
  rect(canvasWidth - colorDisplayBandWidth, 0, colorDisplayBandWidth, topSectionHeight / 2);
  
  // Draw box on canvas for current displayed color
  fill(colorOverlay);
  rect(canvasWidth - colorDisplayBandWidth, topSectionHeight / 2, colorDisplayBandWidth, topSectionHeight / 2);
  
  // Draw the color picker
  colorPicker.render();
  
  // Draw the buttons
  drawButtons();
  
  // Display the frame rate, 70 and 15 are arbitrary values for the UI
  fill(color(255, 255, 255));
  text(round(frameRate), canvasWidth - 70, canvasHeight - 15);
} 