#ifndef BIN_SHOWINF_IMAGEINFO_H
#define BIN_SHOWINF_IMAGEINFO_H

#include <string>
#include <stdexcept>

#include <boost/program_options.hpp>

#include <ome/compat/memory.h>

#include <ome/bioformats/FormatReader.h>
#include <ome/bioformats/Types.h>

#include "options.h"

namespace bin
{
  namespace showinf
  {

    /**
     * showinf format reader information.
     */
    class ImageInfo
    {
    public:
      /// The constructor.
      ImageInfo (const std::string &file,
                 const options&     opts);

      /// The destructor.
      virtual ~ImageInfo ();

      void
      setReader(std::shared_ptr<ome::bioformats::FormatReader>& reader);

      void
      testRead(std::ostream& stream);

    private:
      /**
       * Set up MetadataStore before setId.
       */
      void
      preInit(std::ostream& stream);

      /**
       * Set up DimensionSwapper after setId.
       *
       * @note Dimension swapping is not yet implemented.
       * @todo Implement dimension swapping.
       */
      void
      postInit(std::ostream& stream);

      /**
       * Check reader warnings.
       */
      void
      checkWarnings(std::ostream& stream);

      /**
       * Read and display core metadata.
       */
      void
      readCoreMetadata(std::ostream& stream);

      /**
       * Print a single dimension.
       */
      void
      printDimension(std::ostream&                        stream,
                     const std::string&                   dim,
                     ome::bioformats::dimension_size_type size,
                     ome::bioformats::dimension_size_type effectiveSize,
                     const ome::bioformats::Modulo&       modulo);

      /// File to open with FormatReader::setId.
      std::string file;
      /// Command-line options.
      options opts;
      /// FormatReader instance.
      std::shared_ptr<ome::bioformats::FormatReader> reader;
    };

  }
}

#endif /* BIN_SHOWINF_IMAGEINFO_H */

/*
 * Local Variables:
 * mode:C++
 * End:
 */

