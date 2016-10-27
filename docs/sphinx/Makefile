# Delegate all make targets directly to ant

ifdef SPHINXOPTS
ANT_SPHINXOPTS := -Dsphinx.opts="$(SPHINXOPTS)"
endif

ifdef BF_RELEASE
ANT_BF_RELEASE := -Drelease.version="$(BF_RELEASE)"
endif

ifdef RELEASE
ANT_RELEASE := -Dsphinx.release="$(RELEASE)"
endif

ifdef VERSION
ANT_VERSION := -Dsphinx.version="$(VERSION)"
endif

ifdef SOURCE_BRANCH
ANT_SOURCE_BRANCH := -Dsphinx.source.branch="$(SOURCE_BRANCH)"
endif

ifdef SOURCE_USER
ANT_SOURCE_USER := -Dsphinx.source.user="$(SOURCE_USER)"
endif

ifdef OMERODOC_URI_JOB
ANT_OMERODOC_URI_JOB := -Dsphinx.omerodoc.uri="$(OMERODOC_URI_JOB)"
endif

default: html

%:
	ant $@ $(ANT_SPHINXOPTS) $(ANT_BF_RELEASE) $(ANT_RELEASE) $(ANT_VERSION) $(ANT_SOURCE_BRANCH) $(ANT_SOURCE_USER) $(ANT_JENKINS_JOB) $(ANT_JENKINS_CPP_JOB) $(ANT_OMERODOC_URI_JOB)

.PHONY: default
