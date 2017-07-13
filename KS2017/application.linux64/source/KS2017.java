import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.Robot; 
import java.awt.AWTException; 
import java.awt.Rectangle; 
import com.heroicrobot.controlsynthesis.*; 
import com.heroicrobot.dropbit.common.*; 
import com.heroicrobot.dropbit.devices.*; 
import com.heroicrobot.dropbit.devices.pixelpusher.*; 
import com.heroicrobot.dropbit.discovery.*; 
import com.heroicrobot.dropbit.registry.*; 
import java.util.*; 
import java.awt.Point; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import hypermedia.net.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class KS2017 extends PApplet {

public void setup() {
  // Define window size
  // Size will only take numbers not variables
  // so keep in sync with global width & height
  
  
  // No lines
  noStroke();
  
  // Load default variable values
  loadDefaults();
  
  // Load the map of the pixel locations from the CSV
  loadPixelMap();
  
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
public void draw() {
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
public void addEffects() {
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
  if (averageBrightness > 80 + brightnessAdjustment) {
    avgerageBrightnessAdjustment -= 10;
  } else if (averageBrightness < 60 + brightnessAdjustment) {
    avgerageBrightnessAdjustment += 10;
  }
}

public int increaseBrightness (int c, int amount) {
  float newRed = constrain(red(c) + amount, 0, 255);
  float newGreen = constrain(green(c) + amount, 0, 255);
  float newBlue = constrain(blue(c) + amount, 0, 255);
  return color(newRed, newGreen, newBlue); 
}

public int mergeColors(int a, int b) {
  return blendColor(a, b, blendColorMode);
}

public int getPixel (int x, int y) {
  return pixels[y*canvasWidth+x];
}

public void handDrawnEffects() {
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

public void videoEffect() {
  if (effect0) {
    beatDetectionOn = false;
    avgerageBrightnessAdjustment = 0;
    noStroke();
    fill(color(0, 0, 0));
    rect(0, colorPickerHeight + buttonRowHeight, canvasWidth - colorDisplayBandWidth, screenshotHeight);
  }
}

public void colorLockEffect() {
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
int headStep = 0;
public void headEffect() {
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

public void djEffect() {
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
public void rainEffect() {
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

public void walkColorEffect() {
  if (effect5) {
    colorMode(HSB);
    colorWalkValue = (colorWalkValue + 1) % 765;
    colorOverlay = color(round(colorWalkValue / 3), 255, 255);
    colorMode(RGB);
  }
}
// Pixel Pusher setup class.
class PixelPusherObserver implements Observer {
  public boolean hasStrips = false;
  public void update(Observable registry, Object updatedDevice) {
    this.hasStrips = true;
  }
}

public void drawPixelPusher(){
  if (pixelPusherObserver.hasStrips) {
    List<PixelPusher> pusherList = registry.getPushers();
    PixelPusher[] pixelPusherArray = new PixelPusher[8];
    // Make sure the pixel pushers are in order
    for(PixelPusher pusher : pusherList) {
      int controllerNumber = pusher.getControllerOrdinal();
      pixelPusherArray[controllerNumber] = pusher;
    }
 
    for(PixelDefinition pixel : pixelMap) {      
      // All the PixelPushers may not always be present so check first
      if (pixelPusherArray[pixel.controller] != null) {
        PixelPusher pusher = pixelPusherArray[pixel.controller];
        Strip strip = pusher.getStrip(pixel.strip);
        strip.setPixel(pixel.colorValue, pixel.number);
      }
    } 
  }
}

public void colorPixels() {
  for(PixelDefinition pixel : pixelMap) {  
    int averageColor = pixels[(pixel.y+60)*canvasWidth+pixel.x];
    pixel.colorValue = averageColor;
  }
}

public void drawDisplay() {
  for(PixelDefinition pixel : pixelMap) { 
      fill(pixel.colorValue);
      rect(pixel.x, pixel.y + 210, 3, 3);
  }
}
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

public void loadDefaults() {  
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
public void loadPixelMap() {
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
  
// Screenshot libraries




// Pixel pusher libraries








// General Java libraries


// Beat Detection libraries



// UDP libraries

public void beatSetup() {
  minim = new Minim(this);
  audioInput = minim.getLineIn();
  beatDetect = new BeatDetect(audioInput.bufferSize(), audioInput.sampleRate());
  beatDetect.setSensitivity(10);  
  beatListener = new BeatListener(beatDetect, audioInput);  
}

public void beatDetect() {
  // All these are magic numbers just to make it look good
  beatLevel = constrain(round(pow((audioInput.left.level()*gainSetting*10),1.4f)) - 10, 0, 100);
  if (beatDetectionOn) {
    beatBrightness = beatLevel;
  } else {
    beatBrightness = 0;
  }
}

class BeatListener implements AudioListener
{
  private BeatDetect beatDetect;
  private AudioInput source;
  
  BeatListener(BeatDetect beat, AudioInput source)
  {
    this.source = source;
    this.source.addListener(this);
    this.beatDetect = beat;
  }
  
  public void samples(float[] samps)
  {
    beatDetect.detect(source.mix);
  }
  
  public void samples(float[] sampsL, float[] sampsR)
  {
    beatDetect.detect(source.mix);
  }
}

public void stop()
{
  audioInput.close();
  minim.stop();
  super.stop();
}
Button button1;
Button button2;
Button button3;
Button button4;
Button button5;
Button button6;
Button button7;
Button button8;
Button button9;
Button button10;
Button button11;
Button button21;
Button button22;
Button button23;
Button button24;
Button button25;

Button button99;

public void loadButtons() {
  button1 = new Button("Reset All", 0, 0, 80, buttonRowHeight, false);
  button2 = new Button("Brigent", 80, 0, 50, buttonRowHeight, false);
  button3 = new Button("Darken", 130, 0, 50, buttonRowHeight, false);
  button4 = new Button("Hit White", 180, 0, 60, buttonRowHeight, false);
  button5 = new Button("Hit Black", 240, 0, 60, buttonRowHeight, false);
  button6 = new Button("Beat On/Off", 300, 0, 80, buttonRowHeight, false);
  button7 = new Button("Gain Up", 380, 0, 55, buttonRowHeight, false);
  button8 = new Button("Gain Down", 435, 0, 65, buttonRowHeight, false);
  button9 = new Button("Video On/Off", 500, 0, 90, buttonRowHeight, false);
  button10 = new Button("Overlay Color On/Off", 590, 0, 130, buttonRowHeight, false);
  button11 = new Button("Take Color", 720, 0, 80, buttonRowHeight, false);
  
  // Buttons running vertically down the side
  button21 = new Button("C. Lock", canvasWidth - colorDisplayBandWidth - 25, topSectionHeight, colorDisplayBandWidth + 25, buttonRowHeight, true);
  button22 = new Button("Head", canvasWidth - colorDisplayBandWidth - 10, topSectionHeight + buttonRowHeight * 1, colorDisplayBandWidth + 10, buttonRowHeight, true);
  button23 = new Button("DJ", canvasWidth - colorDisplayBandWidth, topSectionHeight + buttonRowHeight * 2, colorDisplayBandWidth, buttonRowHeight, true);
  button24 = new Button("Rain", canvasWidth - colorDisplayBandWidth - 10, topSectionHeight + buttonRowHeight * 3, colorDisplayBandWidth + 10, buttonRowHeight, true);
  button25 = new Button("C. Walk", canvasWidth - colorDisplayBandWidth - 25, topSectionHeight + buttonRowHeight * 4, colorDisplayBandWidth + 25, buttonRowHeight, true);
}

// mouse button clicked
public void mousePressed()
{
  if (button1.MouseIsOver()) {
    loadDefaults();
  }
  if (button2.MouseIsOver()) {
    brightnessAdjustment += 1;
  }
  if (button3.MouseIsOver()) {
    brightnessAdjustment -= 1;
  }
  if (button4.MouseIsOver()) {
    avgerageBrightnessAdjustment = 500;
  }
  if (button5.MouseIsOver()) {
    avgerageBrightnessAdjustment = -500;
  }
  if (button6.MouseIsOver()) {
    beatDetectionOn = !beatDetectionOn;
    beatBrightness = 0;
    brightnessAdjustment = 0;
    avgerageBrightnessAdjustment = 0;
  }
  if (button7.MouseIsOver()) {
    gainSetting += 1;
  }
  if (button8.MouseIsOver()) {
    gainSetting -= 1;
  }
  if (button9.MouseIsOver()) {
    effect0 = !effect0;
  }
  if (button10.MouseIsOver()) {
    overlayColor = !overlayColor;
  }
  if (button11.MouseIsOver()) {
    colorOverlay = colorSelected;
  }
  if (button21.MouseIsOver()) {
    effect1 = !effect1;
  }
  if (button22.MouseIsOver()) {
    effect2 = !effect2;
  }
  if (button23.MouseIsOver()) {
    effect3 = !effect3;
  }
  if (button24.MouseIsOver()) {
    effect4 = !effect4;
  }
  if (button25.MouseIsOver()) {
    effect5 = !effect5;
  }
}

public void drawButtons() {
  button1.Draw();
  button2.Draw();
  button3.Draw();
  button4.Draw();
  button5.Draw();
  button6.Draw();
  button7.Draw();
  button8.Draw(); 
  button9.Draw();
  button10.Draw();   
  button11.Draw(); 
  button21.Draw(); 
  button22.Draw(); 
  button23.Draw(); 
  button24.Draw(); 
  button25.Draw(); 
}

// the Button class
class Button {
  String label; 
  float x;      
  float y;      
  float w;     
  float h;    
  boolean isDark;
  
  // constructor
  Button(String labelB, float xpos, float ypos, float widthB, float heightB, boolean isDark) {
    label = labelB;
    x = xpos;
    y = ypos;
    w = widthB;
    h = heightB;
    this.isDark = isDark;
  }
  
  public void Draw() {
    if (isDark) {
      fill(0);
    } else {
      fill(218);
    }
    rect(x, y, w, h);
    textAlign(CENTER, CENTER);
    if (isDark) {
      fill(218);
    } else {
      fill(0);
    }
    text(label, x + (w / 2), y + (h / 2));
  }
  
  public boolean MouseIsOver() {
    if (mouseX > x && mouseX < (x + w) && mouseY > y && mouseY < (y + h)) {
      return true;
    }
    return false;
  }
}
public class ColorPicker {
  int x, y, w, h, c;
  PImage cpImage;
  
  public ColorPicker (int x, int y, int w, int h, int c) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.c = c;
    
    cpImage = new PImage(w, h);

    // draw color.
    int cw = w - 60;
    for( int i = 0; i < cw; i++) {
      float nColorPercent = i / (float)cw;
      float rad = (-360 * nColorPercent) * (PI / 180);
      int nR = (int)(cos(rad) * 127 + 128) << 16;
      int nG = (int)(cos(rad + 2 * PI / 3) * 127 + 128) << 8;
      int nB = (int)(Math.cos(rad + 4 * PI / 3) * 127 + 128);
      int nColor = nR | nG | nB;
      
      setGradient(i, 0, 1, h/2, 0xFFFFFF, nColor);
      setGradient(i, (h/2), 1, h/2, nColor, 0x000000);
    }
    
    // draw black/white.
    drawRect(cw, 0, 30, h/2, 0xFFFFFF);
    drawRect(cw, h/2, 30, h/2, 0);
    
    // draw grey scale.
    for(int j=0; j < h; j++) {
      int g = 255 - (int)(j/(float)(h-1) * 255 );
      drawRect(w-60, j, 30, 1, color( g, g, g ) );
    }
  }

  private void setGradient(int x, int y, float w, float h, int c1, int c2 )
  {
    float deltaR = red(c2) - red(c1);
    float deltaG = green(c2) - green(c1);
    float deltaB = blue(c2) - blue(c1);

    for (int j = y; j < (y + h); j++){
      int c = color(red(c1)+(j-y)*(deltaR/h), green(c1)+(j-y)*(deltaG/h), blue(c1)+(j-y)*(deltaB/h));
      cpImage.set(x, j, c);
    }
  }
  
  private void drawRect(int rx, int ry, int rw, int rh, int rc) {
    for(int i = rx; i < rx + rw; i++) {
      for(int j = ry; j < ry + rh; j++) {
        cpImage.set(i, j, rc);
      }
    }
  }
  
  public void render () {
    image(cpImage, x, y);
    if(mousePressed &&
       mouseX >= x && 
       mouseX < x + w &&
       mouseY >= y &&
       mouseY < y + h) {
      colorSelected = get(mouseX, mouseY);
    }
  }
}
// Keyboard
public void keyPressed() {
  switch (keyCode) {
    case UP: 
       brightnessAdjustment += 10;
       break;
    case DOWN: 
      brightnessAdjustment -= 10;
      break;
    case LEFT: 
      overlayColor = true;
      break;
    case RIGHT: 
      overlayColor = false;
      break;
    // Space Bar
    case 32:
       colorOverlay = colorSelected;
       break;
    // 0-9 Keys
    case 48:  colorSelected = color( 255,   0, 127); break;
    case 49:  colorSelected = color( 255,   0,   0); break;
    case 50:  colorSelected = color( 255, 128,   0); break;
    case 51:  colorSelected = color( 255, 255,   0); break;
    case 52:  colorSelected = color( 128, 255,   0); break;
    case 53:  colorSelected = color(   0, 255,   0); break;
    case 54:  colorSelected = color(   0, 255, 128); break;
    case 55:  colorSelected = color(   0, 255, 255); break;
    case 56:  colorSelected = color(   0, 128, 255); break;
    case 57:  colorSelected = color(   0,   0, 255); break;
    case 45:  colorSelected = color( 127,   0, 255); break;
    case 61:  colorSelected = color( 255,   0, 255); break;
    // Z key
    case 90: 
      overlayColor = true;
      break;
    // X key
    case 88: 
      overlayColor = false;
      break;
    // F1-12 keys
    case 112: blendColorMode = DODGE; break; 
    case 113: blendColorMode = ADD; break;
    case 114: blendColorMode = SUBTRACT; break; 
    case 115: blendColorMode = DARKEST; break;
    case 116: blendColorMode = LIGHTEST; break;
    case 117: blendColorMode = DIFFERENCE; break;
    case 118: blendColorMode = EXCLUSION; break;
    case 119: blendColorMode = MULTIPLY; break;
    case 120: blendColorMode = SCREEN; break;
    case 121: blendColorMode = OVERLAY; break;
    case 122: blendColorMode = BURN; break; 
    case 123: blendColorMode = SOFT_LIGHT; break;
    // Q key
    case 81: 
      colorSelected = color(red(colorSelected) + 5, green(colorSelected), blue(colorSelected));
      break;
    // A key
    case 65: 
      colorSelected = color(red(colorSelected) - 5, green(colorSelected), blue(colorSelected));
      break;
    // W key
    case 87: 
      colorSelected = color(red(colorSelected), green(colorSelected) + 5, blue(colorSelected));
      break;
    // S key
    case 83: 
      colorSelected = color(red(colorSelected), green(colorSelected) - 5, blue(colorSelected));
      break;
    // E key
    case 69: 
      colorSelected = color(red(colorSelected) + 5, green(colorSelected), blue(colorSelected) + 5);
      break;
    // D key
    case 68: 
      colorSelected = color(red(colorSelected) - 5, green(colorSelected), blue(colorSelected) - 5);
      break;
    // R key
    case 82: 
      brightnessAdjustment = 150;
      break;
    // F key
    case 70: 
      brightnessAdjustment = 0;
      break;
    // V key
    case 86: 
      brightnessAdjustment = -220;
      break;
    // , key
    case 44: 
      screenshotYAdjustment -= 5;
      break;
    // . key
    case 46: 
      screenshotYAdjustment += 5;
      break;
  }
}
public void udpSetup() {
  udp = new UDP(this, 9901);
}

int updCounter = 0;
public void udpDraw() {
  if (updCounter == 0) {
    // Command line broadcast redirect:
    // sudo socat UDP4-RECVFROM:9000,fork UDP4-SENDTO:192.168.86.255:9001,broadcast
    udp.send(str(frameCount), "localhost", 9000);
    udp.send("STFU", "10.1.1.200", 9901);
  }
  updCounter = (updCounter + 1) % 20;
}
  public void settings() {  size(825, 350); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "KS2017" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
