# Delegate all make targets directly to ant

ifdef BF_RELEASE
MVN_BF_RELEASE := -Drelease.version="$(BF_RELEASE)"
endif

ifdef RELEASE
MVN_RELEASE := -Dsphinx.release="$(RELEASE)"
endif

ifdef VERSION
MVN_VERSION := -Dsphinx.version="$(VERSION)"
endif

ifdef SOURCE_BRANCH
MVN_SOURCE_BRANCH := -Dsphinx.source.branch="$(SOURCE_BRANCH)"
endif

ifdef SOURCE_USER
MVN_SOURCE_USER := -Dsphinx.source.user="$(SOURCE_USER)"
endif

ifdef OMERODOC_URI_JOB
MVN_OMERODOC_URI_JOB := -Dsphinx.omerodoc.uri="$(OMERODOC_URI_JOB)"
endif

ifdef SPHINX_THEME
MVN_SPHINX_THEME := -Dsphinx.html.theme="$(SPHINX_THEME)"
endif

default: html

%:
	mvn -Dsphinx.builder=$@ $(MVN_BF_RELEASE) $(MVN_RELEASE) $(MVN_VERSION) $(MVN_SOURCE_BRANCH) $(MVN_SOURCE_USER) $(MVN_OMERODOC_URI_JOB) $(MVN_SPHINX_THEME)

clean:
	mvn clean

.PHONY: default clean
