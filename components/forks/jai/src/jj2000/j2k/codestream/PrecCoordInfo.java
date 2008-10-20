/*
 * $RCSfile: PrecCoordInfo.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:00 $
 * $State: Exp $
 *
 * Class:                   PrecCoordInfo
 *
 * Description:             Used to store the coordinates precincts.
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
 * */
package jj2000.j2k.codestream;

/**
 * This class is used to store the coordinates of precincts.
 * */
public class PrecCoordInfo extends CoordInfo {

    /** Horizontal upper left coordinate in the reference grid */
    public int xref;

    /** Vertical upper left coordinate on the reference grid */
    public int yref;

    /**
     * Constructor. Creates a PrecCoordInfo object.
     *
     * @param ulx Horizontal upper left coordinate in the subband
     *
     * @param uly Vertical upper left coordinate in the subband
     *
     * @param w Precint's width
     *
     * @param h Precinct's height
     *
     * @param xref The horizontal coordinate on the reference grid
     *
     * @param yref The vertical coordinate on the reference grid
     * */
    public PrecCoordInfo(int ulx, int uly, int w, int h,
                        int xref, int yref) {
        super(ulx, uly, w, h);
        this.xref = xref;
        this.yref = yref;
    }

    /**
     * Empty Constructor. Creates an empty PrecCoordInfo object.
     * */
    public PrecCoordInfo() {
        super();
    }

    /**
     * Returns precinct's information in a String
     *
     * @return String with precinct's information
     * */
    public String toString() {
        return super.toString() + ", xref="+xref+", yref="+yref;
    }
}
