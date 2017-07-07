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
  // Size will only take numbers so keep
  // in sync with global height & width
  
  
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
public void draw() {
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

//int getAverageColor(int sheet, int pixelX) {
//  int x = pixelX % sheetWidth;
//  int y = pixelX / sheetHeight;
//  return avgColors[sheet + x][y];
//}
public void addEffects() {
 
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

public int linearToColor (int value) {
return HSBtoRGB(((float) value)/100.0f*.85f, (float) 1, (float) 1);
}

public int HSBtoRGB(float h, float s, float v) {
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

public int increaseBrightness (int c, int amount) {
  float newRed = constrain(red(c) + amount, 0, 255);
  float newGreen = constrain(green(c) + amount, 0, 255);
  float newBlue = constrain(blue(c) + amount, 0, 255);
  return color(newRed, newGreen, newBlue); 
}

public int mergeColors(int a, int b) {
  return blendColor(a, b, blendColorMode);
}

public int avgColor(int col, int row) {
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

public int getPixel (int x, int y) {
  return pixels[y*canvasWidth+x];
}
// Canvas setup
PImage screenshot; 
int pixelSize = 10;
int sheetWidth = 20;
int sheetHeight = 20;
int numberColumns = 60;
int numberRows = 20;
int pixelColumns = pixelSize * numberColumns;
int pixelRows = pixelSize * numberRows;
int canvasWidth = 825; 
int canvasHeight = 200;
int colorBandWidth = 25;
int screenshotYAdjustment = 150;

// Pixel Pusher setup.
DeviceRegistry registry;
PixelPusherObserver pixelPusherObserver;

// Effects setup
int brightnessAdjustment = 0;
int colorNumber = 0;
int colorOverlay = color(255, 0, 0);
int colorSelected = color(0, 255, 0);
int blendColorMode = OVERLAY;
boolean overlayColor = false;

List<Strip> strips;
ColorPicker colorPicker;

ArrayList<PixelDefinition> pixelMap = new ArrayList<PixelDefinition>();
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
      if (lineArray.length > 1) {
        pd.controller = Integer.parseInt(lineArray[0]);
        pd.strip = Integer.parseInt(lineArray[1]);
        pd.number = Integer.parseInt(lineArray[2]);
        pd.x = Integer.parseInt(lineArray[3]);
        pd.y = Integer.parseInt(lineArray[4]);
        pixelMap.add(pd);
      }
    }
  }
}
  
// Screenshot libraries




// Pixel pusher libraries








// General Java libraries


Button button1;
Button button2;
Button button3;
Button button4;
Button button5;
Button button6;
Button button7;

public void loadButtons() {
  button1 = new Button("Overlay Color On", 0, 0, 120, 30);
  button2 = new Button("Overlay Color Off", 120, 0, 120, 30);
  button3 = new Button("Brigent", 240, 0, 60, 30);
  button4 = new Button("Darken", 300, 0, 60, 30);
  button5 = new Button("Hit White", 360, 0, 60, 30);
  button6 = new Button("Hit Black", 420, 0, 60, 30);
  button7 = new Button("Take Color", 480, 0, 60, 30);
}

public void drawButtons() {
  button1.Draw();
  button2.Draw();
  button3.Draw();
  button4.Draw();
  button5.Draw();
  button6.Draw();
  button7.Draw();  
}

// mouse button clicked
public void mousePressed()
{
  if (button1.MouseIsOver()) {
    overlayColor = true;
  }
}

// the Button class
class Button {
  String label; 
  float x;      
  float y;      
  float w;     
  float h;    
  
  // constructor
  Button(String labelB, float xpos, float ypos, float widthB, float heightB) {
    label = labelB;
    x = xpos;
    y = ypos;
    w = widthB;
    h = heightB;
  }
  
  public void Draw() {
    fill(218);
    rect(x, y, w, h);
    textAlign(CENTER, CENTER);
    fill(0);
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
       print("Brightness at: " + brightnessAdjustment + "\n");
       break;
    case DOWN: 
      brightnessAdjustment -= 10;
      print("Brightness at: " + brightnessAdjustment + "\n");
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
  
  print("BKey Code: " + keyCode + "\n");
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
