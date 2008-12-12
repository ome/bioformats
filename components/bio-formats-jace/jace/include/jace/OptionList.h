
#ifndef JACE_OPTION_LIST_H
#define JACE_OPTION_LIST_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif

#ifndef JACE_COUNTED_PTR_H
#include "jace/counted_ptr.h"
#endif

#include <jni.h>

#include <vector>
#include <string>

BEGIN_NAMESPACE( jace )

/**
 * A Jvm option. These are supplied to jace::helper::createVm()
 * in an OptionList.
 *
 * @author Toby Reyelts
 *
 */
class Option {

  public:

  JACE_API virtual ~Option() {}

  /**
   * Returns the text string value of the option.
   * For example, "-Djava.class.path=c:\foo\myclasses"
   *
   */
  JACE_API virtual std::string stringValue() = 0;

  /**
   * Returns the extra data required for an option,
   * for example, a pointer to a vfprintf hook.
   * Returns NULL if no extra data is required.
   *
   */
  JACE_API virtual void* extraInfo() = 0;

  /**
   * Returns a dynamically allocated copy of the Option.
   *
   */
  JACE_API virtual Option* clone() const = 0;
};

/**
 * A List of Options. An OptionList is supplied to jace::helper::createVm()
 * to specify the set of virtual machine options to be used in creating
 * a virtual machine.
 *
 */
class OptionList {

  public:

  typedef ::jace::counted_ptr<Option> OptionPtr;

  /**
   * An OptionList iterator.
   *
   */
  class iterator {  

    public:

    JACE_API iterator( std::vector<OptionPtr>::iterator it_ );
    JACE_API iterator operator++();
    JACE_API iterator operator++( int i );
    JACE_API iterator operator--();
    JACE_API iterator operator--( int i );
    JACE_API OptionPtr operator*();
    JACE_API bool operator!=( const iterator& it_ );


    private:
    std::vector<OptionPtr>::iterator it;
  };

  /**
   * Creates a new empty OptionList.
   *
   */
  JACE_API OptionList();

  /**
   * Adds a clone of option to the list.
   *
   */
  JACE_API void push_back( const Option& option );

  /**
   * Returns the size of the list.
   *
   */
  JACE_API size_t size() const;

  /**
   * Returns an iterator to the beginning of the list.
   *
   */
  JACE_API iterator begin() const;
 
  /**
   * Returns an iterator to the beginning of the list.
   *
   */
  JACE_API iterator end() const;

  /**
   * Returns the OptionList as an array of JNI options.
   *
   * The caller must deallocate the options by making a call
   * to destroyJniOptions().
   *
   */
  JACE_API JavaVMOption* createJniOptions() const;

  /**
   * Releases the options created by a call to createJniOptions().
   *
   */
  JACE_API void destroyJniOptions( JavaVMOption* jniOptions ) const;


  private:
  mutable std::vector<OptionPtr> options;
};

/**
 * A specific type of Option, the SystemProperty tells the
 * virtual machine to set the named system property to the
 * specified value.
 *
 */
class SystemProperty : public Option {

  public:

  /**
   * Creates a new SystemProperty with the specified name and value.
   *
   */
  JACE_API SystemProperty( const std::string& name_, const std::string& value_ );
  
  /**
   * Returns the name of the SystemProperty.
   *
   */
  JACE_API virtual std::string name();
  
  /**
   * Returns the value of the SystemProperty.
   *
   */
  JACE_API virtual std::string value();

  JACE_API virtual std::string stringValue();
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

  private:

