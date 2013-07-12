CXX=g++-4.8 -std=gnu++11 -gdwarf-4 -g3 -pedantic -Wall -Wcast-align -Wwrite-strings -Wswitch-default -Wcast-qual -Wunused-variable -Wredundant-decls -Wctor-dtor-privacy -Wnon-virtual-dtor -Wreorder -Wold-style-cast -Woverloaded-virtual -fstrict-aliasing
# -flto
CXXFLAGS=-I gen -I src -I .
LDLIBS = -lxerces-c

OBJS = \
  $(addsuffix .o, $(basename $(wildcard src/ome/bioformats/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard src/ome/bioformats/meta/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard src/ome/xml/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard src/ome/xml/model/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard src/ome/xml/model/enums/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard src/ome/xml/model/primitives/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard src/ome/xerces/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard gen/ome/xml/model/*.cpp))) \
  $(addsuffix .o, $(basename $(wildcard gen/ome/xml/model/enums/*.cpp))) \


TESTS = \
	test/color \
	test/enum  \
	test/positive-float
#	test/xerces

all: $(TESTS)

test/xerces: test/xerces.o $(OBJS)
	$(CXX) $(LDLIBS) -o $@ $^

test/enum: test/enum.o $(OBJS)
	$(CXX) $(LDLIBS) -o $@ $^

test/color: test/color.o $(OBJS)
	$(CXX) $(LDLIBS) -o $@ $^

test/timestamp: test/timestamp.o $(OBJS)
	$(CXX) $(LDLIBS) -o $@ $^

test/positive-float: test/positive-float.o $(OBJS)
	$(CXX) $(LDLIBS) -o $@ $^

%: %.o $(OBJS)
	$(CXX) $(LDLIBS) -o $@ $^

check: $(shell test/genheaders)
	for test in $(TESTS); do \
	  "$$test"; \
	done
	find test/headers -type f -executable | while read header; do \
	  echo "$$header"; \
	  "$$header"; \
	done

clean:
	rm -f $(OBJS)
	rm -f test/*.o
	rm -f $(TESTS)

PHONY: check clean all
