
#ifndef JACE_OS_DEP_H
#define JACE_OS_DEP_H

// If we're not on Windows, assume a generic unix
#ifdef _WIN32
  #define PATH_SEPARATOR ";"
#else
  #define JACE_GENERIC_UNIX
  #define PATH_SEPARATOR ":"
#endif

#ifdef _WIN32
	/**
	 * Macros used for importing and exporting DLL symbols.
	 *
	 */
	#ifdef JACE_EXPORTS
		#define JACE_API __declspec(dllexport)
	#else
		#define JACE_API __declspec(dllimport)
	#endif

	/**
	 * Macros used for importing and exporting proxy DLL symbols.
	 *
	 */
	#ifdef JACE_PROXY_EXPORTS
		#define JACE_PROXY_API __declspec(dllexport)
	#else
		#define JACE_PROXY_API __declspec(dllimport)
	#endif
#endif

/**
 * Deal with Visual C++'isms.
 *
 * It's possible to run Comeau in Microsoft compatibility mode.
 * In that case, we should deal with any Comeau issues separately.
 *
 */
#if ( defined _MSC_VER && ! defined __COMO__  )

  /**
   * Shut up about debug identifier truncation since we really
   * can't do anything about it anyway.
   *
   */
  #pragma warning( disable : 4786 )

  /**
   * Shut up about 'performance warnings' related to boolean
   * conversions.
   *
   */
  #pragma warning( disable : 4800 )

  /**
   * Turn off warnings about inheritance of methods via dominance
   * in virtual inheritance.
	 *
   */
  #pragma warning( disable : 4250 )

  /**
   * Turn off warnings about unexpected storage-class
   * or type specifiers.
	 *
   */
  #pragma warning( once : 4518 )

  /**
   * Turn off warnings about "conditional expression is constant" such as "while(true)".
   *
   */
  #pragma warning( disable : 4127 )

  /**
   * Turn off warning to avoid a compiler bug triggered by the use of multiple inheritance.
   * @see http://groups.google.com/group/microsoft.public.vsnet.general/browse_thread/thread/1ec41da52896031d
   *
   */
  #pragma warning( disable : 4673 )

	/**
	 * Disable warning about non-safe C/C++ functions.
	 *
	 * @see http://www.stonesteps.ca/services/consulting/faq.asp?qid=q20060128-01&topic=consulting
	 *
	 */
	#ifndef _SECURE_SCL
		#define _SECURE_SCL 0
	#endif
	#ifndef _CRT_SECURE_NO_DEPRECATE
		#define _CRT_SECURE_NO_DEPRECATE
	#endif

	/**
   * Visual C++ 6.0 and earlier requires template specialization definitions
   * to appear in the header.
   *
   */
  #define PUT_TSDS_IN_HEADER

  /**
   * Virtual inheritance doesn't work like it should. When trying to initialize the
   * virtual base class in an initializer list, the compiler complains that
   * "the object has already been initialized". Pretty much a non-sensical error.
   *
   * Only Visual C++ 6.0 seems to be having a problem here.
	 *
   */
  #if _MSC_VER <= 1200
    #define VIRTUAL_INHERITANCE_IS_BROKEN
  #endif

  /**
   * Visual C++ 6.0 has a problem where you must create using declarations
   * for each parameter type of a template class.
   *
   */
  #if _MSC_VER <= 1200
    #define JACE_BROKEN_USING_DECLARATION
  #endif

  /**
   * VC++ 7.0 doesn't support exception specifications correctly, so it generates hundreds
   * of warnings.
   *
   */
  #if _MSC_VER >= 1300
    #pragma warning( disable : 4290 )
  #endif

	/**
	 * VC++ 9.0 supports the new streaming library; though, I'm not sure which version of VC++
	 * it was introduced at.
	 */
	#if _MSC_VER >= 1500
		#define SUPPORTS_SSTREAM
	#endif

/**
 * Deal with g++'isms. Jace has only been tested with g++3.0+, but we'll just treat
 * all versions the same way for now.
 *
 */
#elif defined __GNUG__

	/**
	 * All symbols that aren't local or static are exported by default.
	 *
	 */
	#define JACE_API
  /**
	 * This is a win32-specific hack.
	 *
	 */
	#define JACE_TEMPLATE_EXTERN

  /**
   * Requires template specialization definitions to appear in the header.
   *
   */
  #define PUT_TSDS_IN_HEADER

  /**
   * Supports the new-style sstreams.
   *
   */
  #define SUPPORTS_SSTREAM

  #define NO_IMPLICIT_TYPENAME

#else // We assume a generic compiler on a generic Unix box.

	/**
	 * All symbols that aren't local or static are exported by default.
	 *
	 */
	#define JACE_API
  /**
	 * This is a win32-specific hack.
	 *
	 */
	#define JACE_TEMPLATE_EXTERN
	#define SUPPORTS_SSTREAM

#endif

#endif // JACE_OS_DEP_H

