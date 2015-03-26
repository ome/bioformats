# Delegate all make targets directly to ant

ifdef SPHINXOPTS
ANT_SPHINXOPTS := -Dsphinx.opts="$(SPHINXOPTS)"
endif

ifdef BF_RELEASE
ANT_BF_RELEASE := -Dbf.release="$(BF_RELEASE)"
endif

ifdef SOURCE_BRANCH
ANT_SOURCE_BRANCH := -Dsource.branch="$(SOURCE_BRANCH)"
endif

ifdef SOURCE_USER
ANT_SOURCE_USER := -Dsource.user="$(SOURCE_USER)"
endif

ifdef OMERODOC_URI_JOB
ANT_OMERODOC_URI_JOB := -Domerodoc.uri="$(OMERODOC_URI_JOB)"
endif

default: html

%:
	ant $@ $(ANT_SPHINXOPTS) $(ANT_BF_RELEASE) $(ANT_SOURCE_BRANCH) $(ANT_SOURCE_USER) $(ANT_JENKINS_JOB) $(ANT_JENKINS_CPP_JOB) $(ANT_OMERODOC_URI_JOB)

.PHONY: default
