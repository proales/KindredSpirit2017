void addEffects() {
  noStroke();
  int averageBrightness = 0;
  for (int x = 0; x < canvasWidth; x++) {
    for (int y = 0; y < topSectionHeight; y++) {
      int adjustedColor;  // Adjusted for brigtness
      int recoloredColor; // Adjusted for color rgb
      int currentColor = pixels[y*canvasWidth+x];
      
      adjustedColor = increaseBrightness(currentColor, brightnessAdjustment + beatBrightness + avgerageBrightnessAdjustment);
      
      // If it is on then overlay a color
      if (overlayColor) {
        recoloredColor = mergeColors(adjustedColor, colorOverlay);
      } else {
        recoloredColor = adjustedColor;
      }
      
      // Fill the new box of pixels
      set(x, y, recoloredColor);
      
      pixels[y*canvasWidth+x] = recoloredColor;
      averageBrightness += red(recoloredColor) + 
                           green(recoloredColor) + 
                           blue(recoloredColor);
    }
  }
  // 3 here because there are three color channels added up above.
  averageBrightness = round(averageBrightness / (topSectionHeight * canvasWidth * 3));
  // 90 and 70 are arbitrary here
  if (averageBrightness > 90) {
    avgerageBrightnessAdjustment--;
  } else if (averageBrightness < 70) {
    avgerageBrightnessAdjustment++;
  }
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

int getPixel (int x, int y) {
  return pixels[y*canvasWidth+x];
}

void handDrawnEffects() {
  videoEffect();
  headEffect();
  djEffect();
  rainEffect();
  walkColorEffect();
  noStroke();
  fill(color(0, 0, 0));
  rect(0, topSectionHeight, canvasWidth - colorDisplayBandWidth, 150);
}

void videoEffect() {
  if (effect1) {
    beatDetectionOn = false;
    avgerageBrightnessAdjustment = 0;
    beatBrightness = 0;
    noStroke();
    fill(color(0, 0, 0));
    rect(0, colorPickerHeight + buttonRowHeight, canvasWidth - colorDisplayBandWidth, screenshotHeight);
  }
}

ArrayList<Integer> headList = new ArrayList<Integer>();
int headStep = 0;
void headEffect() {
  if (effect2) {
    int centerX = 375;
    int centerY = 120;
    noFill();
    stroke(colorOverlay);
    strokeWeight(10);
    for (int i = 0; i < headList.size(); i++) {
      int size = headList.get(i) + 10;
      if (size < 1000) {
        headList.set(i, size);
      } else {
        headList.remove(i);
      }
      ellipse(centerX, centerY, size, size);
    }
    if (headStep == 0) {
      headList.add(0);
    } 
    headStep = (headStep + 1) % 10;

  }
}

ArrayList<Integer> djList = new ArrayList<Integer>();
void djEffect() {
  if (effect3) {
    int centerX = 675;
    int centerY = 70;
    noFill();
    stroke(colorOverlay);
    strokeWeight(10);
    for (int i = 0; i < headList.size(); i++) {
      int size = headList.get(i) + 10;
      if (size < 1500) {
        headList.set(i, size);
      } else {
        headList.remove(i);
      }
      ellipse(centerX, centerY, size, size);
    }
    if (beatLevel > 47) {
      headList.add(0);
    } 
  }
}

ArrayList<Point> rainList = new ArrayList<Integer>();
void rainEffect() {
  int random = round(random(canvasWidth - 30));
  for (int i = 0; i < rainList.size(); i++) {
    Point rainlet = rainList.get(i);
    if (rainlet.y < 1500) {
      rainList.set(i, size);
    } else {
      headList.remove(i);
    }
    ellipse(rainlet.x, rainlet.y, 1, 10);
  }
  
  
}

void walkColorEffect() {
  
}