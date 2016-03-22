#! /usr/bin/python
# Script for increasing versions numbers across the code

import glob
import re
import argparse


def check_version_format(version):
    """Check format of version number"""
    pattern = '^[0-9]+[\.][0-9]+[\.][0-9]+(\-.+)*$'
    return re.match(pattern, version) is not None


def replace_file(input_path, pattern, version):
    """Substitute a pattern with version in a file"""
    with open(input_path, "r") as infile:
        regexp = re.compile(pattern)
        new_content = regexp.sub(r"\g<1>%s\g<2>" % version, infile.read())
        with open(input_path, "w") as output:
            output.write(new_content)
            output.close()
        infile.close()

artifact_pattern = r"(<groupId>ome</groupId>\n.*\n.*<version>).*(</version>)"
release_version_pattern = r"(<release.version>).*(</release.version>)"


def bump_pom_versions(version):
    """Replace versions in pom.xml files"""

    # Replace versions in components pom.xml
    for pomfile in (glob.glob("*/*/pom.xml") + glob.glob("*/*/*/pom.xml")):
        replace_file(pomfile, artifact_pattern, version)

    # Replace versions in top-level pom.xml
    toplevelpomfile = "pom.xml"
    replace_file(toplevelpomfile, artifact_pattern, version)
    replace_file(toplevelpomfile, release_version_pattern, version)

stableversion_pattern = r"(STABLE_VERSION = \").*(\";)"
upgradecheck = "components/formats-bsd/src/loci/formats/UpgradeChecker.java"


def bump_stable_version(version):
    """Replace UpgradeChecker stable version"""

    replace_file(upgradecheck, stableversion_pattern, version)


if __name__ == "__main__":
    # Input check
    parser = argparse.ArgumentParser()
    parser.add_argument("version", type=str)
    ns = parser.parse_args()

    if not check_version_format(ns.version):
        print "Invalid version format"
    else:
        bump_pom_versions(ns.version)
        if not ns.version.endswith('SNAPSHOT'):
            bump_stable_version(ns.version)
