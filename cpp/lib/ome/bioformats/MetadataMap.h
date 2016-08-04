/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2015 Open Microscopy Environment:
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

#ifndef OME_BIOFORMATS_METADATAMAP_H
#define OME_BIOFORMATS_METADATAMAP_H

#include <algorithm>
#include <cmath>
#include <iomanip>
#include <map>
#include <ostream>
#include <sstream>
#include <string>
#include <vector>

#include <ome/compat/cstdint.h>
#include <ome/common/variant.h>

namespace ome
{
  namespace bioformats
  {

    /**
     * Metadata key-value map using a restricted set of value types.
     *
     * Permitted types include @c std::string, @c bool, integer types
     * (@c uint8_t, @c uint16_t, @c uint32_t, @c uint64_t, @c int8_t,
     * @c int16_t, @c int32_t, @c int64_t) and floating point types
     * (@c float, @c double, @c long @c double).  Additionally a @c
     * std::vector specialised for each of the aforementioned types
     * may also be stored.  However, types may not be mixed within a
     * single @c std::vector.
     *
     * The set() method permits setting of values, which includes both
     * simple values and vectors of any supported type.  The
     * convenience method append() allows appending of a single value
     * to a list.
     *
     * The get() methods permit retrieval of values.  There are
     * various forms, which offer different tradeoffs, for example
     * copying the value and returning an error, versus returning a
     * direct reference but throwing an exception on error.
     */
    class MetadataMap
    {
    private:
      /*
       * The following series of typedefs may appear a little
       * complicated, and perhaps unnecessary, but they do have a
       * purpose.  They exist to work around pre-C++11 compiler
       * limitations (lack of variadic templates), primarily a limit
       * to the maximum number of types which may be used with
       * boost::variant.  To exceed this limit (minimum guaranteed is
       * 10), boost::mpl sequences are used to define the variant
       * types.  These also have length limits, so the type list is
       * built up by defining separate type sequences, then
       * concatenating them, and transforming them to provide list
       * variants.  Note that none of this is code per se; it's all
       * compile-time template expansion which evaluates to a list of
       * permitted types.
       */

      /// Storable non-numeric types.
      typedef boost::mpl::vector<std::string,
                                 bool> non_numeric_types;

      /// Storable integer types.
      typedef boost::mpl::vector<uint8_t,
                                 uint16_t,
                                 uint32_t,
                                 uint64_t,
                                 int8_t,
                                 int16_t,
                                 int32_t,
                                 int64_t> integer_types;

      /// Storable floating-point types.
      typedef boost::mpl::vector<float,
                                 double,
                                 long double> float_types;

      /// Aggregate view of all numeric types.
      typedef boost::mpl::joint_view<integer_types,
                                     float_types>::type numeric_types_view;

      /// Aggregate view of all storable simple types.
      typedef boost::mpl::joint_view<non_numeric_types,
                                     numeric_types_view>::type basic_types_view;

      /// Convert T into a std::vector<T>.
      template<typename T>
      struct make_vector
      {
        /// The result type.
        typedef std::vector<T> type;
      };

      /// Aggregate view of all storable list types.
      typedef boost::mpl::transform_view<basic_types_view, make_vector<boost::mpl::_1> >::type list_types_view;

      /// Aggregate view of all storable types.
      typedef boost::mpl::joint_view<basic_types_view, list_types_view> all_types_view;

      /// List of discriminated types used by boost::variant.
      typedef boost::mpl::insert_range<boost::mpl::vector0<>, boost::mpl::end<boost::mpl::vector0<> >::type, all_types_view>::type discriminated_types;

    public:
      /// Key type.
      typedef std::string key_type;

      /// Value type, allowing assignment of all storable types.
      typedef boost::make_variant_over<discriminated_types>::type value_type;

      /// std::string to discriminated type mapping.
      typedef std::map<key_type, value_type> map_type;

      /// Size type.
      typedef map_type::size_type size_type;

      /// Iterator.
      typedef map_type::iterator iterator;

      /// Constant iterator.
      typedef map_type::const_iterator const_iterator;

      /// Reverse iterator.
      typedef map_type::reverse_iterator reverse_iterator;

      /// Constant reverse iterator.
      typedef map_type::const_reverse_iterator const_reverse_iterator;

    private:
      /// Key-value mapping.
      map_type discriminating_map;

    public:
      /// Constructor.
      MetadataMap()
      {}

      /// Destructor.
      ~MetadataMap()
      {}

