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
  if (averageBrightness > 90 + brightnessAdjustment) {
    avgerageBrightnessAdjustment -= 10;
  } else if (averageBrightness < 70 + brightnessAdjustment) {
    avgerageBrightnessAdjustment += 10;
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
  colorLockEffect();
  // Draw a black rectangle over the bottom portion so the hand drawn effects dont cover the output
  noStroke();
  fill(color(0, 0, 0));
  rect(0, topSectionHeight, canvasWidth - colorDisplayBandWidth, 150);
}

void videoEffect() {
  if (effect0) {
    beatDetectionOn = false;
    avgerageBrightnessAdjustment = 0;
    noStroke();
    fill(color(0, 0, 0));
    rect(0, colorPickerHeight + buttonRowHeight, canvasWidth - colorDisplayBandWidth, screenshotHeight);
  }
}

void colorLockEffect() {
  if (effect1) {
    colorOverlay = colorSelected;
  }
}

class EffectPoint {
  EffectPoint(int size, int colorValue) {
    this.size = size;
    this.colorValue = colorValue;
  }
  int size;
  int colorValue;
}
ArrayList<EffectPoint> headList = new ArrayList<EffectPoint>();
int headStep = 0;
void headEffect() {
  if (effect2) {
    int centerX = 375;
    int centerY = 120;
    noFill();
    strokeWeight(10);
    for (int i = 0; i < headList.size(); i++) {
      EffectPoint effectPoint = headList.get(i);
      int size = effectPoint.size + 10;
      stroke(effectPoint.colorValue);
      ellipse(centerX, centerY, size, size);
      if (size < 1000) {
        headList.set(i, new EffectPoint(size, effectPoint.colorValue));
      } else {
        headList.remove(i);
      }
    }
    if (headStep == 0) {
      headList.add(new EffectPoint(0, colorOverlay));
    } 
    headStep = (headStep + 1) % 10;

  }
}

ArrayList<EffectPoint> djList = new ArrayList<EffectPoint>();
void djEffect() {
  if (effect3) {
    int centerX = 675;
    int centerY = 70;
    noFill();
    strokeWeight(10);
    for (int i = 0; i < djList.size(); i++) {
      EffectPoint effectPoint = djList.get(i);
      int size = effectPoint.size + 10;
      stroke(effectPoint.colorValue);
      ellipse(centerX, centerY, size, size);
      if (size < 1500) {
        djList.set(i, new EffectPoint(size, effectPoint.colorValue));
      } else {
        djList.remove(i);
      }
    }
    if (beatLevel > 35) {
      djList.add(new EffectPoint(0, colorOverlay));
    } 
  }
}

class RainDrop {
  RainDrop(int x, int y, int colorValue) {
    this.x = x;
    this.y = y;
    this.colorValue = colorValue;
  }
  int x;
  int y;
  int colorValue;
}
ArrayList<RainDrop> rainList = new ArrayList<RainDrop>();
void rainEffect() {
  if (effect4) {
    strokeWeight(3);
    int random = round(random(canvasWidth - 30));
    for (int i = 0; i < rainList.size(); i++) {
      RainDrop rainlet = rainList.get(i);
      if (rainlet.y < 1500) {
        rainList.set(i, new RainDrop(rainlet.x, rainlet.y + 1, rainlet.colorValue));
      } else {
        rainList.remove(i);
      }
      stroke(rainlet.colorValue);
      line(rainlet.x, rainlet.y, rainlet.x, rainlet.y + 20);
    }
    rainList.add(new RainDrop(random, 0, colorOverlay));
  }
}

int colorWalkValue = 0;
void walkColorEffect() {
  if (effect5) {
    colorMode(HSB);
    colorWalkValue = (colorWalkValue + 1) % 765;
    colorOverlay = color(round(colorWalkValue / 3), 255, 255);
    colorMode(RGB);
  }
}