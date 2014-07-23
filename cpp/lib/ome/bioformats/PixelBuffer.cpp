/*
 * #%L
 * OME-BIOFORMATS C++ library for image IO.
 * Copyright © 2006 - 2014 Open Microscopy Environment:
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

#include <ome/bioformats/PixelBuffer.h>

using ome::bioformats::PixelBuffer;
using ome::bioformats::PixelEndianProperties;
using ome::bioformats::EndianType;
using ome::bioformats::VariantPixelBuffer;
using ::ome::xml::model::enums::PixelType;
namespace
{

  struct PBCopyVisitor : public boost::static_visitor<>
  {
  private:
    VariantPixelBuffer::variant_buffer_type& dest;

  public:
    PBCopyVisitor(VariantPixelBuffer::variant_buffer_type& dest):
      dest(dest)
    {}

    template <typename T>
    void
    operator() (const T& v)
    {
      dest = v;
    }
  };

  struct PBNumElementsVisitor : public boost::static_visitor<boost::multi_array_types::size_type>
  {
    template <typename T>
    boost::multi_array_types::size_type
    operator() (const T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->num_elements();
    }
  };

  struct PBNumDimensionsVisitor : public boost::static_visitor<boost::multi_array_types::size_type>
  {
    template <typename T>
    boost::multi_array_types::size_type
    operator() (const T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->num_dimensions();
    }
  };

  struct PBShapeVisitor : public boost::static_visitor<const boost::multi_array_types::size_type *>
  {
    template <typename T>
    const boost::multi_array_types::size_type *
    operator() (const T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->shape();
    }
  };

  struct PBStridesVisitor : public boost::static_visitor<const boost::multi_array_types::index *>
  {
    template <typename T>
    const boost::multi_array_types::index *
    operator() (const T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->strides();
    }
  };

  struct PBIndexBasesVisitor : public boost::static_visitor<const boost::multi_array_types::index *>
  {
    template <typename T>
    const boost::multi_array_types::index *
    operator() (const T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->index_bases();
    }
  };

  struct PBStorageOrderVisitor : public boost::static_visitor<const VariantPixelBuffer::storage_order_type&>
  {
    template <typename T>
    const VariantPixelBuffer::storage_order_type&
    operator() (const T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->storage_order();
    }
  };

  struct PBRawBufferVisitor : public boost::static_visitor<VariantPixelBuffer::raw_type *>
  {
    template <typename T>
    VariantPixelBuffer::raw_type *
    operator() (T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return reinterpret_cast<VariantPixelBuffer::raw_type *>(v->array()->data());
    }
  };

  template<typename T>
  struct PBBufferVisitor : public boost::static_visitor<>
  {
    T *
    operator() (T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->array()->data();
    }

    template <typename U>
    void
    operator() (U& /* v */) const
    {
      throw std::runtime_error("Unsupported pixel type conversion for buffer");
    }
  };

  struct PBCompareVisitor : public boost::static_visitor<bool>
  {
    template <typename T, typename U>
    bool
    operator() (T& /* lhs */, U& /* rhs */) const
    {
      return false;
    }

    template <typename T>
    bool
    operator() (T& lhs, T& rhs) const
    {
      return lhs && rhs &&
        lhs->array() && rhs->array() &&
        *(lhs->array()) == *(rhs->array());
    }
  };

  template <typename InputIterator>
  struct PBAssignVisitor : public boost::static_visitor<>
  {
    InputIterator begin, end;

    PBAssignVisitor(InputIterator begin, InputIterator end):
      begin(begin), end(end)
    {}

    template <typename T>
    void
    operator() (T& /* v */) const
    {
      throw std::runtime_error("Unsupported pixel type conversion for assignment");
    }

    template <int P, int E>
    typename boost::enable_if<
      typename boost::is_same<
        typename std::iterator_traits<InputIterator>::value_type,
        typename PixelEndianProperties<P, E>::type
        >::value,
      void
      >::type
    operator() (std::shared_ptr<PixelBuffer<typename PixelEndianProperties<P, E>::type> >& lhs) const
    {
      if (!lhs)
        throw std::runtime_error("Null pixel type");
      lhs->array()->assign(begin, end);
    }
  };

  struct PBPixelTypeVisitor : public boost::static_visitor<PixelType>
  {
    template <typename T>
    PixelType
    operator() (T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->pixelType();
    }
  };

  struct PBPixelEndianVisitor : public boost::static_visitor<EndianType>
  {
    template <typename T>
    EndianType
    operator() (T& v)
    {
      if (!v)
        throw std::runtime_error("Null pixel type");
      return v->endianType();
    }
  };

}

namespace ome
{
  namespace bioformats
  {

    // No switch default to avoid -Wunreachable-code errors.
    // However, this then makes -Wswitch-default complain.  Disable
    // temporarily.
#ifdef __GNUC__
#  pragma GCC diagnostic push
#  pragma GCC diagnostic ignored "-Wswitch-default"
#endif