      /**
       * Add a key-value pair to the map.
       *
       * @note If a key by the same already exists in the map, it will
       * be removed and replaced.
       *
       * @param key the key name.
       * @param value the abstract value.
       */
      void
      set(const key_type&   key,
          const value_type& value)
      {
        iterator i = find(key);
        if (i != end())
          erase(i);
        map_type::value_type newvalue(key, value);
        insert(newvalue);
      }

      /**
       * Add a key-value pair to the map.
       *
       * @note If a key by the same already exists in the map, it will
       * be removed and replaced.
       *
       * @param key the key name.
       * @param value the value.
       */
      template <typename T>
      void
      set(const key_type& key,
          const T&        value)
      {
        value_type v = value;
        set(key, v);
      }

      /**
       * Append a value to a vector.
       *
       * @note If a key by the same already exists in the map and is
       * of the wrong type or is not a vector, it will be removed and
       * replaced.
       *
       * @param key the key name.
       * @param value the value to append.
       */
      template <typename T>
      void
      append(const key_type& key,
             const T&        value)
      {
        typedef typename std::vector<T> list_type;

        try
          {
            list_type& list(get<list_type>(key));
            list.push_back(value);
          }
        catch (const boost::bad_get&)
          {
            list_type new_list;
            new_list.push_back(value);
            set(key, new_list);
          }
      }

      /**
       * Get the value of a particular key from the map.
       *
       * If the key was not found, @p value will remain unmodified.
       *
       * @param key the key to find.
       * @param value a reference to store a copy of the value.
       * @returns @c true if the key was found, @c false otherwise.
       */
      bool
      get(const key_type& key,
          value_type&     value) const
      {
        const_iterator i = find(key);
        if (i == end())
          return false;

        value = i->second;
        return true;
      }

      /**
       * Get the value of a particular key from the map.
       *
       * If the key was not found, or the type of @p value does not
       * match the stored value type, @p value will remain unmodified.
       *
       * @param key the key to find.
       * @param value a reference to store a copy of the value.
       * @returns @c true if the key was found and the value was of
       * the correct type, @c false otherwise.
       */
      template <typename T>
      bool
      get(const key_type& key,
          T&              value) const
      {
        try
          {
            value = get<T>(key);
            return true;
          }
        catch (const boost::bad_get&)
          {
            return false;
          }
      }

      /**
       * Get a reference to the value of a particular key from the map.
       *
       * If the key was not found, or the type of @p value does not
       * match the stored value type, @p value will remain unmodified.
       *
       * @param key the key to find.
       * @returns a reference to the stored value.
       * @throws boost::bad_get on failure if the key was not found or
       * if the type did not match the stored value type.
       */
      template <typename T>
      T&
      get(const key_type& key)
      {
        return boost::get<T>(get<value_type>(key));
      }

      /**
       * Get a reference to the value of a particular key from the map.
       *
       * If the key was not found, or the type of @p value does not
       * match the stored value type, @p value will remain unmodified.
       *
       * @param key the key to find.
       * @returns a reference to the stored value.
       * @throws boost::bad_get on failure if the key was not found or
       * if the type did not match the stored value type.
       */
      template <typename T>
      const T&
      get(const key_type& key) const
      {
        return boost::get<T>(get<value_type>(key));
      }

      /**
       * Find a key in the map.
       *
       * @param key the key to find.
       * @returns an iterator to the key-value pair.
       */
      iterator
      find(const key_type& key)
      {
        return discriminating_map.find(key);
      }

      /**
       * Find a key in the map.
       *
       * @param key the key to find.
       * @returns an iterator to the key-value pair.
       */
      const_iterator
      find(const key_type& key) const
      {
        return discriminating_map.find(key);
      }

      /**
       * Insert a value into the map.
       *
       * Note that this method is an alternative to set(), which
       * retains the insert semantics of std::map::insert(), i.e. it
       * will not replace keys which already exist in the map, and so
       * will be a no-op if the key is already present.
       *
       * @param value the value to insert.
       * @returns an iterator to the inserted key, or prexisting key
       * if present, and @c true if the value was inserted, @c false
       * otherwise.
       */
      std::pair<iterator, bool>
      insert(map_type::value_type& value)
      {
        return discriminating_map.insert(value);
      }

      /**
       * Erase a key from the map by name.
       *
       * @param key the key to erase.
       */
      void
      erase(const key_type& key)
      {
        discriminating_map.erase(key);
      }

