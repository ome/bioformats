  The loci-legacy component preserves classic bio-formats
  package structures as classes and components are moved to SCIFIO. For
  example, if loci.formats.Sample.java is migrated from the bio-formats
  component to ome.scifio.Sample.java in the scifio component, then
  a loci.formats.Sample.java would also be added to loci-legacy. This
  class would be an empty delegator and would pass all control to the
  ome.scifio class. (thus loci-legacy depends only the scifio component)

  All delegators should use a "has a" pattern when possible.

  By including this component, all backwards compatibility with downstream
  code using legacy packages will be preserved, yet functionality can
  continue to be improved via delegating to the latest SCIFIO code.
