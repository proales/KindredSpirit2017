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

void loadButtons() {
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
void mousePressed()
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

void drawButtons() {
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
  
  void Draw() {
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
  
  boolean MouseIsOver() {
    if (mouseX > x && mouseX < (x + w) && mouseY > y && mouseY < (y + h)) {
      return true;
    }
    return false;
  }
}