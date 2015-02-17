/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2014 Open Microscopy Environment:
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

#include <showinf/options.h>

namespace opt = boost::program_options;

namespace showinf
{

  options::options ():
    action(ACTION_METADATA),
    verbosity(MSG_NORMAL),
    showcore(true),
    showformat(false),
    filter(false),
    showomexml(false),
    validate(false),
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
    swapin(),
    swapout(),
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
      ("help,h",
       "Show help options")
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
      ("flat", opt::value<bool>(&this->flat),
       "Flatten subresolutions")
      ("merge", opt::value<bool>(&this->merge),
       "Combine separate channels into an RGB image")
      ("group", opt::value<bool>(&this->group),
       "Files in multi-file datasets are treated as a single dataset")
      ("stitch", opt::value<bool>(&this->stitch),
       "Group files with similar names")
      ("separate", opt::value<bool>(&this->separate),
       "Separate RGB image into separate channels")
      ("series", opt::value<ome::bioformats::dimension_size_type>(&this->series),
       "Use the specified series")
      ("resolution", opt::value<ome::bioformats::dimension_size_type>(&this->resolution),
       "Use the specified subresolution (only if unflattened)")
      ("swapin", opt::value<std::string>(&this->swapin),
       "Swap the dimension input order")
      ("swapout", opt::value<std::string>(&this->swapout),
       "Swap the dimension output order");

    metadata.add_options()
      ("core", opt::value<bool>(&this->showcore),
       "Display core metadata")
      ("format", opt::value<bool>(&this->showformat),
       "Display format-specific global and series metadata")
      ("filter", opt::value<bool>(&this->filter),
       "Filter format-specific global and series metadata")
      ("omexml", opt::value<bool>(&this->showomexml),
       "Display OME-XML metadata")
      ("validate", opt::value<bool>(&this->validate),
       "Validate OME-XML metadata")
      ("sa", opt::value<bool>(&this->showsa),
       "Display structured annotations")
      ("used", opt::value<bool>(&this->showused),
       "Display used files");

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
  }

  void
  options::check_actions ()
  {
  }

}
