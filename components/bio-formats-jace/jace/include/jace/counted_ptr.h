
#ifndef JACE_COUNTED_PTR_H
#define JACE_COUNTED_PTR_H

/**
 * This implementation of a reference counted smart pointer 
 * was lifted from Mark E's website. Originally, I attempted 
 * to make use of boost::shared_ptr, but its inclusion 
 * was cumbersome, due to the slew of dependencies
 * it held upon other files. Simply using the entire Boost library
 * was out of the question, as its size and scope greatly dwarfs my own.
 * I've taken care to place counted_ptr in the Jace namespace to prevent
 * possible collisions with other implementations.
 *
 * Mark's copyright notice appears below.
 */

/*
  Copyright 1998, 1999
  Mark E. (snowball3@usa.net)
  http://snowball.digitalspace.net/cpp/

  Permission to use, copy, modify, and distribute this software
  and its documentation for any purpose is hereby granted without fee,
  provided that the above copyright notice appear in all copies and
  that both that copyright notice and this permission notice appear
  in supporting documentation.

  Mark E. makes no representations about the suitability of
  this software for any purpose. It is provided "as is" without
  express or implied warranty.
*/

/*
  This header contains the classes counted_ptr and counted_array_ptr.
  These classes implement reference-counted smart pointers that automatically
  deletes the pointer it contains when no longer needed
  i.e. (reference count drops to zero)
*/


#include <stddef.h>

#include "jace/namespace.h"
#include "jace/os_dep.h"

BEGIN_NAMESPACE( jace )

template <class X>
class counted_ptr
{
//
// Public typedefs
//
public:
  typedef X element_type;
  typedef X* pointer_type;
  typedef size_t size_type;

  explicit counted_ptr(X* p=0) : ptr(p)
  {
    count=new size_type(1);
  }

  counted_ptr (const counted_ptr<X> &r)
  {
    ptr=r.ptr;
    count=r.count;
    acquire();
  }

  ~counted_ptr() { release(); }

	counted_ptr& operator= (const counted_ptr<X> &r)
	{
    if (this != &r)
    {
      release();
      ptr = r.ptr;
      count = r.count;
      acquire();
    }
    return *this;
  }

  X& operator* () const { return *ptr; }
  X* get () const { return ptr; }
  X* operator-> () const { return ptr; }

  bool unique () const
  {
    return *count==1;
  }

protected:
  X* ptr;
  size_type *count;

  void release()
  {
    if (count && --(*count)==0)
    {
      delete ptr;
      delete count;
    }
    ptr = 0;
    count = 0;
  }

  void acquire()
  {
    (*count) += 1;
  }

//  template <class Y>
//  void acquire()
//  {
//    (*count) += 1;
//  }

};

//
// counted_array_ptr
//

template <class X>
class counted_array_ptr
{
//
// Public typedefs
//
public:
  typedef X element_type;
  typedef X* pointer_type;
  typedef size_t size_type;

  explicit counted_array_ptr(X* p = NULL) : ptr(p)
  {
    count=new size_type;
    *count=1;
  }

  counted_array_ptr (const counted_array_ptr<X> &r)
  {
    ptr = r.ptr;
    count = r.count;
    (*count)++;
  }

  ~counted_array_ptr() { release(); }

  counted_array_ptr& operator= (const counted_array_ptr<X> &r)
  {
    if (this != &r)
    {
      release();
      ptr = r.ptr;
      count = r.count;
      acquire();
    }
     return *this;
  }

  X& operator* () const { return *ptr; }
  X* operator-> () const { return ptr; }
  X* get () const { return ptr; }

  bool unique () const { return *count==1; }

protected:
  X* ptr;
  size_type *count;

  void release()
  {
    if (count && --(*count)==0)
    {
      delete [] ptr;
      delete count;
    }
    ptr = 0;
    count = 0;
  }

  void acquire()
  {
    ++(*count);
  }
};

END_NAMESPACE( jace )

#endif // JACE_COUNTED_PTR_H


