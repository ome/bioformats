#
# Makefile
#

# VisBio application for visualization of multidimensional
# biological image data. Copyright (C) 2002-2004 Curtis Rueden.
# 
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

default:
	@echo ""
	@echo "Type one of the following:"
	@echo "	make compile	to compile java files"
	@echo "	make clean	to remove class files"
	@echo "	make jar	to create visbio.jar"
	@echo "	make source	to create visbio_src.jar"
	@echo "	make docs	to create visbio_docs.jar"
	@echo ""
	@echo "You may want to uncomment one or more lines in the"
	@echo "\"Customizations\" section if you want to use something"
	@echo "other than the standard JDK tools."
	@echo ""

############################################################################
# Standard definitions -- don't change these
############################################################################

# JC is the Java compiler
# JFLAGS specifies special flags for the Java compiler
#
JC = javac
JFLAGS = -J-mx64m

# JAR is the Java archive executable
# JARVERBOSE is normally 'v'
#
JAR = jar
JARVERBOSE = v

# program used to clean up the results of previous builds
#
RM = rm -f

# DOC is the program used to generate the documentation
# DOC_DIR is the top-level directory where generated documentation is stored
# DOC_MEM is the amount of heap used to build the documentation
# DOC_LINKS points to any external class documentation sites
# DOC_EXTRAFLAGS holds any non-standard flags and should be empty by default
# DOCFLAGS are any flags needed to run the documentation generator
#
DOC = javadoc
DOC_DIR = docs
DOC_MEM = 128m
DOC_LINKS = \
	-link http://java.sun.com/j2se/1.4.2/docs/api \
	-link http://www.ssec.wisc.edu/~dglo/visad
DOCFLAGS = -source 1.4 $(DOC_LINKS) -d $(DOC_DIR) -J-Xmx$(DOC_MEM) -package \
	-doctitle 'VisBio Documentation' $(DOC_EXTRAFLAGS)

############################################################################
# Customizations
############################################################################

# to always use IBM's 'jikes' compiler, uncomment the following two lines
#JC = jikes
#JFLAGS = +P -deprecation

############################################################################
# You shouldn't need to change anything beyond this point
############################################################################

.SUFFIXES : .java .class

.java.class:
	$(JC) $(JFLAGS) $<

PACKAGES = \
	loci.ome.xml \
	loci.visbio \
	loci.visbio.data \
	loci.visbio.help \
	loci.visbio.measure \
	loci.visbio.ome \
	loci.visbio.state \
	loci.visbio.util \
	loci.visbio.view

CLASS_FILES = \
	`echo $(PACKAGES)' ' | sed -e 's/\./\//g' -e 's/ /\/\*\.class /g'` \

JAR_FILES = \
	loci/visbio/visbio.manifest \
	$(CLASS_FILES) \
	loci/visbio/*.png \
	loci/visbio/help/*.html \
	loci/visbio/help/*.png \
	loci/visbio/measure/*.png \
	loci/visbio/view/*.png

SOURCE_FILES = \
	`echo $(JAR_FILES)' ' | sed -e 's/\.class/\.java/g'` \
	loci/visbio/*.txt \
	loci/visbio/Makefile \
	loci/visbio/package.html

# CTR TODO: This is unacceptable. Needs work. Remodel after visad/Makefile.
compile:
	$(JC) $(JFLAGS) *.java

clean:
	@cd ../.. ; \
	$(RM) $(CLASS_FILES)

jar:
	@cd ../.. ; \
	$(JAR) $(JARVERBOSE)cfm visbio.jar $(JAR_FILES) ; \
	ls -l visbio.jar

source:
	@cd ../.. ; \
	$(JAR) $(JARVERBOSE)cf visbio_src.jar $(SOURCE_FILES) ; \
	ls -l visbio_src.jar

docs:
	@cd ../.. ; \
	if [ -d $(DOC_DIR) ]; then $(RM) $(DOC_DIR); fi ; \
	mkdir $(DOC_DIR) ; \
	$(DOC) $(DOCFLAGS) $(PACKAGES) ; \
	$(JAR) $(JARVERBOSE)cf visbio_docs.jar $(DOC_DIR) ; \
	$(RM) $(DOC_DIR) ; \
	ls -l visbio_docs.jar

