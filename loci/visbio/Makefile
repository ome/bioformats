#
# Makefile
#

# VisBio application for visualization of multidimensional
# biological image data. Copyright (C) 2002-2005 Curtis Rueden.
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

# Adapted from VisAD's Makefile

default:
	@echo ""
	@echo "Type one of the following:"
	@echo "	make compile	to compile java files from scratch"
	@echo "	make recompile	to compile recently altered java files"
	@echo "	make clean	to remove class files"
	@echo "	make jar	to create visbio.jar"
	@echo "	make source	to create visbio_src.jar"
	@echo "	make docs	to create visbio_docs.jar"
	@echo "	make visad	to create VisBio's visad.jar from VisAD source"
	@echo "	make all	compiles from scratch, then does jar, source and docs"
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
JAR_DIR = jar

# programs used to clean up the results of previous builds
#
RM = rm -f
MKDIR = mkdir
RMDIR = rm -rf

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
	loci.visbio \
	loci.visbio.data \
	loci.visbio.ext \
	loci.visbio.help \
	loci.visbio.ome \
	loci.visbio.overlays \
	loci.visbio.state \
	loci.visbio.util \
	loci.visbio.view

CLASS_FILES = \
	`echo $(PACKAGES)' ' | sed -e 's/\./\//g' -e 's/ /\/\*\.class /g'`

JAVA_FILES = \
	`echo $(PACKAGES)' ' | sed -e 's/\./\//g' -e 's/ /\/\*\.java /g'` \

RESOURCE_FILES = \
	loci/visbio/*.png \
	loci/visbio/*.gif \
	loci/visbio/help/*.html \
	loci/visbio/help/*.png \
	loci/visbio/overlays/*.png \
	loci/visbio/view/*.png

JAR_FILES = \
	loci/visbio/visbio.manifest \
	loci/visbio/classes.txt \
	$(CLASS_FILES) \
	$(RESOURCE_FILES)

SOURCE_FILES = \
	loci/visbio/visbio.manifest \
	$(JAVA_FILES) \
	loci/visbio/package.html \
	loci/visbio/Makefile \
	loci/visbio/*.txt \
	loci/visbio/*.cpp \
	loci/visbio/*.m \
	$(RESOURCE_FILES)

VISAD_PACKAGES = \
	visad \
	visad.bom \
	visad.browser \
	visad.collab \
	visad.data \
	visad.data.avi \
	visad.data.bio \
	visad.data.gif \
	visad.data.ij \
	visad.data.jai \
	visad.data.mcidas \
	visad.data.netcdf \
	visad.data.qt \
	visad.data.tiff \
	visad.data.units \
	visad.data.visad \
	visad.data.visad.object \
	visad.java2d \
	visad.java3d \
	visad.util

VISAD_FILES= \
	DATE \
	`echo $(VISAD_PACKAGES)' ' | sed -e 's/\./\//g' -e 's/ /\/\*\.class /g'` \
	visad/util/*.jhf

recompile:
	@cd ../.. ; \
	for i in $(PACKAGES); do \
	  dir=`echo $$i | sed -e 's/\./\//g'`; \
	  if [ ! -z "$$dir" ]; then \
	    if [ ! -d $$dir ]; then \
	      echo "$$dir does not exist"; \
	    else \
	      echo "*** Building in $$dir"; \
	      (cd $$dir; \
	      if [ ! -z "$(COMPILE_ALL)" ]; then \
	        if ls *.java >/dev/null 2>&1; then \
	          echo $(JC) $(JFLAGS) '*.java'; \
	          $(JC) $(JFLAGS) *.java; \
	        fi; \
	      fi; \
	      for j in *.java; do \
	        if [ -f $$j ]; then \
	          b=`basename $$j .java`; \
	          if [ -z "$(COMPILE_ALL)" ]; then \
	            c=$$b.class; \
	            compile=false; \
	            if [ ! -f $$c ]; then \
	              compile=true; \
	            else \
	              set `ls -td $$j $$c` X; \
	              if [ $$c != $$1 ]; then \
	                compile=true; \
	              fi; \
	            fi; \
	            if $$compile; then \
	              (echo "$(JC) $(JFLAGS) $$j" && \
	                $(JC) $(JFLAGS) $$j || true); \
	            fi; \
	          fi; \
	        fi; \
	      done); \
	    fi; \
	  fi; \
	done

compile: clean
	@make COMPILE_ALL=true recompile

clean:
	@cd ../.. ; \
	for i in $(PACKAGES); do \
	  dir=`echo $$i | sed -e 's/\./\//g'`; \
	  if [ ! -z "$$dir" ]; then \
	    if [ ! -d $$dir ]; then \
	      echo "$$dir does not exist"; \
	    else \
	      echo "*** Cleaning in $$dir"; \
	      (cd $$dir; \
	      $(RM) *.class); \
	    fi; \
	  fi; \
	done

jar:
	@cd ../.. ; \
	if [ ! -d $(JAR_DIR) ]; then $(MKDIR) $(JAR_DIR); fi ; \
	$(JAR) $(JARVERBOSE)cfm $(JAR_DIR)/visbio.jar $(JAR_FILES) ; \
	ls -l $(JAR_DIR)/visbio.jar

source:
	@cd ../.. ; \
	if [ ! -d $(JAR_DIR) ]; then $(MKDIR) $(JAR_DIR); fi ; \
	$(JAR) $(JARVERBOSE)cf $(JAR_DIR)/visbio_src.jar $(SOURCE_FILES) ; \
	ls -l $(JAR_DIR)/visbio_src.jar

docs:
	@cd ../.. ; \
	if [ -d $(DOC_DIR) ]; then $(RM) $(DOC_DIR); fi ; \
	$(MKDIR) $(DOC_DIR) ; \
	$(DOC) $(DOCFLAGS) $(PACKAGES) ; \
	if [ ! -d $(JAR_DIR) ]; then $(MKDIR) $(JAR_DIR); fi ; \
	$(JAR) $(JARVERBOSE)cf $(JAR_DIR)/visbio_docs.jar $(DOC_DIR) ; \
	$(RMDIR) $(DOC_DIR) ; \
	ls -l $(JAR_DIR)/visbio_docs.jar

visad:
	@cd ../.. ; \
	date > DATE ; \
	${JAR} ${JARVERBOSE}cfm $(JAR_DIR)/visad.jar \
		visad/VisAD.Manifest $(VISAD_FILES) ; \
	ls -l $(JAR_DIR)/visad.jar

all: compile jar source docs
