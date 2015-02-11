package colorspace.yuvspace;

public class YUV422 implements YUVImage {

  protected int width;
  protected int height;
  protected int oneFrameSize;
  protected int uIndex;
  protected int vIndex;

  public YUV422(int width, int height) {
    setUpYUV422Image(width, height);
  }
  
  protected void setUpYUV422Image(int width, int height) {
    this.width = width;
    this.height = height;
    int uvRowBytes;
    int uvSize;
    if(width % 2 != 0) {
      uvRowBytes = (width + 1) / 2;
    } else {
      uvRowBytes = width / 2;
    }
    
    uvSize = uvRowBytes * height;
    oneFrameSize = width * height + uvSize * 2;
    uIndex = width * height;
    vIndex = uIndex + uvSize;
  }
  
  public int getWidth() {
    return width;
  }
  public int getHeight() {
    return height;
  }
  public int getOneFrameSize() {
    return oneFrameSize;
  }
  public int getUIndex() { 
    return uIndex;
  }
  public int getVIndex() {
    return vIndex;
  }
  public void setSize(int width, int height) {
    setUpYUV422Image(width, height);
  }
  
  public int[] convertYUVtoRGB(byte[] yuv) {

    
    int[] rgb = new int[width*height];
    int yValue = 0;
    int uValue = 0;
    int vValue = 0;
    int uAndvPosition = 0;
    int a = 0xFF000000;
    int r, g, b;
    for(int i = 0; i < width*height; i++) {
      yValue = yuv[i] & 0xFF;
      if(((i%width)%2) == 0) {
        // U 
        uValue = yuv[uIndex + uAndvPosition] & 0xFF;
        // V
        vValue = yuv[vIndex + uAndvPosition] & 0xFF;
        uAndvPosition++;
      }   
      
      r = (int)(yValue + 1.4075 * (vValue - 128));
      r = (r < 0 ? 0 : (r > 255 ? 255 : r));
      g = (int)(yValue - 0.3455 * (uValue - 128) - 0.7169  * (vValue - 128));
      g = (g < 0 ? 0 : (g > 255 ? 255 : g));
      b = (int)(yValue + 1.7790 * (uValue - 128));  
      b = (b < 0 ? 0 : (b > 255 ? 255 : b));
      
      rgb[i] = a | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);  
    }
    return rgb;
  }
  
  public byte[] convertRGBtoYUV(int[] rgb) {
    byte[] yuvFrame = new byte[oneFrameSize];

    int a, r, g, b;
    int uAndvPosition = 0;
    for(int i = 0; i < width*height; i++) {
        a = (rgb[i] & 0xFF000000) >> 24;
        r = (rgb[i] & 0xFF0000) >> 16;
        g = (rgb[i] & 0xFF00) >> 8;
        b = (rgb[i] & 0xFF) >> 0;
        // Y
        yuvFrame[i]  = (byte)(0.299 * r + 0.587 * g + 0.114 * b);
        if(((i%width)%2) == 0) {
          // U 
          yuvFrame[uIndex + uAndvPosition] = (byte)(-0.169 * r - 0.331 * g + 0.5 * b + 128);
          // V
          yuvFrame[vIndex + uAndvPosition] = (byte)(0.5 * r - 0.419 * g - 0.081 * b + 128);
          
          uAndvPosition++;
        }  
    }
    return yuvFrame;
  }
}