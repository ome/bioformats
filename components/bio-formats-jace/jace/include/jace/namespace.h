
#ifndef JACE_NAMESPACE_H
#define JACE_NAMESPACE_H

/**
 * This header file defines macros for creating nested namespaces.
 *
 * For example, the following code:
 *
 *   namespace com {
 *     namespace foo {
 *       namespace bar {
 *         namespace baz {
 *           // some code
 *         }
 *       }
 *     }
 *   }
 *
 * can instead be written as:
 *
 *   BEGIN_NAMESPACE_4( com, foo, bar, baz )
 *     // some code
 *   END_NAMESPACE_4( com, foo, bar, baz )
 *
 * @internal
 * It's messy that we have to define multiple BEGIN_NAMESPACE macros
 * and that the user has to use the appropriately numbered macro.
 * It would be nicer if we only had two macros, BEGIN_NAMESPACE,
 * and END_NAMESPACE, which could take any number of arguments
 * and work correctly. Unfortunately, I can't think of any way
 * to do that.
 *
 * @author Toby Reyelts
 *
 */

#define BEGIN_NAMESPACE( n1 ) namespace n1 {
#define END_NAMESPACE( n1 ) }

#define BEGIN_NAMESPACE_1( n1 ) namespace n1 {
#define END_NAMESPACE_1( n1 ) }

#define BEGIN_NAMESPACE_2( n1, n2 ) namespace n1 { namespace n2 {
#define END_NAMESPACE_2( n1, n2 ) } }

#define BEGIN_NAMESPACE_3( n1, n2, n3 ) namespace n1 { namespace n2 { namespace n3 {
#define END_NAMESPACE_3( n1, n2, n3 ) } } }

#define BEGIN_NAMESPACE_4( n1, n2, n3, n4 ) namespace n1 { namespace n2 { namespace n3 { namespace n4 {
#define END_NAMESPACE_4( n1, n2, n3, n4 ) } } } }

#define BEGIN_NAMESPACE_5( n1, n2, n3, n4, n5 ) namespace n1 { namespace n2 { namespace n3 { namespace n4 { namespace n5 {
#define END_NAMESPACE_5( n1, n2, n3, n4, n5 ) } } } } }

#define BEGIN_NAMESPACE_6( n1, n2, n3, n4, n5, n6 ) namespace n1 { namespace n2 { namespace n3 { namespace n4 { namespace n5 { namespace n6 {
#define END_NAMESPACE_6( n1, n2, n3, n4, n5, n6 ) } } } } } }

#define BEGIN_NAMESPACE_7( n1, n2, n3, n4, n5, n6, n7 ) namespace n1 { namespace n2 { namespace n3 { namespace n4 { namespace n5 { namespace n6 { namespace n7 {
#define END_NAMESPACE_7( n1, n2, n3, n4, n5, n6, n7 ) } } } } } } }

#define BEGIN_NAMESPACE_8( n1, n2, n3, n4, n5, n6, n7, n8 ) namespace n1 { namespace n2 { namespace n3 { namespace n4 { namespace n5 { namespace n6 { namespace n7 { namespace n8 {
#define END_NAMESPACE_8( n1, n2, n3, n4, n5, n6, n7, n8 ) } } } } } } } }

#define BEGIN_NAMESPACE_9( n1, n2, n3, n4, n5, n6, n7, n8, n9 ) namespace n1 { namespace n2 { namespace n3 { namespace n4 { namespace n5 { namespace n6 { namespace n7 { namespace n8 { namespace n9 {
#define END_NAMESPACE_9( n1, n2, n3, n4, n5, n6, n7, n8, n9 ) } } } } } } } } }

#define BEGIN_NAMESPACE_10( n1, n2, n3, n4, n5, n6, n7, n8, n9, n10 ) namespace n1 { namespace n2 { namespace n3 { namespace n4 { namespace n5 { namespace n6 { namespace n7 { namespace n8 { namespace n9 { namespace n10 {
#define END_NAMESPACE_10( n1, n2, n3, n4, n5, n6, n7, n8, n9, n10 ) } } } } } } } } } }

#endif

