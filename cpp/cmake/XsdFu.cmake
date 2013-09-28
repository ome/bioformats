set(XSD_FU_SCRIPT ${PROJECT_SOURCE_DIR}/../components/xsd-fu/xsd-fu)
set(XSD_FU python ${XSD_FU_SCRIPT})
set(MODEL_VERSION 2013-06)
set(MODEL_PATH ${PROJECT_SOURCE_DIR}/../components/specification/released-schema/${MODEL_VERSION})
set(MODEL_FILES
  ${MODEL_PATH}/ome.xsd
  ${MODEL_PATH}/BinaryFile.xsd
  ${MODEL_PATH}/ROI.xsd
  ${MODEL_PATH}/SA.xsd
  ${MODEL_PATH}/SPW.xsd)
set(GEN_DIR ${PROJECT_BINARY_DIR}/lib)
set(XSD_FU_ARGS -p ome.xml.model -l C++ -o ${GEN_DIR} ${MODEL_FILES})
