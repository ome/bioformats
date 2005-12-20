// scale.cpp

#include <iostream>
using namespace std;

// assumes no difference between text and binary modes;
// not totally portable (e.g., 0x0d bytes fail on Windows)

int main(int argc, char** argv) {
  int numParams = 2;
  char** params = new char*[numParams];

  int width = -1, height = -1, planes = -1;
  bool predict = false, listParams = false;
  int p = 0;
  for (int i=1; i<argc; i++) {
    if (strcmp(argv[i], "--predict") == 0) predict = true;
    else if (strcmp(argv[i], "--params") == 0) listParams = true;
    else if (strcmp(argv[i], "--width") == 0 || strcmp(argv[i], "-x") == 0) {
      if (i < argc - 1) width = atoi(argv[++i]);
    }
    else if (strcmp(argv[i], "--height") == 0 || strcmp(argv[i], "-y") == 0) {
      if (i < argc - 1) height = atoi(argv[++i]);
    }
    else if (strcmp(argv[i], "--planes") == 0 || strcmp(argv[i], "-n") == 0) {
      if (i < argc - 1) planes = atoi(argv[++i]);
    }
    else if (p < numParams) params[p++] = argv[i];
    else cerr << "Ignoring extraneous parameter: " << argv[i] << endl;
  }

  if (listParams) {
    cout << "Scale X\n1\nScale Y\n1\n";
    delete params;
    return 0;
  }

  if (p < numParams) {
    cerr << "Insufficient parameters (got " << p
         << ", expected " << numParams << ")\n";
    delete params;
    return 1;
  }
  int scale_x = atoi(params[0]);
  int scale_y = atoi(params[1]);
  if (scale_x <= 0 || scale_y <= 0) {
    cerr << "Invalid scale parameters\n";
    delete params;
    return 2;
  }
  if (width <= 0 || height <= 0 || planes <= 0) {
    cerr << "Invalid image dimensions\n";
    delete params;
    return 3;
  }

  if (predict) {
    cout << width / scale_x << "\n"
         << height / scale_y << "\n"
         << planes << "\n";
    delete params;
    return 0;
  }

  int size = sizeof(float) * width * height * planes;
  char* memblock = new char[size];

  int nx = width / scale_x;
  int ny = height / scale_y;
  cerr << "scale: Output image will be " << nx << " x " << ny << " (" << size << " bytes)" << endl;//TEMP
  cerr << flush;

  cin.read(memblock, size);

  cerr << "scale: Successfully read input data\n";//TEMP
  float* pix = (float*) memblock;

  float* img = new float[ny * nx];
  for (int p=0; p<planes; p++) {
    int base = p * width * height;
    for (int y=0; y<ny; y++) {
      int iy = scale_y * y;
      for (int x=0; x<nx; x++) {
        int ix = scale_x * x;
        img[nx * y + x] = pix[base + width * iy + ix];
      }
    }
    cout.write((char*) img, sizeof(float) * ny * nx);
  }
  cerr << "scale: Successfully wrote output data\n";//TEMP
  delete img;

  delete[] memblock;

  delete params;
  return 0;
}
