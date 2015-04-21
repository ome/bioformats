/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2014 - 2015 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

#ifndef SHOWINF_IMAGEINFO_H
#define SHOWINF_IMAGEINFO_H

#include <string>
#include <stdexcept>

#include <boost/program_options.hpp>

#include <ome/common/log.h>

#include <ome/compat/memory.h>

#include <ome/bioformats/FormatReader.h>
#include <ome/bioformats/Types.h>

#include <info/options.h>

namespace info
{

  /**
   * Metadata from format reader.
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
    setReader(ome::compat::shared_ptr<ome::bioformats::FormatReader>& reader);

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
     * Read and display used files.
     */
    void
    readUsedFiles(std::ostream& stream);

    /**
     * Read and display core metadata.
     */
    void
    readCoreMetadata(std::ostream& stream);

    /**
     * Read and display original metadata.
     */
    void
    readOriginalMetadata(std::ostream& stream);

    /**
     * Read and display OME-XML metadata.
     */
    void
    readOMEXMLMetadata(std::ostream& stream);

    /**
     * Print a single dimension.
     */
    void
    printDimension(std::ostream&                        stream,
                   const std::string&                   dim,
                   ome::bioformats::dimension_size_type size,
                   ome::bioformats::dimension_size_type effectiveSize,
                   const ome::bioformats::Modulo&       modulo);

    /// Message logger.
    ome::common::Logger logger;
    /// File to open with FormatReader::setId.
    std::string file;
    /// Command-line options.
    options opts;
    /// FormatReader instance.
    ome::compat::shared_ptr<ome::bioformats::FormatReader> reader;
  };

}

#endif /* SHOWINF_IMAGEINFO_H */

/*
 * Local Variables:
 * mode:C++
 * End:
 */

