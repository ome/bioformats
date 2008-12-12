
#ifndef JACE_BASE_EXCEPTION_H
#define JACE_BASE_EXCEPTION_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#include <string>
#include <exception>

BEGIN_NAMESPACE( jace )


/**
 * A simple extension to std::exception that allows users to 
 * create an exception with a string message. 
 *
 * Exception derives from std::exception so that it can 
 * be backwards compatible with anybody expecting 
 * std::exception.
 *
 * @internal - I specify that Exception doesn't ever throw,
 * but I'm not sure if that is truly the case. I think std::string
 * might throw an error if it can't allocate memory during a
 * copy or assignment. I believe that there isn't any hope of
 * recovery anyway, if you're application runs out of memory
 * while trying to throw an exception. If somebody can think
 * of a better way of handling this, I'd like to here it.
 *
 * @author Toby Reyelts
 */
class BaseException : public std::exception {


public:

/**
 * Creates a new Exception with the given mesage.
 *
 */
JACE_API BaseException( const std::string& value ) throw ();

/**
 * Creates a new Exception from the given Exception.
 *
 */
JACE_API BaseException( const BaseException& rhs ) throw ();

/**
 * Assigns this Exception to the given Exception.
 *
 */
JACE_API BaseException& operator=( const BaseException& rhs ) throw ();

/**
 * Destroys this Exception.
 *
 */
JACE_API virtual ~BaseException() throw ();

/**
 * Returns the message this Exception was created with.
 *
 */
JACE_API virtual const char *what() const throw ();


private:

/* The message for this Exception.
 */
std::string mValue;

};


END_NAMESPACE( jace )


#endif

