void beatSetup() {
  minim = new Minim(this);
  audioInput = minim.getLineIn();
  beatDetect = new BeatDetect(audioInput.bufferSize(), audioInput.sampleRate());
  beatDetect.setSensitivity(10);  
  beatListener = new BeatListener(beatDetect, audioInput);  
}

void beatDetect() {
  // All these are magic numbers just to make it look good
  beatLevel = constrain(round(pow((audioInput.left.level()*gainSetting*10),1.4)) - 10, 0, 100);
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
  
  void samples(float[] samps)
  {
    beatDetect.detect(source.mix);
  }
  
  void samples(float[] sampsL, float[] sampsR)
  {
    beatDetect.detect(source.mix);
  }
}

void stop()
{
  audioInput.close();
  minim.stop();
  super.stop();
}