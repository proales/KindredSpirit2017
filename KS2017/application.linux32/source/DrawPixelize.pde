void addEffects() {
 
  for (int x = 0; x < 825; x++) {
    for (int y = 0; y < 200; y++) {
      int avgColor;       // Average color of the old box of pixels
      int adjustedColor;  // Adjusted for brigtness
      int recoloredColor; // Adjusted for color rgb
      int currentColor = pixels[y*canvasWidth+x];
      
      adjustedColor = increaseBrightness(currentColor, brightnessAdjustment);
      
      // If it is on then overlay a color
      if (overlayColor) {
        recoloredColor = mergeColors(adjustedColor, colorOverlay);
      } else {
        recoloredColor = adjustedColor;
      }
      
      // Fill the new box of pixels
      fill(recoloredColor);
      // Replace the 0 here to move the pixelized output down
      rect(x, y, 1, 1);
      
      //// Save average color of the box 
      //avgColors[x][y] = recoloredColor;
      pixels[y*canvasWidth+x] = recoloredColor;
    }
  }
  
}

color linearToColor (int value) {
return HSBtoRGB(((float) value)/100.0*.85, (float) 1, (float) 1);
}

color HSBtoRGB(float h, float s, float v) {
  double r = 0, g = 0, b = 0, i, f, p, q, t;
  i = Math.floor(h * 6);
  f = h * 6 - i;
  p = v * (1 - s);
  q = v * (1 - f * s);
  t = v * (1 - (1 - f) * s);
  switch ((int) (i % 6)) {
       case 0: r = v; g = t; b = p; break;
       case 1: r = q; g = v; b = p; break;
       case 2: r = p; g = v; b = t; break;
       case 3: r = p; g = q; b = v; break;
       case 4: r = t; g = p; b = v; break;
       case 5: r = v; g = p; b = q; break;
   }
   return color((float) r * 255, (float) g * 255, (float) b * 255);
}

color increaseBrightness (color c, int amount) {
  float newRed = constrain(red(c) + amount, 0, 255);
  float newGreen = constrain(green(c) + amount, 0, 255);
  float newBlue = constrain(blue(c) + amount, 0, 255);
  return color(newRed, newGreen, newBlue); 
}

color mergeColors(color a, color b) {
  return blendColor(a, b, blendColorMode);
}

int avgColor(int col, int row) {
  int red = 0;
  int green = 0;
  int blue = 0;
  int currentColor;
  int totalPixels = 7 * 7;
  
  for (int x = 0; x < pixelSize; x++) {
    for (int y = 0; y < pixelSize; y++) {
      currentColor = getPixel(col * pixelSize + x, row * pixelSize + y);
      red += red(currentColor);
      green += green(currentColor);
      blue += blue(currentColor);
    }
  }
  
  return color(red/totalPixels, green/totalPixels, blue/totalPixels);
}

int getPixel (int x, int y) {
  return pixels[y*canvasWidth+x];
}