      /**
       * Erase a key from the map by an iterator position.
       *
       * @param pos the iterator position to erase.
       */
      void
      erase(iterator pos)
      {
        discriminating_map.erase(pos);
      }

    private:
      /// Functor to get a map key.
      struct getkey
      {
        /**
         * Get key from pair.
         *
         * @param pair the pair to use.
         * @returns the key.
         */
        template <typename T>
        typename T::first_type operator()(T pair) const
        {
          return pair.first;
        }
      };

    public:
      /**
       * Get a list of keys in the map.
       *
       * @note This is inefficient, and exists solely for
       * compatibility with the Java implementation.  Using the
       * iterator interface directly should be preferred.
       *
       * @returns a sorted list of keys.
       */
      std::vector<key_type>
      keys() const
      {
        std::vector<key_type> ret;
        std::transform(begin(), end(), std::back_inserter(ret), getkey());
        std::sort(ret.begin(), ret.end());

        return ret;
      }

      /**
       * Merge a separate map into this map.
       *
       * @param map the map to merge.
       * @param prefix a prefix to append to the keys of the map being
       * merged.
       */
      void
      merge(const MetadataMap& map,
            const std::string& prefix)
      {
        for (const_iterator i = map.begin();
             i != map.end();
             ++i)
          {
            map_type::value_type v(prefix + i->first, i->second);
            insert(v);
          }
      }

      /**
       * Create a flattened map.
       *
       * All vectors in the map will be flattened, which is the
       * replacement of each vector by a key per element with with a
       * @c \#n key suffix.
       *
       * @returns the flattened map.
       */
      MetadataMap
      flatten() const;

      /**
       * Get the underlying map.
       *
       * @returns a reference to the map.
       */
      map_type&
      map()
      {
        return discriminating_map;
      }

      /**
       * Get the underlying map.
       *
       * @returns a reference to the map.
       */
      const map_type&
      map() const
      {
        return discriminating_map;
      }

      /**
       * Get an iterator to the beginning of the map.
       *
       * @returns an iterator.
       */
      iterator
      begin()
      {
        return discriminating_map.begin();
      }

      /**
       * Get a constant iterator to the beginning of the map.
       *
       * @returns a constant iterator.
       */
      const_iterator
      begin() const
      {
        return discriminating_map.begin();
      }

      /**
       * Get an iterator to the end of the map.
       *
       * @returns an iterator.
       */
      iterator
      end()
      {
        return discriminating_map.end();
      }

      /**
       * Get a constant iterator to the end of the map.
       *
       * @returns a constant iterator.
       */
      const_iterator
      end() const
      {
        return discriminating_map.end();
      }

      /**
       * Get a reverse iterator to the end of the map.
       *
       * @returns an iterator.
       */
      reverse_iterator
      rbegin()
      {
        return discriminating_map.rbegin();
      }

      /**
       * Get a constant reverse iterator to the end of the map.
       *
       * @returns an iterator.
       */
      const_reverse_iterator
      rbegin() const
      {
        return discriminating_map.rbegin();
      }

      /**
       * Get a reverse iterator to the beginning of the map.
       *
       * @returns an iterator.
       */
      reverse_iterator
      rend()
      {
        return discriminating_map.rend();
      }

      /**
       * Get a constant reverse iterator to the beginning of the map.
       *
       * @returns an iterator.
       */
      const_reverse_iterator
      rend() const
      {
        return discriminating_map.rend();
      }

      /**
       * Get or set a value by key index.
       *
       * Access the metadata map as an associative array.  This may be
       * used to get or set a value, with the value being created if
       * the key was not already present in the map.
       *
       * @param key the key to retrieve.
       * @returns a reference to the value associated with the @p key.
       */
      value_type&
      operator[] (const key_type& key)
      {
        return discriminating_map[key];
      }

      /**
       * Compare maps for equality.
       *
       * @param rhs the map to compare.
       * @returns @c true if equal to @p rhs, @c false otherwise.
       */
      bool
      operator == (const MetadataMap& rhs) const
      {
        return discriminating_map == rhs.discriminating_map;
      }

      /**
       * Compare maps for non-equality.
       *
       * @param rhs the map to compare.
       * @returns @c true if not equal to @p rhs, @c false otherwise.
       */
      bool
      operator != (const MetadataMap& rhs) const
      {
        return discriminating_map != rhs.discriminating_map;
      }

