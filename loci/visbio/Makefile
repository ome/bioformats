#
# VisBio Makefile
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
	@echo "	make compile	to compile .java files from scratch"
	@echo "	make recompile	to compile recently altered .java files"
	@echo "	make jar	to create visbio.jar"
	@echo "	make docs	to create visbio_docs.jar"
	@echo "	make source	to create visbio_src.jar"
	@echo "	make clean	to remove class files"
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

# JAVADIR should be the top-level JDK1.4 directory
#
JAVADIR = /opt/java

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
DOC_DIR = ../docs
DOC_MEM = 150m
DOC_LINKS = http://java.sun.com/j2se/1.4.2/docs/api
DOCFLAGS = -link $(DOC_LINKS) -d $(DOC_DIR) -J-Xmx$(DOC_MEM) -package \
	-doctitle 'VisBio Documentation' $(DOC_EXTRAFLAGS)

############################################################################
# Customizations
############################################################################

# set this if your JDK/JRE is installed somewhere other than /opt/java
#JAVADIR = /usr/local/share/java

# to always use IBM's 'jikes' compiler, uncomment the following two lines
#JC = jikes
#JFLAGS = +P -deprecation

############################################################################
# You shouldn't need to change anything beyond this point
############################################################################

.SUFFIXES : .java .class

.java.class:
	$(JC) $(JFLAGS) $<