    PixelBufferBase::storage_order_type
    PixelBufferBase::make_storage_order(ome::xml::model::enums::DimensionOrder order,
                                        bool                                   interleaved)
    {
      size_type ordering[dimensions];
      bool ascending[dimensions] = {true, true, true, true, true, true, true, true, true};

      if (interleaved)
        {
          ordering[0] = DIM_SUBCHANNEL;
          ordering[1] = DIM_SPATIAL_X;
          ordering[2] = DIM_SPATIAL_Y;
        }
      else
        {
          ordering[0] = DIM_SPATIAL_X;
          ordering[1] = DIM_SPATIAL_Y;
          ordering[2] = DIM_SUBCHANNEL;
        }

      switch(order)
        {
        case ome::xml::model::enums::DimensionOrder::XYZTC:
          ordering[3] = DIM_MODULO_Z;
          ordering[4] = DIM_SPATIAL_Z;
          ordering[5] = DIM_MODULO_T;
          ordering[6] = DIM_TEMPORAL_T;
          ordering[7] = DIM_MODULO_C;
          ordering[8] = DIM_CHANNEL;
          break;
        case ome::xml::model::enums::DimensionOrder::XYZCT:
          ordering[3] = DIM_MODULO_Z;
          ordering[4] = DIM_SPATIAL_Z;
          ordering[5] = DIM_MODULO_C;
          ordering[6] = DIM_CHANNEL;
          ordering[7] = DIM_MODULO_T;
          ordering[8] = DIM_TEMPORAL_T;
          break;
        case ome::xml::model::enums::DimensionOrder::XYTZC:
          ordering[3] = DIM_MODULO_T;
          ordering[4] = DIM_TEMPORAL_T;
          ordering[5] = DIM_MODULO_Z;
          ordering[6] = DIM_SPATIAL_Z;
          ordering[7] = DIM_MODULO_C;
          ordering[8] = DIM_CHANNEL;
          break;
        case ome::xml::model::enums::DimensionOrder::XYTCZ:
          ordering[3] = DIM_MODULO_T;
          ordering[4] = DIM_TEMPORAL_T;
          ordering[5] = DIM_MODULO_C;
          ordering[6] = DIM_CHANNEL;
          ordering[7] = DIM_MODULO_Z;
          ordering[8] = DIM_SPATIAL_Z;
          break;
        case ome::xml::model::enums::DimensionOrder::XYCZT:
          ordering[3] = DIM_MODULO_C;
          ordering[4] = DIM_CHANNEL;
          ordering[5] = DIM_MODULO_Z;
          ordering[6] = DIM_SPATIAL_Z;
          ordering[7] = DIM_MODULO_T;
          ordering[8] = DIM_TEMPORAL_T;
          break;
        case ome::xml::model::enums::DimensionOrder::XYCTZ:
          ordering[3] = DIM_MODULO_C;
          ordering[4] = DIM_CHANNEL;
          ordering[5] = DIM_MODULO_T;
          ordering[6] = DIM_TEMPORAL_T;
          ordering[7] = DIM_MODULO_Z;
          ordering[8] = DIM_SPATIAL_Z;
          break;
        }

      return storage_order_type(ordering, ascending);
    }

#ifdef __GNUC__
#  pragma GCC diagnostic pop
#endif

    PixelBufferBase::storage_order_type
    PixelBufferBase::default_storage_order()
    {
      return make_storage_order(ome::xml::model::enums::DimensionOrder::XYZTC, true);
    }

    VariantPixelBuffer::VariantPixelBuffer(const VariantPixelBuffer& buffer):
      buffer()
    {
      PBCopyVisitor v(this->buffer);
      boost::apply_visitor(v, buffer.buffer);
    }

    boost::multi_array_types::size_type
    VariantPixelBuffer::num_elements() const
    {
      PBNumElementsVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    boost::multi_array_types::size_type
    VariantPixelBuffer::num_dimensions() const
    {
      PBNumDimensionsVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    const boost::multi_array_types::size_type *
    VariantPixelBuffer::shape() const
    {
      PBShapeVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    const boost::multi_array_types::index *
    VariantPixelBuffer::strides() const
    {
      PBStridesVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    const boost::multi_array_types::index *
    VariantPixelBuffer::index_bases() const
    {
      PBStridesVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    const VariantPixelBuffer::storage_order_type&
    VariantPixelBuffer::storage_order() const
    {
      PBStorageOrderVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    PixelType
    VariantPixelBuffer::pixelType() const
    {
      PBPixelTypeVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    EndianType
    VariantPixelBuffer::endianType() const
    {
      PBPixelEndianVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    VariantPixelBuffer::raw_type *
    VariantPixelBuffer::data()
    {
      PBRawBufferVisitor v;
      return boost::apply_visitor(v, buffer);
    }

    bool
    VariantPixelBuffer::operator == (const VariantPixelBuffer& rhs) const
    {
      return boost::apply_visitor(PBCompareVisitor(), buffer, rhs.buffer);
    }

    bool
    VariantPixelBuffer::operator != (const VariantPixelBuffer& rhs) const
    {
      return !(*this == rhs);
    }

  }
}
