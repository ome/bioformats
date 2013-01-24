
#include "test1a.h"

char * getDesc1();
char getDesc2();

enum Shades {
	white,
	grey,
	black
};

enum Fruits {
	peach = 2,
	nectarine,
	cataloupe,
	banana = 7,
	apple,
	orange
};

char * Globalname;

static const unsigned int MY_CONSTANT = 1234;

struct Record1 {
	int height;
	int width;
}; 

struct Record2 {
	int size;
	char * name;
};

class Klass1 {

public:

	Klass1();
	Klass1(int category, char * notes);
	~Klass1();

	Record2 * getRec1();
	char * getName(char * prefix, unsigned long maxSize);
	Record2 getRec2();
	void setRec(Record2 * newRecord);
	int getSize(int index);
	char * getName(char * prefix, int maxSize);

private:

	Record2 * rec;

};




