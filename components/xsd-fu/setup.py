#!/usr/bin/env python
"""
   setuptools entry point

   This script is used by the ant build (build.xml) to
   package and test the xsd-fu prackage. For most uses,
   see ant.

   Copyright 2011 Glencoe Software, Inc. All rights reserved.
   Use is subject to license terms supplied in LICENSE.txt

"""

import os
ov = os.environ.get("OMERO_VERSION", "unknown")

from ez_setup import use_setuptools
use_setuptools()
from setuptools import setup, find_packages

if os.path.exists("target"):
    packages = find_packages("target")+[""]
else:
    packages = [""]

setup(name="xsd-fu",
      version=ov,
      description="Python tools for generating code from the OME specification",
      long_description="""\
Python tools for generating code from the OME specification",
""",
      author="The Open Microscopy Consortium",
      author_email="ome-users@lists.openmicroscopy.org.uk",
      url="https://trac.openmicroscopy.org.uk/ome/wiki/XsdFu",
      download_url="https://trac.openmicroscopy.org.uk/ome/wiki/XsdFu",
#      package_dir = {"": "target"},
#      packages=packages,
      test_suite='test.suite'
)

