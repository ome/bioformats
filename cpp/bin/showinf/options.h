#ifndef BIN_SHOWINF_OPTIONS_H
#define BIN_SHOWINF_OPTIONS_H

#include <string>
#include <stdexcept>

#include <boost/program_options.hpp>

#include <ome/compat/memory.h>

#include <ome/bioformats/Types.h>

namespace bin
{
  namespace showinf
  {

/*      -nocore: do not output core metadata */
/*      -nometa: do not parse format-specific metadata table */
/*    -nofilter: do not filter metadata fields */
/*     -nogroup: force multi-file datasets to be read as individual files */
/*      -stitch: stitch files with similar names */
/*      -omexml: populate OME-XML metadata */
/*      -noflat: do not flatten subresolutions */
/*        -swap: override the default input dimension order */
/*     -shuffle: override the default output dimension order */
/*         -map: specify file on disk to which name should be mapped */
/*     -novalid: do not perform validation of OME-XML */
/* -omexml-only: only output the generated OME-XML */
/*      -no-sas: do not output OME-XML StructuredAnnotation elements */

    /**
     * showinf command-line options.
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
      bool showformat;
      bool filter;
      bool showomexml;
      bool validate;
      bool showsa;
      bool showused;
      bool group;
      bool stitch;
      bool flat;
      ome::bioformats::dimension_size_type series;
      ome::bioformats::dimension_size_type resolution;

      std::string format;
      std::vector<std::string> files;
      std::string swapin;
      std::string swapout;

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
}

#endif /* BIN_SHOWINF_OPTIONS_H */

/*
 * Local Variables:
 * mode:C++
 * End:
 */

