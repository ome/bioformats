CC = gcc
CFLAGS = `xml2-config --cflags`
LDFLAGS = `xml2-config --libs` 
Example = example
ExampleFiles = example.c

all: $(Example)

$(Example): $(ExampleFiles)
	$(CC) -o $(Example) $(CFLAGS) $(ExampleFiles) $(LDFLAGS)

clean:
	rm -f $(Example)