      /**
       * Check if map is less than another map.
       *
       * @param rhs the map to compare.
       * @returns @c true if less than @p rhs, @c false otherwise.
       */
      bool
      operator < (const MetadataMap& rhs) const
      {
        return discriminating_map < rhs.discriminating_map;
      }

      /**
       * Check if map is less than or equal to another map.
       *
       * @param rhs the map to compare.
       * @returns @c true if less than or equal to @p rhs, @c false
       * otherwise.
       */
      bool
      operator <= (const MetadataMap& rhs) const
      {
        return discriminating_map <= rhs.discriminating_map;
      }

      /**
       * Check if map is greater than another map.
       *
       * @param rhs the map to compare.
       * @returns @c true if greater than @p rhs, @c false otherwise.
       */
      bool
      operator > (const MetadataMap& rhs) const
      {
        return discriminating_map > rhs.discriminating_map;
      }

      /**
       * Check if map is greater than or equal to another map.
       *
       * @param rhs the map to compare.
       * @returns @c true if greater than or equal to @p rhs, @c false
       * otherwise.
       */
      bool
      operator >= (const MetadataMap& rhs) const
      {
        return discriminating_map >= rhs.discriminating_map;
      }

      /**
       * Get the size of the map.
       *
       * Note that vectors are counted as a single item; child
       * elements are not included.
       *
       * @returns the number of elements.
       */
      size_type
      size() const
      {
        return discriminating_map.size();
      }

      /**
       * Check if the map is empty.
       *
       * @returns @c true if empty, @c false otherwise.
       */
      bool
      empty() const
      {
        return discriminating_map.empty();
      }

      /**
       * Clear the map.
       *
       * All keys are cleared from the map.
       */
      void
      clear()
      {
        return discriminating_map.clear();
      }
    };

    namespace detail
    {

      /**
       * Visitor template for output of MetadataMap values to an ostream.
       */
      struct MetadataMapValueTypeOStreamVisitor : public boost::static_visitor<>
      {
        /// The stream to output to.
        std::ostream& os;

        /**
         * Constructor.
         *
         * @param os the output stream to output to.
         */
        MetadataMapValueTypeOStreamVisitor(std::ostream& os):
          os(os)
        {}

        /**
         * Output a vector value of arbitrary type.
         *
         * Each value will be output in a comma-separated list.
         *
         * @param c the container values to output.
         */
        template <typename T>
        void
        operator() (const std::vector<T> & c) const
        {
          for (typename std::vector<T>::const_iterator i = c.begin();
               i != c.end();
               ++i)
            {
              os << *i;
              if (i + 1 != c.end())
                os << ", ";
            }
        }

        /**
         * Output a scalar value of arbitrary type.
         *
         * @param v the value to output.
         */
        template <typename T>
        void
        operator() (const T& v) const
        {
          os << v;
        }
      };

      /**
       * Visitor template for output of MetadataMap values to an ostream.
       */
      struct MetadataMapOStreamVisitor : public boost::static_visitor<>
      {
        /// The stream to output to.
        std::ostream& os;
        /// The key of the value being output.
        const MetadataMap::key_type& key;

        /**
         * Constructor.
         *
         * @param os the output stream to output to.
         * @param key the key being output.
         */
        MetadataMapOStreamVisitor(std::ostream&                os,
                                  const MetadataMap::key_type& key):
          os(os),
          key(key)
        {}

        /**
         * Output a vector value of arbitrary type.
         *
         * Each value will be output as a separate line of the form @c
         * "key #n = value", where @c n is the index into the vector,
         * indexed from 1.  The suffix will be left zero-padded to the
         * width of the largest suffix.
         *
         * @param c the container values to output.
         */
        template <typename T>
        void
        operator() (const std::vector<T> & c) const
        {
          typename std::vector<T>::size_type idx = 1;
          // Determine the optimal padding based on the maximum digit count.
          int sf = static_cast<int>(std::log10(static_cast<float>(c.size()))) + 1;
          for (typename std::vector<T>::const_iterator i = c.begin();
               i != c.end();
               ++i, ++idx)
            {
              os << key << " #" << std::setw(sf) << std::setfill('0') << std::right << idx << " = " << *i << '\n';
            }
        }

        /**
         * Output a scalar value of arbitrary type.
         *
         * The value will be output as a separate line of the form
         * @c "key = value".
         *
         * @param v the value to output.
         */
        template <typename T>
        void
        operator() (const T& v) const
        {
          os << key << " = " << v << '\n';
        }
      };

