/*
 * #%L
 * OME-XERCES C++ library for working with Xerces C++.
 * %%
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

#ifndef OME_COMMON_XML_DOM_NODE_H
#define OME_COMMON_XML_DOM_NODE_H

#include <ome/common/config.h>

#include <ome/common/xml/String.h>
#include <ome/common/xml/dom/Base.h>
#include <ome/common/xml/dom/NodeList.h>
#include <ome/common/xml/dom/NamedNodeMap.h>
#include <ome/common/xml/dom/Wrapper.h>

#include <xercesc/dom/DOMNode.hpp>

namespace ome
{
  namespace common
  {
    namespace xml
    {
      namespace dom
      {

        /**
         * DOM Node wrapper.  The wrapper behaves as though is the
         * wrapped DOMNode; it can be dereferenced using the "*" or "->"
         * operators to obtain a reference or pointer to the wrapped
         * object.  It can also be cast to a pointer to the wrapped
         * object, so can substitute for it directly.
         */
        class Node : public Wrapper<xercesc::DOMNode, Base<xercesc::DOMNode> >
        {
        public:
          /// The derived object type of a node.
          typedef xercesc::DOMNode::NodeType node_type;

          /**
           * Construct a NULL Node.
           */
          Node ():
            Wrapper<xercesc::DOMNode, Base<xercesc::DOMNode> >()
          {
          }

          /**
           * Copy construct a Node.
           *
           * @param node the Node to copy.
           */
          Node (const Node& node):
            Wrapper<xercesc::DOMNode, Base<xercesc::DOMNode> >(node)
          {
          }

          /**
           * Construct a Node from a xercesc::DOMNode *.
           *
           * @param node the Node to wrap.
           * @param managed is the value to be managed?
           */
          Node (xercesc::DOMNode *node,
                bool              managed):
            Wrapper<xercesc::DOMNode, Base<xercesc::DOMNode> >(managed ?
                                                               Wrapper<xercesc::DOMNode, Base<xercesc::DOMNode> >(node, std::mem_fun(&base_element_type::release)) :
                                                               Wrapper<xercesc::DOMNode, Base<xercesc::DOMNode> >(node, &ome::common::xml::dom::detail::unmanaged<base_element_type>))
          {
          }

          /// Destructor.
          ~Node ()
          {
          }

          /**
           * Append a child Node.
           *
           * @param node the child Node to append.
           * @returns the appended Node.
           */
          Node
          appendChild (Node& node)
          {
            // TODO: Catch and rethrow xerces exceptions with the xerces
            // errors converted to sane descriptions.  And additionally
            // for all other xerces methods which throw.
            return Node((*this)->appendChild(node.get()), false);
          }

          /**
           * Get the object type of this node.
           *
           * @return the object type.
           */
          node_type
          getNodeType ()
          {
            return (*this)->getNodeType();
          }

          /**
           * Get child nodes.
           *
           * @returns the child nodes (if any).
           */
          NodeList
          getChildNodes()
          {
            return NodeList((*this)->getChildNodes());
          }

          /**
           * Get node attributes.
           *
           * @returns the attributes.
           */
          NamedNodeMap
          getAttributes()
          {
            return NamedNodeMap((*this)->getAttributes());
          }

          /**
           * Get node value.
           *
           * @returns the node value.
           */
          std::string
          getNodeValue()
          {
            return String((*this)->getNodeValue());
          }

          /**
           * Get node text content.
           *
           * @returns the text content.
           */
          std::string
          getTextContent()
          {
            return String((*this)->getTextContent());
          }
        };

      }
    }
  }
}

#endif // OME_COMMON_XML_DOM_NODE_H

/*
 * Local Variables:
 * mode:C++
 * End:
 */
