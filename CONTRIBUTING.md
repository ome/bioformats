# Contributing to Bio-Formats

Guidance for contributing to OME projects in general can be found at
https://docs.openmicroscopy.org/contributing/

Developer documentation specifically for Bio-Formats is at
https://docs.openmicroscopy.org/bio-formats/

The README file gives instructions for testing your code before opening a PR,
please ensure you read these.

## The Quick Version

* Fork the GitHub repository.
* Create a branch for your work based on the latest `dev_x` e.g. dev_5_0 or
  `develop` branch. Unless you are targeting a specific release, it is
  best to default to working against `develop`.
* Make your commits, test your changes locally as per the README, and open a
  PR.
* Make sure you include details of the problem you are fixing and how to test
  your changes.
* We may need you to submit some test data
  [via our QA system](http://qa.openmicroscopy.org.uk/qa/upload/). If the
  files are particularly large (> ~2 GB), contact the
  [mailing list](https://www.openmicroscopy.org/support)
  and we will get back to you with secure upload details.

## External Contributors

* PRs submitted from outside OME will get an initial review to identify if
  they are suitable to pass into our continuous integration system for
  building and testing. We try to do this within 2 days of submission but
  please be patient if we are busy and it takes longer.

* If there are any obvious issues, we will comment and wait for you to fix
  them. You can help this process by ensuring that the Travis build is passing
  when you first submit the PR.

* Once we are confident the PR contains no obvious errors, an "include" label
  will be added which means the PR will be included in the merge build jobs
  for the appropriate branch.

* Build failures will then be noted on the PR and we will either submit a
  patch or provide sufficient information for you to fix the problem yourself.
  The "include" label will be removed until this is completed.

* The PR will be merged once all the builds are green with the "include" label
  added.

## Contributing to Bio-Formats Documentation

The documentation hosted at
https://docs.openmicroscopy.org/bio-formats/ is built from the
`/docs/sphinx/` directory. Contributions are welcome but please follow the
style guidance from the
[OME Documentation Repository README](https://github.com/openmicroscopy/ome-documentation/blob/develop/README.rst#conventions-used).

Documentation for new supported formats is auto-generated so it is best to
contact the [mailing list](https://www.openmicroscopy.org/support)
before embarking on such a change, or submit your new reader code and let one
of the main OME team deal with the documentation for you.

