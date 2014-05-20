# Contributing to Bio-Formats

Guidance for contributing to OME projects in general can be found at
http://www.openmicroscopy.org/site/support/contributing/

Developer documentation specifically for Bio-Formats is at
http://www.openmicroscopy.org/site/support/bio-formats/developers/

The README file gives instructions for testing your code before opening a PR,
please ensure you read these.

## The Quick Version

* Fork the GitHub repository.
* Create a branch for your work based on the latest `dev_x` e.g. dev_5_0 or
  `develop` branch.
* Make your commits, test your changes locally as per the README, and open a
  PR.
* Make sure you include details of the problem you are fixing and how to test
  your changes.
* We may need you to submit some test data
  [via our QA system](http://qa.openmicroscopy.org.uk/qa/upload/). If the 
  files are particularly large (> ~2 GB), contact the
  [mailing list](http://www.openmicroscopy.org/site/community/mailing-lists)
  and we will get back to you with secure upload details.

We review all PRs as soon as we are able. Someone will comment whether
the change is fine as-is or request you make changes before we merge it.

## Contributing to Bio-Formats Documentation

The documentation hosted at
http://www.openmicroscopy.org/site/support/bio-formats/ is built from the
`/docs/sphinx/` directory. Contributions are welcome but please follow the
style guidance from the
[OME Documentation Repository README](https://github.com/openmicroscopy/ome-documentation/blob/develop/README.rst#conventions-used).

Documentation for new supported formats is auto-generated so it is best to
contact the [mailing list](http://www.openmicroscopy.org/site/community/mailing-lists)
before embarking on such a change, or submit your new reader code and let one
of the main OME team deal with the documentation for you.

