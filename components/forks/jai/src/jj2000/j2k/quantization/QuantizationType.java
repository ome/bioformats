/*
 * $RCSfile: QuantizationType.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:18 $
 * $State: Exp $
 *
 * Class:                   QuantizationType
 *
 * Description:             This interface defines the possible
 *                          quantization types.
 *
 *
 *
 * COPYRIGHT:
 *
 * This software module was originally developed by Raphaël Grosbois and
 * Diego Santa Cruz (Swiss Federal Institute of Technology-EPFL); Joel
 * Askelöf (Ericsson Radio Systems AB); and Bertrand Berthelot, David
 * Bouchard, Félix Henry, Gerard Mozelle and Patrice Onno (Canon Research
 * Centre France S.A) in the course of development of the JPEG2000
 * standard as specified by ISO/IEC 15444 (JPEG 2000 Standard). This
 * software module is an implementation of a part of the JPEG 2000
 * Standard. Swiss Federal Institute of Technology-EPFL, Ericsson Radio
 * Systems AB and Canon Research Centre France S.A (collectively JJ2000
 * Partners) agree not to assert against ISO/IEC and users of the JPEG
 * 2000 Standard (Users) any of their rights under the copyright, not
 * including other intellectual property rights, for this software module
 * with respect to the usage by ISO/IEC and Users of this software module
 * or modifications thereof for use in hardware or software products
 * claiming conformance to the JPEG 2000 Standard. Those intending to use
 * this software module in hardware or software products are advised that
 * their use may infringe existing patents. The original developers of
 * this software module, JJ2000 Partners and ISO/IEC assume no liability
 * for use of this software module or modifications thereof. No license
 * or right to this software module is granted for non JPEG 2000 Standard
 * conforming products. JJ2000 Partners have full right to use this
 * software module for his/her own purpose, assign or donate this
 * software module to any third party and to inhibit third parties from
 * using this software module for non JPEG 2000 Standard conforming
 * products. This copyright notice must be included in all copies or
 * derivative works of this software module.
 *
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *
 *
 *
 */


package jj2000.j2k.quantization;

/**
 * This interface defines the IDs of the possible quantization types. JPEG
 * 2000 part I support only the scalar quantization with dead zone. However
 * other quantization type may be defined in JPEG 2000 extensions (for
 * instance Trellis Coded Quantization).
 *
 * <P>This interface defines the constants only. In order to use the
 * constants in any other class you can either use the fully qualified
 * name (e.g., <tt>QuantizationType.Q_TYPE_SCALAR_DZ</tt>) or declare
 * this interface in the implements clause of the class and then
 * access the identifier directly.
 * */
public interface QuantizationType {

    /** The ID of the scalar deadzone dequantizer */
    public final static int Q_TYPE_SCALAR_DZ = 0;

}
