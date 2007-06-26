README
6/26/07

These are overlays files which at caused a problem OverlayUtil.getNodedLayer() (OverlayUtil.getSelectionLayer()),
which tries to build a bunch of Gridded2DSets to highlight the overlay.
The filenames indicate what problem the overlay caused: a "bow tie" shaped Gridded2DSet, or 
a set that apparently wasn't bow tie shaped but still didn't pass the Gridded2DSet validation (called an "invalid set" here).
There are also sets of overlays (e.g., polylines_with_parallel_overlapping_segments.txt) designed to test the behavior
of the getNodedLayer() method in special cases.