SAVE_FILES = \
	visad/README					\
	visad/DEDICATION				\
	visad/NOTICE					\
	visad/LICENSE					\
	visad/DATE					\
	visad/Makefile					\
	visad/Makefile.WinNT				\
	visad/README.nexusrmi				\
	visad/rmic_script				\
	visad/VisAD.Manifest				\
	visad/*.java					\
	visad/examples/*.java				\
	visad/examples/display_test.c			\
	visad/java3d/*.java				\
	visad/java2d/*.java				\
	visad/python/*.java				\
	visad/python/*.py				\
	visad/python/README.python			\
	visad/browser/*.java				\
	visad/browser/*.html				\
	visad/browser/README.browser			\
	visad/util/*.java				\
	visad/util/*.jhf				\
	visad/matrix/*.java				\
	visad/math/*.java				\
	visad/formula/*.java				\
	visad/ss/*.java					\
	visad/ss/*.gif					\
	visad/ss/README.ss				\
	visad/cluster/*.java				\
	visad/collab/*.java				\
	visad/data/*.java				\
	visad/data/units/*.java				\
	visad/data/units/*.jj				\
	visad/data/in/*.java				\
	visad/data/in/package.html			\
	visad/data/dods/*.java				\
	visad/data/dods/package.html			\
	visad/data/netcdf/*.java			\
	visad/data/netcdf/units/*.java			\
	visad/data/netcdf/in/*.java			\
	visad/data/netcdf/out/*.java			\
	visad/data/fits/*.java				\
	nom/tam/fits/*.java				\
	nom/tam/util/*.java				\
	nom/tam/test/*.java				\
	HTTPClient/0*					\
	HTTPClient/*.html				\
	HTTPClient/*.java				\
	ucar/COPYRIGHT					\
	ucar/VERSION					\
	ucar/multiarray/*.java				\
	ucar/util/*.java				\
	ucar/netcdf/*.java				\
	ucar/tests/*.java				\
	ucar/tests/test.nc				\
	ucar/tests/test.out				\
	dods/dap/*.java					\
	dods/dap/parser/*.java				\
	dods/dap/Server/*.java				\
	dods/util/*.java				\
	gnu/regexp/*.java				\
	visad/data/hdfeos/*.java			\
	visad/data/hdfeos/hdfeosc/*.java		\
	visad/data/hdfeos/hdfeosc/*.c			\
	visad/data/hdfeos/hdfeosc/*.h			\
	visad/data/hdfeos/hdfeosc/Makefile		\
	edu/wisc/ssec/mcidas/*.java			\
	edu/wisc/ssec/mcidas/adde/*.java		\
	visad/data/mcidas/*.java			\
	visad/data/mcidas/README.mcidas			\
	visad/data/avi/*.java				\
	visad/data/bio/*.java				\
	visad/data/biorad/*.java			\
	visad/data/ij/*.java				\
	visad/data/jai/*.java				\
	visad/data/qt/*.java				\
	visad/data/vis5d/*.java				\
	visad/data/gif/*.java				\
	visad/data/gis/*.java				\
	visad/data/text/*.java				\
	visad/data/text/example1.txt			\
	visad/data/text/example2.csv			\
	visad/data/text/example3.txt			\
	visad/data/text/README.text			\
	visad/data/tiff/*.java				\
	visad/data/visad/*.java				\
	visad/data/visad/object/*.java			\
	visad/data/hdf5/*.java				\
	visad/data/hdf5/hdf5objects/*.java		\
	ncsa/hdf/hdf5lib/*.java				\
	ncsa/hdf/hdf5lib/exceptions/*.java		\
	visad/jmet/*.java				\
	visad/paoloa/README.paoloa			\
	visad/paoloa/Makefile				\
	visad/paoloa/*.java				\
	visad/paoloa/*.f				\
	visad/paoloa/*.c				\
	visad/paoloa/*.h				\
	visad/paoloa/spline/Makefile			\
	visad/paoloa/spline/*.java			\
	visad/paoloa/spline/*.f				\
	visad/paoloa/spline/*.c				\
	visad/paoloa/spline/*.h				\
	visad/aune/README.aune				\
	visad/aune/Makefile				\
	visad/aune/shsize.fcm				\
	visad/aune/*.java				\
	visad/aune/*.f					\
	visad/aune/*.c					\
	visad/aune/*.h					\
	visad/benjamin/Makefile				\
	visad/benjamin/*.java				\
	visad/benjamin/*.f				\
	visad/benjamin/*.c				\
	visad/benjamin/*.h				\
	visad/benjamin/switch.inp			\
	visad/benjamin/*.table				\
	visad/rabin/*.java				\
	visad/bom/*.java				\
	visad/aeri/*.java				\
	visad/data/amanda/*.java			\
	visad/georef/*.java				\
	visad/meteorology/*.java			\
	visad/install/*.java				\
	visad/install/install_visad			\
	visad/install/README				\
	visad/install/README.html			\
	visad/Gridded1D.txt				\
	visad/Gridded2D.txt				\
	visad/Gridded3D.txt

TAR_FILES = \
	$(SAVE_FILES)					\
	visad/data/gif/sseclogo.gif			\
	visad/data/hdf5/COPYING				\
	visad/data/hdfeos/README.hdfeos			\
	visad/java3d/*.c				\
	visad/java3d/Makefile

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
	loci/visbio/visbio.manifest \
	`echo $(PACKAGES)' ' | sed -e 's/\./\//g' -e 's/ /\/\*\.class /g'` \
	loci/visbio/*.png \
	loci/visbio/help/*.png \
	loci/visbio/measure/*.png \
	loci/visbio/view/*.png \
	loci/visbio/help/*.html

SOURCE_FILES = \
	`echo $(PACKAGES)' ' | sed -e 's/\./\//g' -e 's/ /\/\*\.java /g'` \
	visad/ss/*.gif \
	visad/util/*.jhf

tar:
	date > DATE ; \
	cd .. ; \
	tar -cvf visad_src-2.0.tar $(TAR_FILES) ; \
	compress visad_src-2.0.tar ; \
	mv visad_src-2.0.tar.Z visad ; \
	cd visad ; \
	rm -f visadsrc.taz; \
	ln visad_src-2.0.tar.Z visadsrc.taz ; \
	ls -l visad_src-2.0.tar.Z visadsrc.taz

jar:
	@date > DATE ; \
	cd .. ; \
	${JAR} ${JARVERBOSE}cf visad_src-2.0.jar $(TAR_FILES) ; \
	mv visad_src-2.0.jar visad ; \
	cd visad ; \
	rm -f visadsrc.jar ; \
	ln visad_src-2.0.jar visadsrc.jar ; \
	ls -l visad_src-2.0.jar visadsrc.jar

classes:
	@cd .. ; \
	date > DATE ; \
	${JAR} ${JARVERBOSE}cfm visad.jar visad/VisAD.Manifest $(CLASS_FILES) ; \
	tar -cvf visad.tar visad.jar


EXAMPLES_TAR_FILES = \
	visad/examples/*.java				\
	visad/examples/display_test.c			\
	visad/examples/*.class

examplesjar:
	@cd .. ; \
	${JAR} ${JARVERBOSE}cf visad_examples.jar $(EXAMPLES_TAR_FILES) ; \
	mv visad_examples.jar visad ; \
	cd visad ; \
	ls -l visad_examples.jar


save:
	/bin/rm -rf backup
	mkdir backup
	cd .. && tar -cvf save.tar $(SAVE_FILES)
	cd backup && tar -xvf ../../save.tar
	rm -f ../save.tar
	wc `find backup -name '*.java' -print`


clear:
	cd .. && $(RM) $(JAVA_FILES)


debug:
	make compile \
	"JFLAGS = ${JFLAGS} -g"


clean:
	$(RM) *.class *~
	for i in $(PACKAGES); do					\
	  case $$i in							\
	  visad)							\
	    dir=							\
	    ;;								\
	  visad.*)							\
	    dir=`echo $$i | sed -e 's;\.;/;g' -e 's;^visad/;;'`;	\
	    ;;								\
	  *)								\
	    dir=`echo $$i | sed -e 's;\.;/;g' -e 's;^;\.\./;'`;		\
	    ;;								\
	  esac;								\
	  if [ ! -z "$$dir" ]; then					\
	    if [ ! -d $$dir ]; then					\
	      echo "$$dir does not exist";				\
	    else							\
	      echo "*** Cleaning in $$dir";				\
	      if [ -f $$dir/Makefile ]; then				\
		(cd $$dir && make -k clean);				\
	      else							\
		(cd $$dir && $(RM) *.class *.o *.so *~);		\
	      fi;							\
	    fi;								\
	  fi;								\
	done

recompile:
	@root=`cd .. && pwd`;						\
	for i in $(PACKAGES); do					\
	  case $$i in							\
	  visad)							\
	    dir=.							\
	    ;;								\
	  visad.*)							\
	    dir=`echo $$i | sed -e 's;\.;/;g' -e 's;^visad/;;'`;	\
	    ;;								\
	  *)								\
	    dir=`echo $$i | sed -e 's;\.;/;g' -e 's;^;\.\./;'`;		\
	    ;;								\
	  esac;								\
	  if [ ! -z "$$dir" ]; then					\
	    if [ ! -d $$dir ]; then					\
	      echo "$$dir does not exist";				\
	    else							\
	      echo "*** Building in $$dir";				\
	      if [ -f $$dir/Makefile -a "$$dir" != "." ]; then		\
		(cd $$dir && make					\
			JAVADIR="${JAVADIR}"				\
			JC="${JC}"					\
			JFLAGS="${JFLAGS}"				\
			RM="${RM}"					\
							) || true;	\
	      else							\
		(cd $$dir;						\
		 if [ ! -z "$(COMPILE_ALL)" ]; then			\
		   if ls *.java >/dev/null 2>&1; then			\
		     echo $(JC) $(JFLAGS) '*.java';			\
		     $(JC) $(JFLAGS) *.java;				\
		   fi;							\
		 fi;							\
		 for j in *.java; do					\
		   if [ -f $$j ]; then					\
		     b=`basename $$j .java`;				\
		     if [ -z "$(COMPILE_ALL)" ]; then			\
		       c=$$b.class;					\
		       compile=false;					\
		       if [ ! -f $$c ]; then				\
			 compile=true;					\
		       else						\
			 set `ls -td $$j $$c` X;			\
			 if [ $$c != $$1 ]; then			\
			   compile=true;				\
			 fi;						\
		       fi;						\
		       if $$compile; then				\
			 (echo "$(JC) $(JFLAGS) $$j" &&			\
			  $(JC) $(JFLAGS) $$j || true);			\
		       fi;						\
		     fi;						\
		     rmicomp=;						\
		     case "$$b" in					\
		     *Remote*Impl) rmicomp=1;;				\
		     NetcdfServer) rmicomp=1;;				\
		     esac;						\
		     if [ ! -z "$$rmicomp" ]; then			\
		       set `ls -t $$b.class $${b}_Stub.class 2>/dev/null || true` X; \
		       if [ $${b}_Stub.class != $$1 ]; then		\
			 (echo "${RMIC} -d $$root $$i.$$b" &&		\
			  ${RMIC} -d $$root $$i.$$b || true);		\
		       fi;						\
		     fi;						\
		   fi;							\
	         done);							\
	      fi;							\
	    fi;								\
	  fi;								\
	done

compile: clean
	@make								\
			JAVADIR="${JAVADIR}"				\
			JAVAARCH="${JAVAARCH}"				\
			JC="${JC}"					\
			JFLAGS="${JFLAGS}"				\
			JH="${JH}"					\
			SHCFLAGS="${SHCFLAGS}"				\
			SHFFLAGS="${SHFFLAGS}"				\
			SHLD="${SHLD}"					\
			SHLDFLAGS="${SHLDFLAGS}"			\
			SHFLD="${SHFLD}"				\
			SHFLDFLAGS="${SHFLDFLAGS}"			\
			SHFLIBS="${SHFLIBS}"				\
			RM="${RM}"					\
			COMPILE_ALL=true				\
		recompile

docs:
	@if [ ! -d $(DOC_DIR) ]; then mkdir $(DOC_DIR); fi   
	$(DOC) $(DOCFLAGS) $(PACKAGES)

tardocs: docs
	(cd $(DOC_DIR)/.. &&						\
	 DNAME=`basename $(DOC_DIR)` &&					\
	 date > $$DNAME/DATE &&						\
	 tar -cvf - $$DNAME) > visad_doc-2.0.tar;			\
	compress visad_doc-2.0.tar;					\
	rm -f visaddoc.taz;						\
	ln visad_doc-2.0.tar.Z visaddoc.taz;				\
	ls -l visad_doc-2.0.tar.Z visaddoc.taz

jardocs: docs
	@(cd $(DOC_DIR)/.. &&						\
	 DNAME=`basename $(DOC_DIR)` &&					\
	 date > $$DNAME/DATE &&						\
	 ${JAR} ${JARVERBOSE}c $$DNAME) > visad_doc-2.0.jar;		\
	rm -f visaddoc.jar;						\
	ln visad_doc-2.0.jar visaddoc.jar;				\
	ls -l visad_doc-2.0.jar visaddoc.jar

nexusrmi_compile:
	make RMIC=nexusrmic compile

tonexus:
	java2nexus `find . -name "*.java"`
	java2nexus `find ../ucar -name "*.java"`
	java2nexus `find ../dods -name "*.java"`
	java2nexus `find ../gnu -name "*.java"`
	java2nexus `find ../nom -name "*.java"`
	java2nexus `find ../edu -name "*.java"`

tojava:
	nexus2java `find . -name "*.java"`
	nexus2java `find ../ucar -name "*.java"`
	nexus2java `find ../dods -name "*.java"`
	nexus2java `find ../gnu -name "*.java"`
	nexus2java `find ../nom -name "*.java"`
	nexus2java `find ../edu -name "*.java"`

jikes_compile:
	make JC=jikes JFLAGS= compile

java_version:
	java -fullversion
