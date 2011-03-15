
/* File: test1a.h
*/

#include "test1b.h"


enum Colors {
  red = 1,
  green,
  blue
};



typedef struct Record3 {
	int category;
	unsigned long quantity;
	char * description;
} * Record3Ptr;