  std::string mName;
  std::string mValue;
};


  //-------------------------------------------------------------------------
  // Predefined SystemProperty instances
  //-------------------------------------------------------------------------

  class Version : public SystemProperty {
    public: JACE_API Version( const std::string& value ) : SystemProperty( "java.version", value ) {}
  };

  class Vendor : public SystemProperty {
    public: JACE_API Vendor( std::string value ) : SystemProperty( "java.vendor", value ) {}
  };

  class VendorUrl : public SystemProperty {
    public: JACE_API VendorUrl( const std::string& value ) : SystemProperty( "java.vendor.url", value ) {}
  };

  class Home : public SystemProperty {
    public: JACE_API Home( const std::string& value ) : SystemProperty( "java.home", value ) {}
  };

  class VmSpecificationVersion : public SystemProperty {
    public: JACE_API VmSpecificationVersion( const std::string& value ) : SystemProperty( "java.vm.specification.version", value ) {}
  };

  class VmSpecificationVendor : public SystemProperty {
    public: JACE_API VmSpecificationVendor( const std::string& value ) : SystemProperty( "java.vm.specification.vendor", value ) {}
  };

  class VmSpecificationName : public SystemProperty {
    public: JACE_API VmSpecificationName( const std::string& value ) : SystemProperty( "java.vm.specification.name", value ) {}
  };

  class VmVersion : public SystemProperty {
    public: JACE_API VmVersion( const std::string& value ) : SystemProperty( "java.vm.version", value ) {}
  };

  class VmVendor : public SystemProperty {
    public: JACE_API VmVendor( const std::string& value ) : SystemProperty( "java.vm.vendor", value ) {}
  };

  class VmName : public SystemProperty {
    public: JACE_API VmName( const std::string& value ) : SystemProperty( "java.vm.name", value ) {}
  };

  class SpecificationVersion : public SystemProperty {
    public: JACE_API SpecificationVersion( const std::string& value ) : SystemProperty( "java.specification.version", value ) {}
  };

  class SpecificationVendor : public SystemProperty {
    public: JACE_API SpecificationVendor( const std::string& value ) : SystemProperty( "java.specification.vendor", value ) {}
  };

  class SpecificationName : public SystemProperty {
    public: JACE_API SpecificationName( const std::string& value ) : SystemProperty( "java.specification.name", value ) {}
  };

  class ClassVersion : public SystemProperty {
    public: JACE_API ClassVersion( const std::string& value ) : SystemProperty( "java.class.version", value ) {}
  };

  class ClassPath : public SystemProperty {
    public: JACE_API ClassPath( const std::string& value ) : SystemProperty( "java.class.path", value ) {}
  };

  class LibraryPath : public SystemProperty {
    public: JACE_API LibraryPath( const std::string& value ) : SystemProperty( "java.library.path", value ) {}
  };

  class IoTmpDir : public SystemProperty {
    public: JACE_API IoTmpDir( const std::string& value ) : SystemProperty( "java.io.tmpdir", value ) {}
  };

  class Compiler : public SystemProperty {
    public: JACE_API Compiler( const std::string& value ) : SystemProperty( "java.compiler", value ) {}
  };

  class ExtDirs : public SystemProperty {
    public: JACE_API ExtDirs( const std::string& value ) : SystemProperty( "java.ext.dirs", value ) {}
  };

  class OsName : public SystemProperty {
    public: JACE_API OsName( const std::string& value ) : SystemProperty( "java.os.name", value ) {}
  };

  class OsArch : public SystemProperty {
    public: JACE_API OsArch( const std::string& value ) : SystemProperty( "java.os.arch", value ) {}
  };

  class OsVersion : public SystemProperty {
    public: JACE_API OsVersion( const std::string& value ) : SystemProperty( "java.os.version", value ) {}
  };

  class FileSeparator : public SystemProperty {
    public: JACE_API FileSeparator( const std::string& value ) : SystemProperty( "file.separator", value ) {}
  };

  class PathSeparator : public SystemProperty {
    public: JACE_API PathSeparator( const std::string& value ) : SystemProperty( "path.separator", value ) {}
  };

  class LineSeparator : public SystemProperty {
    public: JACE_API LineSeparator( const std::string& value ) : SystemProperty( "line.separator", value ) {}
  };

  class UserName : public SystemProperty {
    public: JACE_API UserName( const std::string& value ) : SystemProperty( "user.name", value ) {}
  };

  class UserHome : public SystemProperty {
    public: JACE_API UserHome( const std::string& value ) : SystemProperty( "user.home", value ) {}
  };

  class UserDir : public SystemProperty {
    public: JACE_API UserDir( const std::string& value ) : SystemProperty( "user.dir", value ) {}
  };


/**
 * A specific type of Option that tells the virtual machine to produce
 * verbose output of a specified type.
 *
 * Nonstandard verbose types must begin with a leading "X" character.
 *
 */
class Verbose : public Option {

  public:

  /**
   * Output for garbage collection
   */
  JACE_API static std::string Gc;

  /**
   * Output for JNI
   */
  JACE_API static std::string Jni;

  /**
   * Output for class loading
   */
  JACE_API static std::string Class;
  
  JACE_API Verbose( const std::string& option );

  JACE_API Verbose( const std::string& o1, const std::string& o2 );

  JACE_API Verbose( const std::string& o1, const std::string& o2, const std::string& o3 );

  JACE_API Verbose( std::vector<std::string>::iterator begin, std::vector<std::string>::iterator end );

  JACE_API std::string stringValue();
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

  private:
 
  mutable std::vector<std::string> options;
};


/**
 * A virtual machine specific, custom option.
 *
 * You may specify any virtual machine specific option, for example,
 * "-Xmx=128M". All custom options must begin with a leading 
 * "-X" or "_" string.
 *
 */
class CustomOption : public Option {

  public:

  JACE_API CustomOption( const std::string& value_ );

  JACE_API virtual std::string stringValue();
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

  private: 
  std::string value;
};


/**
 * A virtual machine option that lets you hook into a specific function.
 *
 */
class Hook : public Option {
};


/**
 * Lets you hook into the vfprintf() function.
 *
 */
class VfprintfHook : public Hook {

  public:

  typedef int ( *vfprintf_t )( FILE* fp, const char* format, va_list arg );

  JACE_API VfprintfHook( vfprintf_t hook_ );

  JACE_API virtual std::string stringValue();
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

  private:
  vfprintf_t hook;
};

/**
 * Lets you hook into the exit() function.
 *
 */
class ExitHook : public Hook {

  public:

  typedef void ( *exit_t )( int status ); 

  JACE_API ExitHook( exit_t hook_ );

  JACE_API virtual std::string stringValue();
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

  private:

  exit_t hook;
};


/**
 * Lets you hook into the abort() function.
 *
 */
class AbortHook : public Hook {

  public:

  typedef void ( *abort_t ) ( void ); 

  JACE_API AbortHook( abort_t hook_ );

  JACE_API virtual std::string stringValue();
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

  private:

  abort_t hook;
};

END_NAMESPACE( jace )

#endif // JACE_OPTION_LIST_H

