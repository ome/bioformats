REM This script is used for testing the build, primarily for use
REM with appveyor, but may be used by hand as well.

set build=%1

if [%build%] == [] exit /b 2

if [%build%] == [maven] (
  REM Test the maven build
  mvn install || exit /b 1
)

if [%build%] == [ant] (
  REM Test the ant build
  ant clean compile || exit /b 1
  ant clean compile-autogen || exit /b 1
  ant clean compile-formats-api || exit /b 1
  ant clean compile-bio-formats-plugins || exit /b 1
  ant clean compile-formats-bsd || exit /b 1
  ant clean compile-formats-gpl || exit /b 1
  ant clean compile-bio-formats-tools || exit /b 1
  ant clean compile-tests || exit /b 1
  ant clean compile-turbojpeg || exit /b 1
  ant clean utils || exit /b 1
  ant -Dsphinx.warnopts="-W" clean-docs-sphinx docs-sphinx || exit /b 1
  REM don't clean, so that the docs zip will be archived along with
  REM the other zips and jars.
  ant tools dist-bftools dist-matlab dist-octave || exit /b 1
  REM Finally, run the unit tests.
  ant test || exit /b 1
)
