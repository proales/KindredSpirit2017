
Button button1;
Button button2;
Button button3;
Button button4;
Button button5;
Button button6;
Button button7;

void loadButtons() {
  button1 = new Button("Overlay Color On", 0, 0, 120, 30);
  button2 = new Button("Overlay Color Off", 120, 0, 120, 30);
  button3 = new Button("Brigent", 240, 0, 60, 30);
  button4 = new Button("Darken", 300, 0, 60, 30);
  button5 = new Button("Hit White", 360, 0, 60, 30);
  button6 = new Button("Hit Black", 420, 0, 60, 30);
  button7 = new Button("Take Color", 480, 0, 60, 30);
}

void drawButtons() {
  button1.Draw();
  button2.Draw();
  button3.Draw();
  button4.Draw();
  button5.Draw();
  button6.Draw();
  button7.Draw();  
}

// mouse button clicked
void mousePressed()
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