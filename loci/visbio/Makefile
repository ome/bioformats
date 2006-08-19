#
# Makefile
#

# VisBio application for visualization of multidimensional
# biological image data. Copyright (C) 2002-@year@ Curtis Rueden.
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
	@echo "	make source	to create visbio_src.jar"
	@echo "	make docs	to create visbio_docs.jar"
	@echo ""

############################################################################
# Standard definitions -- don't change these
############################################################################

# JAR is the Java archive executable
# JARVERBOSE is normally 'v'
#
JAR = jar
JARVERBOSE = v
JAR_DIR = jar

# utility programs
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
# You shouldn't need to change anything beyond this point
############################################################################

PACKAGES = \
	loci.util \
	loci.visbio \
	loci.visbio.data \
	loci.visbio.ext \
	loci.visbio.help \
	loci.visbio.ome \
	loci.visbio.overlays \
	loci.visbio.state \
	loci.visbio.util \
	loci.visbio.view

JAVA_FILES = \
	`echo $(PACKAGES)' ' | sed -e 's/\./\//g' -e 's/ /\/\*\.java /g'` \

RESOURCE_FILES = \
	loci/visbio/*.png \
	loci/visbio/*.gif \
	loci/visbio/help/*.html \
	loci/visbio/help/*.png \
	loci/visbio/overlays/*.png \
	loci/visbio/view/*.png

SOURCE_FILES = \
	loci/visbio/visbio.manifest \
	$(JAVA_FILES) \
	loci/visbio/package.html \
	loci/visbio/Makefile \
	loci/visbio/*.txt \
	loci/visbio/*.cpp \
	loci/visbio/*.m \
	$(RESOURCE_FILES)

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
