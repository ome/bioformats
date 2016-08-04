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

#include <info/options.h>

namespace opt = boost::program_options;

namespace info
{

  options::options ():
    action(ACTION_METADATA),
    verbosity(MSG_NORMAL),
    showcore(true),
    showorig(true),
    filter(false),
    showomexml(false),
    validate(true),
    showsa(true),
    showused(true),
    merge(false),
    group(false),
    stitch(false),
    separate(false),
    flat(false),
    series(0),
    resolution(0),
    format(),
    files(),
    inputOrderString(),
    outputOrderString(),
    inputOrder(),
    outputOrder(),
    actions("Actions"),
    general("General options"),
    reader("Reader options"),
    metadata("Metadata filtering and display options"),
    hidden("Hidden options"),
    positional(),
    visible(),
    global(),
    vm()
  {
  }

  options::~options ()
  {
  }

  boost::program_options::options_description const&
  options::get_visible_options() const
  {
    return this->visible;
  }

  void
  options::parse (int   argc,
                  char *argv[])
  {
    add_options();
    add_option_groups();

    opt::store(opt::command_line_parser(argc, argv).
               options(global).positional(positional).run(), vm);
    opt::notify(vm);

    check_options();
    check_actions();
  }

  void
  options::add_options ()
  {
    actions.add_options()
      ("usage,u",
       "Show command usage")
      ("help,h",
       "Display manual for this command")
      ("metadata",
       "Display image metadata (default)")
      ("version,V",
       "Print version information");

    general.add_options()
      ("debug",
       "Show debug output")
      ("quiet,q",
       "Show less output")
      ("verbose,v",
       "Show more output");

    reader.add_options()
      ("format", opt::value<std::string>(&this->format),
       "Use the specified format reader")
      ("flat", "Flatten subresolutions")
      ("no-flat", "Do not flatten subresolutions (default)")
      ("merge", "Combine separate channels into an RGB image")
      ("no-merge", "Do not combine separate channels into an RGB image (default)")
      ("group", "Files in multi-file datasets are grouped as a single dataset")
      ("no-group", "Files in multi-file datasets are treated as a multiple datasets (default)")
      ("stitch", "Group files with similar names")
      ("no-stitch", "Do not group files with similar names (default)")
      ("separate", "Separate RGB image into separate channels")
      ("no-separate", "Do not separate RGB image into separate channels (default)")
      ("series", opt::value<ome::bioformats::dimension_size_type>(&this->series),
       "Use the specified series")
      ("resolution", opt::value<ome::bioformats::dimension_size_type>(&this->resolution),
       "Use the specified subresolution (only if unflattened)")
      ("input-order",
       opt::value<std::string>(&this->inputOrderString),
       "Override the dimension input order")
      ("output-order",
       opt::value<std::string>(&this->outputOrderString),
       "Override the dimension output order");

    metadata.add_options()
      ("core", "Display core metadata (default)")
      ("no-core", "Do not display core metadata")
      ("orig", "Display original format-specific global and series metadata (default)")
      ("no-orig", "Do not display original format-specific global and series metadata")
      ("filter", "Filter format-specific global and series metadata")
      ("no-filter", "Do not filter format-specific global and series metadata (default)")
      ("omexml", "Display OME-XML metadata")
      ("no-omexml", "Do not display OME-XML metadata (default)")
      ("validate", "Validate OME-XML metadata (default)")
      ("no-validate", "Do not validate OME-XML metadata")
      ("sa", "Display structured annotations (default)")
      ("no-sa", "Do not display structured annotations")
      ("used", "Display used files (default)")
      ("no-used", "Do not display used files");

    hidden.add_options()
      ("files", opt::value<std::vector<std::string> >(&this->files),
       "Files to read");

    positional.add("files", -1);
  }

  void
  options::add_option_groups ()
  {
#ifndef BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD
    if (!actions.options().empty())
#else
      if (!actions.primary_keys().empty())
#endif
        {
          global.add(actions);
          visible.add(actions);
        }
#ifndef BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD
    if (!general.options().empty())
#else
      if (!general.primary_keys().empty())
#endif
        {
          global.add(general);
          visible.add(general);
        }
#ifndef BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD
    if (!reader.options().empty())
#else
      if (!reader.primary_keys().empty())
#endif
        {
          global.add(reader);
          visible.add(reader);
        }
#ifndef BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD
    if (!metadata.options().empty())
#else
      if (!metadata.primary_keys().empty())
#endif
        {
          global.add(metadata);
          visible.add(metadata);
        }
#ifndef BOOST_PROGRAM_OPTIONS_DESCRIPTION_OLD
    if (!hidden.options().empty())
#else
      if (!hidden.primary_keys().empty())
#endif
        global.add(hidden);
  }

  void
  options::check_options ()
  {
    if (vm.count("usage"))
      this->action = ACTION_USAGE;

    if (vm.count("help"))
      this->action = ACTION_HELP;

    if (vm.count("version"))
      this->action = ACTION_VERSION;

    if (vm.count("quiet"))
      this->verbosity = MSG_QUIET;
    if (vm.count("verbose"))
      this->verbosity = MSG_VERBOSE;
    if (vm.count("debug"))
      this->verbosity = MSG_DEBUG;

    if (vm.count("flat"))
      this->flat = true;
    if (vm.count("no-flat"))
      this->flat = false;

    if (vm.count("merge"))
      this->merge = true;
    if (vm.count("no-merge"))
      this->merge = false;

    if (vm.count("group"))
      this->group = true;
    if (vm.count("no-group"))
      this->group = false;

    if (vm.count("stitch"))
      this->stitch = true;
    if (vm.count("no-stitch"))
      this->stitch = false;

    if (vm.count("separate"))
      this->separate = true;
    if (vm.count("no-separate"))
      this->separate = false;

    if(!this->inputOrderString.empty())
      this->inputOrder = ome::xml::model::enums::DimensionOrder(inputOrderString);
    if(!this->outputOrderString.empty())
      this->outputOrder = ome::xml::model::enums::DimensionOrder(outputOrderString);

    if (vm.count("core"))
      this->showcore = true;
    if (vm.count("no-core"))
      this->showcore = false;

    if (vm.count("orig"))
      this->showorig = true;
    if (vm.count("no-orig"))
      this->showorig = false;

    if (vm.count("filter"))
      this->filter = true;
    if (vm.count("no-filter"))
      this->filter = false;

    if (vm.count("omexml"))
      this->showomexml = true;
    if (vm.count("no-omexml"))
      this->showomexml = false;

    if (vm.count("validate"))
      this->validate = true;
    if (vm.count("no-validate"))
      this->validate = false;

    if (vm.count("sa"))
      this->showsa = true;
    if (vm.count("no-sa"))
      this->showsa = false;

    if (vm.count("used"))
      this->showused = true;
    if (vm.count("no-used"))
      this->showused = false;
  }

  void
  options::check_actions ()
  {
  }

}
