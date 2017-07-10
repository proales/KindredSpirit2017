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
Button button12;
Button button13;
Button button14;
Button button15;

void loadButtons() {
  button1 = new Button("Overlay Color On", 0, 0, 120, buttonRowHeight);
  button2 = new Button("Overlay Color Off", 120, 0, 120, buttonRowHeight);
  button3 = new Button("Brigent", 240, 0, 60, buttonRowHeight);
  button4 = new Button("Darken", 300, 0, 60, buttonRowHeight);
  button5 = new Button("Hit White", 360, 0, 70, buttonRowHeight);
  button6 = new Button("Hit Black", 430, 0, 50, buttonRowHeight);
  button7 = new Button("Reset Bright", 480, 0, 80, buttonRowHeight);
  button8 = new Button("Beat On", 560, 0, 80, buttonRowHeight);
  button9 = new Button("Beat Off", 640, 0, 80, buttonRowHeight);
  button10 = new Button("Take Color", 720, 0, 80, buttonRowHeight);
  // Buttons running vertically down the side
  button11 = new Button("V", canvasWidth - colorDisplayBandWidth, topSectionHeight, colorDisplayBandWidth, buttonRowHeight);
  button12 = new Button("H", canvasWidth - colorDisplayBandWidth, topSectionHeight + buttonRowHeight * 1, colorDisplayBandWidth, buttonRowHeight);
  button13 = new Button("DJ", canvasWidth - colorDisplayBandWidth, topSectionHeight + buttonRowHeight * 2, colorDisplayBandWidth, buttonRowHeight);
  button14 = new Button("R", canvasWidth - colorDisplayBandWidth, topSectionHeight + buttonRowHeight * 3, colorDisplayBandWidth, buttonRowHeight);
  button15 = new Button("W", canvasWidth - colorDisplayBandWidth, topSectionHeight + buttonRowHeight * 4, colorDisplayBandWidth, buttonRowHeight);
}

// mouse button clicked
void mousePressed()
{
  if (button1.MouseIsOver()) {
    overlayColor = true;
  }
  if (button2.MouseIsOver()) {
    overlayColor = false;
  }
  if (button3.MouseIsOver()) {
    brightnessAdjustment += 1;
  }
  if (button4.MouseIsOver()) {
    brightnessAdjustment -= 1;
  }
  if (button5.MouseIsOver()) {
    brightnessAdjustment = 500;
  }
  if (button6.MouseIsOver()) {
    brightnessAdjustment = -500;
  }
  if (button7.MouseIsOver()) {
    brightnessAdjustment = 0;
    avgerageBrightnessAdjustment = 0;
  }
  if (button8.MouseIsOver()) {
    beatDetectionOn = true;
  }
  if (button9.MouseIsOver()) {
    beatDetectionOn = false;
  }
  if (button10.MouseIsOver()) {
    colorOverlay = colorSelected;
  }
  if (button11.MouseIsOver()) {
    effect1 = !effect1;
  }
  if (button12.MouseIsOver()) {
    effect2 = !effect2;
  }
  if (button13.MouseIsOver()) {
    effect3 = !effect3;
  }
  if (button14.MouseIsOver()) {
    effect4 = !effect4;
  }
  if (button15.MouseIsOver()) {
    effect5 = true;
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
  button12.Draw(); 
  button13.Draw(); 
  button14.Draw(); 
  button15.Draw(); 
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
  
  void Draw() {
    fill(218);
    rect(x, y, w, h);
    textAlign(CENTER, CENTER);
    fill(0);
    text(label, x + (w / 2), y + (h / 2));
  }
  
  boolean MouseIsOver() {
    if (mouseX > x && mouseX < (x + w) && mouseY > y && mouseY < (y + h)) {
      return true;
    }
    return false;
  }
}