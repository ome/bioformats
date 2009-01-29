
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
  JACE_API virtual const std::string stringValue() const = 0;

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
	JACE_API std::vector<OptionPtr>::const_iterator begin() const;
 
  /**
   * Returns an iterator to the beginning of the list.
   *
   */
  JACE_API std::vector<OptionPtr>::const_iterator end() const;

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
	/**
	 * Prevent assignment.
	 */
	OptionList& operator=( const OptionList& other);

  std::vector<OptionPtr> options;
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
	 * Copy constructor.
	 */
	JACE_API SystemProperty( const SystemProperty& other);
  
	/**
   * Returns the name of the SystemProperty.
   *
   */
  JACE_API virtual const std::string name();
  
  /**
   * Returns the value of the SystemProperty.
   *
   */
  JACE_API virtual const std::string value();

  JACE_API virtual const std::string stringValue() const;
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

private:
	/**
	 * Prevent assignment.
	 */
	SystemProperty& operator=( const SystemProperty& other);

  const std::string mName;
  const std::string mValue;
};


  //-------------------------------------------------------------------------
  // Predefined SystemProperty instances
  //-------------------------------------------------------------------------

  class Version : public SystemProperty {
    public: Version( const std::string& value ) : SystemProperty( "java.version", value ) {}
  };

  class Vendor : public SystemProperty {
    public: Vendor( const std::string& value ) : SystemProperty( "java.vendor", value ) {}
  };

  class VendorUrl : public SystemProperty {
    public: VendorUrl( const std::string& value ) : SystemProperty( "java.vendor.url", value ) {}
  };

  class Home : public SystemProperty {
    public: Home( const std::string& value ) : SystemProperty( "java.home", value ) {}
  };

  class VmSpecificationVersion : public SystemProperty {
    public: VmSpecificationVersion( const std::string& value ) : SystemProperty( "java.vm.specification.version", value ) {}
  };

  class VmSpecificationVendor : public SystemProperty {
    public: VmSpecificationVendor( const std::string& value ) : SystemProperty( "java.vm.specification.vendor", value ) {}
  };

  class VmSpecificationName : public SystemProperty {
    public: VmSpecificationName( const std::string& value ) : SystemProperty( "java.vm.specification.name", value ) {}
  };

  class VmVersion : public SystemProperty {
    public: VmVersion( const std::string& value ) : SystemProperty( "java.vm.version", value ) {}
  };

  class VmVendor : public SystemProperty {
    public: VmVendor( const std::string& value ) : SystemProperty( "java.vm.vendor", value ) {}
  };

  class VmName : public SystemProperty {
    public: VmName( const std::string& value ) : SystemProperty( "java.vm.name", value ) {}
  };

  class SpecificationVersion : public SystemProperty {
    public: SpecificationVersion( const std::string& value ) : SystemProperty( "java.specification.version", value ) {}
  };

  class SpecificationVendor : public SystemProperty {
    public: SpecificationVendor( const std::string& value ) : SystemProperty( "java.specification.vendor", value ) {}
  };

  class SpecificationName : public SystemProperty {
    public: SpecificationName( const std::string& value ) : SystemProperty( "java.specification.name", value ) {}
  };

  class ClassVersion : public SystemProperty {
    public: ClassVersion( const std::string& value ) : SystemProperty( "java.class.version", value ) {}
  };

  class ClassPath : public SystemProperty {
    public: ClassPath( const std::string& value ) : SystemProperty( "java.class.path", value ) {}
  };

  class LibraryPath : public SystemProperty {
    public: LibraryPath( const std::string& value ) : SystemProperty( "java.library.path", value ) {}
  };

  class IoTmpDir : public SystemProperty {
    public: IoTmpDir( const std::string& value ) : SystemProperty( "java.io.tmpdir", value ) {}
  };

  class Compiler : public SystemProperty {
    public: Compiler( const std::string& value ) : SystemProperty( "java.compiler", value ) {}
  };

  class ExtDirs : public SystemProperty {
    public: ExtDirs( const std::string& value ) : SystemProperty( "java.ext.dirs", value ) {}
  };

  class OsName : public SystemProperty {
    public: OsName( const std::string& value ) : SystemProperty( "java.os.name", value ) {}
  };

  class OsArch : public SystemProperty {
    public: OsArch( const std::string& value ) : SystemProperty( "java.os.arch", value ) {}
  };

  class OsVersion : public SystemProperty {
    public: OsVersion( const std::string& value ) : SystemProperty( "java.os.version", value ) {}
  };

  class FileSeparator : public SystemProperty {
    public: FileSeparator( const std::string& value ) : SystemProperty( "file.separator", value ) {}
  };

  class PathSeparator : public SystemProperty {
    public: PathSeparator( const std::string& value ) : SystemProperty( "path.separator", value ) {}
  };

  class LineSeparator : public SystemProperty {
    public: LineSeparator( const std::string& value ) : SystemProperty( "line.separator", value ) {}
  };

  class UserName : public SystemProperty {
    public: UserName( const std::string& value ) : SystemProperty( "user.name", value ) {}
  };

  class UserHome : public SystemProperty {
    public: UserHome( const std::string& value ) : SystemProperty( "user.home", value ) {}
  };

  class UserDir : public SystemProperty {
    public: UserDir( const std::string& value ) : SystemProperty( "user.dir", value ) {}
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
	 * The component that should output verbosely.
	 */
	JACE_API enum ComponentType {
		/**
		 * The garbage collector.
		 */
		GC,
		/**
		 * The Java Native Interface.
		 */
		JNI,
		/**
		 * The ClassLoader.
		 */
		CLASS
	};

	/**
	 * Creates a new Verbose option.
	 *
	 * @param component the component type
	 */
  JACE_API Verbose( ComponentType component );

	/**
	 * Creates a new Verbose option.
	 *
	 * @param begin the first component type
	 * @param end the last component type
	 */
  JACE_API Verbose( std::vector<ComponentType>::const_iterator begin, std::vector<ComponentType>::const_iterator end );

	/**
	 * Copy constructor.
	 */
	JACE_API Verbose( const Verbose& other );
  
  JACE_API virtual const std::string stringValue() const;
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

private:
	/**
	 * Prevent assignment.
	 */
	Verbose& operator=( const Verbose& other);
	/**
	 * Converts a ComponentType to a string.
	 *
	 * @param component the component type
	 */
	std::string toString(ComponentType component) const;
	/**
	 * Returns a vector containing a single value.
	 *
	 * @param component the value
	 */
	static std::vector<ComponentType> createVector( ComponentType& component );
	/**
	 * Returns a vector containing a collection of values
	 *
	 * @param begin the first element
	 * @param end the last element
	 */
	static std::vector<ComponentType> createVector( std::vector<ComponentType>::const_iterator begin, 
		std::vector<ComponentType>::const_iterator end );

  const std::vector<ComponentType> options;
};


