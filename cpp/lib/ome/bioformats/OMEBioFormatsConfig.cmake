include(CMakeFindDependencyMacro)
find_dependency(OME REQUIRED COMPONENTS Common XML)
find_dependency(Boost 1.46 REQUIRED COMPONENTS boost iostreams filesystem)
find_dependency(TIFF REQUIRED)

include(${CMAKE_CURRENT_LIST_DIR}/OMEBioFormatsInternal.cmake)

add_library(OME::BioFormats INTERFACE IMPORTED)
set_target_properties(OME::BioFormats PROPERTIES INTERFACE_LINK_LIBRARIES ome-bioformats)
