//
// scale.cpp
//

#include <iostream>
using namespace std;

// downsamples input image by the given X and Y scale factors
// compile with "g++ scale.cpp -o scale"

// assumes no difference between text and binary modes;
// not totally portable (e.g., newlines cause difficulties on Windows)

int main(int argc, char** argv) {
  // parse command line parameters
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

  // output parameter names and default values
  if (listParams) {
    cout << "Scale X\n1\nScale Y\n1\n";
    delete params;
    return 0;
  }

  // perform some error checking on the parameters
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

  // output image dimensions for the given input dimensions and parameters
  if (predict) {
    cout << width / scale_x << "\n"
         << height / scale_y << "\n"
         << planes << "\n";
    delete params;
    return 0;
  }

  // read input image data from stdin
  int size = sizeof(float) * width * height * planes;
  char* memblock = new char[size];
//  cerr << "scale: Input image will be " << width << " x " << height
//       << " (" << size << " bytes)\n" << flush;
  int nx = width / scale_x;
  int ny = height / scale_y;
//  cerr << "scale: Output image will be " << nx << " x " << ny
//       << " (" << sizeof(float)*nx*ny << " bytes)\n" << flush;
  cin.read(memblock, size);
//  cerr << "scale: Successfully read input data\n";

  // scale image data with a simple nearest neighbor algorithm
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
    // write output image data to stdout
    cout.write((char*) img, sizeof(float) * ny * nx);
  }
//  cerr << "scale: Successfully wrote output data\n";
  delete img;

  delete[] memblock;

  delete params;
  return 0;
}