/**
 * A specific type of Option that tells the virtual machine to load
 * a Java instrumentation agent.
 *
 */
class JavaAgent : public Option {
public:
	/**
	 * Creates a new JavaAgent.
	 *
	 * @param path the path to the JavaAgent
	 */
  JACE_API JavaAgent( const std::string& path );

	/**
	 * Creates a new JavaAgent.
	 *
	 * @param path the path to the JavaAgent
	 * @param options the agent options
	 */
	JACE_API JavaAgent( const std::string& path, const std::string& options );

	/**
	 * Copy constructor.
	 */
	JACE_API JavaAgent( const JavaAgent& other);
  
	/**
   * Returns the path of the Java agent.
   *
   */
  JACE_API virtual const std::string path();

	/**
   * Returns the Java agent options.
   *
   */
  JACE_API virtual const std::string options();
  JACE_API virtual const std::string stringValue() const;
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

private:
	/**
	 * Prevent assignment.
	 */
	JavaAgent& operator=( const JavaAgent& other);
	/**
	 * Removes the leading and trailing whitespace from a string.
	 *
	 * @param text the input string
	 * @return the output string
	 */
	std::string trim( const std::string& text );

	const std::string mPath;
  const std::string mOptions;
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

	/**
	 * Copy constructor.
	 */
	JACE_API CustomOption( const CustomOption& other);
  
	JACE_API virtual const std::string stringValue() const;
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

private: 
	/**
	 * Prevent assignment.
	 */
	CustomOption& operator=( const CustomOption& other);

  const std::string value;
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

  JACE_API virtual const std::string stringValue() const;
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

  JACE_API virtual const std::string stringValue() const;
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

  JACE_API virtual const std::string stringValue() const;
  JACE_API virtual void* extraInfo();
  JACE_API virtual Option* clone() const;

  private:

  abort_t hook;
};

END_NAMESPACE( jace )

#endif // JACE_OPTION_LIST_H