      /**
       * Visitor template for flattening of MetadataMap vector values.
       */
      struct MetadataMapFlattenVisitor : public boost::static_visitor<>
      {
        /// The map in which to set the flattened elements.
        MetadataMap& map;
        /// The key of the value being flattened.
        const MetadataMap::key_type& key;

        /**
         * Constructor.
         *
         * @param map the map in which to set flattened elements.
         * @param key the key being output.
         */
        MetadataMapFlattenVisitor(MetadataMap&                 map,
                                  const MetadataMap::key_type& key):
          map(map),
          key(key)
        {}

        /**
         * Output a vector value of arbitrary type.
         *
         * Each value will be set as a separate entry of with the a @c
         * \#n suffix added to the key, where @c n is the index into the
         * vector, indexed from 1.  The suffix will be left zero-padded
         * to the width of the largest suffix.
         *
         * @param c the container values to output.
         */
        template <typename T>
        void
        operator() (const std::vector<T> & c) const
        {
          typename std::vector<T>::size_type idx = 1;
          // Determine the optimal padding based on the maximum digit count.
          int sf = static_cast<int>(std::log10(static_cast<float>(c.size()))) + 1;
          for (typename std::vector<T>::const_iterator i = c.begin();
               i != c.end();
               ++i, ++idx)
            {
              std::ostringstream os;
              os << key << " #" << std::setw(sf) << std::setfill('0') << std::right << idx;
              map.set(os.str(), *i);
            }
        }

        /**
         * Output a scalar value of arbitrary type.
         *
         * The value will be output as a separate line of the form
         * @c "key = value".
         *
         * @param v the value to output.
         */
        template <typename T>
        void
        operator() (const T& v) const
        {
          map.set(key, v);
        }
      };

    }

    /**
     * Get a reference to the value of a particular key from the map.
     *
     * If the key was not found, or the type of @p value does not
     * match the stored value type, @p value will remain unmodified.
     *
     * @param key the key to find.
     * @returns a reference to the stored value.
     * @throws boost::bad_get on failure if the key was not found or
     * if the type did not match the stored value type.
     */
    template<>
    inline MetadataMap::value_type&
    MetadataMap::get<MetadataMap::value_type>(const key_type& key)
    {
      map_type::iterator i = discriminating_map.find(key);
      if (i == discriminating_map.end())
        throw boost::bad_get();

      return i->second;
    }

    /**
     * Get a reference to the value of a particular key from the map.
     *
     * If the key was not found, or the type of @p value does not
     * match the stored value type, @p value will remain unmodified.
     *
     * @param key the key to find.
     * @returns a reference to the stored value.
     * @throws boost::bad_get on failure if the key was not found or
     * if the type did not match the stored value type.
     */
    template<>
    inline const MetadataMap::value_type&
    MetadataMap::get<MetadataMap::value_type>(const key_type& key) const
    {
      map_type::const_iterator i = discriminating_map.find(key);
      if (i == discriminating_map.end())
        throw boost::bad_get();

      return i->second;
    }

    inline
    MetadataMap
    MetadataMap::flatten() const
    {
      MetadataMap newmap;
      for (MetadataMap::const_iterator i = discriminating_map.begin();
           i != discriminating_map.end();
           ++i)
        {
          boost::apply_visitor(detail::MetadataMapFlattenVisitor(newmap, i->first), i->second);
        }
      return newmap;
    }

  }
}

namespace std
{

    /**
     * Output MetadataMap::value_type to output stream.
     *
     * @param os the output stream.
     * @param vt the MetadataMap::value_type to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline basic_ostream<charT,traits>&
    operator<< (basic_ostream<charT,traits>& os,
                const ::ome::bioformats::MetadataMap::value_type& vt)
    {
      boost::apply_visitor(::ome::bioformats::detail::MetadataMapValueTypeOStreamVisitor(os), vt);
      return os;
    }

    /**
     * Output MetadataMap to output stream.
     *
     * @param os the output stream.
     * @param map the MetadataMap to output.
     * @returns the output stream.
     */
    template<class charT, class traits>
    inline basic_ostream<charT,traits>&
    operator<< (basic_ostream<charT,traits>& os,
                const ::ome::bioformats::MetadataMap& map)
    {
      for (::ome::bioformats::MetadataMap::const_iterator i = map.begin();
           i != map.end();
           ++i)
        {
          boost::apply_visitor(::ome::bioformats::detail::MetadataMapOStreamVisitor(os, i->first), i->second);
        }
      return os;
    }

}

#endif // OME_BIOFORMATS_METADATAMAP_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
