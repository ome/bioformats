include(CMakeFindDependencyMacro)
find_dependency(OME REQUIRED COMPONENTS Common XML BioFormats)
find_dependency(Boost 1.46 REQUIRED COMPONENTS boost iostreams filesystem)
find_dependency(TIFF REQUIRED)

include(${CMAKE_CURRENT_LIST_DIR}/OMEQtWidgetsInternal.cmake)

add_library(OME::QtWidgets INTERFACE IMPORTED)
set_target_properties(OME::QtWidgets PROPERTIES INTERFACE_LINK_LIBRARIES ome-bioformats)
