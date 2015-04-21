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

#ifndef SHOWINF_OPTIONS_H
#define SHOWINF_OPTIONS_H

#include <string>
#include <stdexcept>

#include <boost/program_options.hpp>

#include <ome/compat/memory.h>

#include <ome/bioformats/Types.h>

#include <ome/xml/model/enums/DimensionOrder.h>

namespace info
{

  /**
   * Command-line options.
   */
  class options
  {
  public:
    /// The constructor.
    options ();

    /// The destructor.
    virtual ~options ();

    /**
     * Parse the command-line options.
     *
     * @param argc the number of arguments
     * @param argv argument vector
     */
    void
    parse (int   argc,
           char *argv[]);

    enum userAction
      {
        ACTION_USAGE,
        ACTION_HELP,
        ACTION_VERSION,
        ACTION_METADATA
      };

    enum messageVerbosity
      {
        MSG_QUIET,
        MSG_NORMAL,
        MSG_VERBOSE,
        MSG_DEBUG
      };

    /// Action list.
    userAction action;

    /// Message verbosity.
    messageVerbosity verbosity;

    bool showcore;
    bool showorig;
    bool filter;
    bool showomexml;
    bool validate;
    bool showsa;
    bool showused;
    bool merge;
    bool group;
    bool stitch;
    bool separate;
    bool flat;
    ome::bioformats::dimension_size_type series;
    ome::bioformats::dimension_size_type resolution;

    std::string format;
    std::vector<std::string> files;
    std::string inputOrderString;
    std::string outputOrderString;
    boost::optional<ome::xml::model::enums::DimensionOrder> inputOrder;
    boost::optional<ome::xml::model::enums::DimensionOrder> outputOrder;

    /**
     * Get the visible options group.  This options group contains
     * all the options visible to the user.
     *
     * @returns the options_description.
     */
    boost::program_options::options_description const&
    get_visible_options() const;

  protected:
    /**
     * Add options to option groups.
     */
    virtual void
    add_options ();

    /**
     * Add option groups to container groups.
     */
    virtual void
    add_option_groups ();

    /**
     * Check options after parsing.
     */
    virtual void
    check_options ();

    /**
     * Check actions after parsing.
     */
    virtual void
    check_actions ();

    /// Actions options group.
    boost::program_options::options_description            actions;
    /// General options group.
    boost::program_options::options_description            general;
    /// Reader options group.
    boost::program_options::options_description            reader;
    /// Metadata options group.
    boost::program_options::options_description            metadata;
    /// Hidden options group.
    boost::program_options::options_description            hidden;
    /// Positional options group.
    boost::program_options::positional_options_description positional;
    /// Visible options container (used for --help).
    boost::program_options::options_description            visible;
    /// Global options container (used for parsing).
    boost::program_options::options_description            global;
    /// Variables map, filled during parsing.
    boost::program_options::variables_map                  vm;
  };

}

#endif /* SHOWINF_OPTIONS_H */

/*
 * Local Variables:
 * mode:C++
 * End:
 */

