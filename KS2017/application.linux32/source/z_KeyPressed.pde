// Keyboard
void keyPressed() {
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