//                              -*- Mode: Java -*- 
// QDTextUtils.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:37:15 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:37:34 1999
// Update Count    : 1
// Status          : Renamed
// 

package ch.epfl.lse.jqd.utils;

import ch.epfl.lse.jqd.basics.QDException;

/** This class does mac -> unicode translation.
 *  Should be replced by Java 1.1 methods
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public abstract class QDTextUtils 
{
    public static final int SPACE = 0x20 ;
    public static final int UNKNOWN = 0x20 ;
 	
    // should be changed into hashtable 
    protected static int translate(int mac_char) throws QDException {
	switch (mac_char) {
	case 0x80 : return  196 ; // A umlaut
	case 0x81 : return  194 ; // A circ 
	case 0x82 : return  199 ; // C cedil
	case 0x83 : return  201 ; // E acute
	case 0x84 : return  209 ; // N tilde
	case 0x85 : return  214 ; // O umlaut
	case 0x86 : return  220 ; // U umlaut
	case 0x87 : return  225 ; // a acute
	case 0x88 : return  224 ; // a grave
	case 0x89 : return  226 ; // a circ
	case 0x8a : return  228 ; // a umlaut
	case 0x8b : return  227 ; // a tilde
	case 0x8c : return  229 ; // a ring
	case 0x8d : return  231 ; // c cedil
	case 0x8e : return  233 ; // e acute
	case 0x8f : return  232 ; // e grave
 			
	case 0x90 : return  234 ; // e circ
	case 0x91 : return  235 ; // e umlaut
	case 0x92 : return  237 ; // i acute
	case 0x93 : return  236 ; // i grave
	case 0x94 : return  238 ; // i circ
	case 0x95 : return  239 ; // i umlaut
	case 0x96 : return  241 ; // n tilde
	case 0x97 : return  243 ; // o acute
	case 0x98 : return  242 ; // o grave
	case 0x99 : return  244 ; // o circ
	case 0x9a : return  246 ; // o umlaut
	case 0x9b : return  245 ; // o tilde
	case 0x9c : return  250 ; // u acute
	case 0x9d : return  249 ; // u grave
	case 0x9e : return  251 ; // u circ
	case 0x9f : return  252 ; // u umlaut
 			
	case 0xa0 : return  UNKNOWN ; // cross
	case 0xa1 : return  176 ; // degree
	case 0xa2 : return  162 ; // cents
	case 0xa3 : return  163 ; // pounds
	case 0xa4 : return  167 ; // paragraph
	case 0xa5 : return  183 ; // bullet (not sure)
	case 0xa6 : return  182 ; // return 
	case 0xa7 : return  223 ; // beta / 2s
	case 0xa8 : return  174 ; // registred
	case 0xa9 : return  169 ; // copyright
	case 0xaa : return  UNKNOWN ; // trade mark
	case 0xab : return  180 ; // acute
	case 0xac : return  168 ; // umlaut
	case 0xad : return  UNKNOWN ; // not equal
	case 0xae : return  198 ; // AE
 			
	case 0xb0 : return  UNKNOWN ; // infinite
	case 0xb1 : return  UNKNOWN ; // +-
	case 0xb2 : return  60 ; // <= mapped to <
	case 0xb3 : return  62 ; // >= mapped to >
	case 0xb4 : return  165 ; // yen
	case 0xb5 : return  181 ; // mu
	case 0xb6 : return  100 ; // delta mapped to d
	case 0xb7 : return  69 ; // epsilon mapped to E
	case 0xb8 : return  UNKNOWN ; // PI
	case 0xb9 : return  UNKNOWN ; // pi
	case 0xba : return  UNKNOWN ; // integral
	case 0xbb : return  170 ; // small a
	case 0xbc : return  179 ; // small o
	case 0xbd : return  UNKNOWN ; // Omega o
	case 0xbe : return  230 ; // ae
 			
	case 0xc0 : return  191 ; // rev ?
	case 0xc1 : return  161 ; // rev !
	case 0xc2 : return  172 ; // not
	case 0xc3 : return  UNKNOWN ; // root
	case 0xc4 : return  UNKNOWN ; // fl
	case 0xc5 : return  UNKNOWN ; // double tilde
	case 0xc6 : return  UNKNOWN ; // big delta
	case 0xc7 : return  171 ; // <<
	case 0xc8 : return  187 ; // >>
	case 0xc9 : return  UNKNOWN ; // elipsis
	case 0xca : return  SPACE ; // non breaking space
	case 0xcb : return  192 ; // A grave
	case 0xcc : return  195 ; // A tilde
	case 0xcd : return  213 ; // O tilde
	case 0xce : return  UNKNOWN ; // OE
	case 0xcf : return  UNKNOWN ; // oe
 			
	case 0xd0 : return  173 ; // -
	case 0xd1 : return  175 ; // long - 
	case 0xd2 : return  34 ; //  smart " <
	case 0xd3 : return  34 ; //  smart " >
	case 0xd4 : return  96 ; //  smart ' <
	case 0xd5 : return  39 ; //  smart ' >
	case 0xd6 : return  247 ; // divide
	case 0xd7 : return  UNKNOWN ; // diamond
	case 0xd8 : return  255 ; // y umlaut
	case 0xd9 : return  221 ; // Y umlaut mapped to Y acute
	case 0xda : return  47 ; // slash
	case 0xdb : return  176 ; // o x
	case 0xdc : return  60 ; // small <
	case 0xdd : return  62 ; // small >
	case 0xde : return  UNKNOWN ; // fi
	case 0xdf : return  UNKNOWN ; // FI
 			
	case 0xe0 : return  166 ; // double cross ???
	case 0xe1 : return  183 ; // small bullet
	case 0xe2 : return  96 ; // low quote
	case 0xe3 : return  34 ; // low double quote
	case 0xe4 : return  UNKNOWN ; // /oo
	case 0xe5 : return  194 ; // A circ
	case 0xe6 : return  202 ; // E circ
	case 0xe7 : return  193 ; // A acute
	case 0xe8 : return  203 ; // E uml
	case 0xe9 : return  200 ; // E grave
	case 0xea : return  205 ; // I acute
	case 0xeb : return  206 ; // I circ
	case 0xec : return  207 ; // I uml
	case 0xed : return  204 ; // I grave
	case 0xee : return  211 ; // O acute
	case 0xef : return  212 ; // O circ
 			
	case 0xf0 : return  UNKNOWN ; // apple
	case 0xf1 : return  210 ; // O grave
	case 0xf2 : return  218 ; // U acute
	case 0xf3 : return  219 ; // U circ
	case 0xf4 : return  217 ; // U grave
	case 0xf5 : return  217 ; // U grave
	case 0xf6 : return  124 ; // | ????
	case 0xf7 : return  126 ; // ~ ????
	case 0xf8 : return  UNKNOWN ; // flat accent
	case 0xf9 : return  UNKNOWN ; // tonic accent
	case 0xfa : return  183 ; // ???
	case 0xfb : return  176 ; // degree
	case 0xfc : return  44 ; // , ????
 			
	} // switch
	return(UNKNOWN);
    } // translate
 		
    protected static byte translate(byte mac_char) throws QDException {
	    if (mac_char>0) return mac_char ;
	    int unsign = mac_char+256 ;
	    int result = translate(unsign);
	    // System.err.println("translate "+unsign+" ->"+result);
	    return((byte) (result-256));
	} // translate
 	
    public static String translateASCII(byte[] macString) throws QDException {
	    byte[] isoString = new byte[macString.length] ;
	    for (int i=0 ; i<macString.length ; i++)
		isoString[i]=translate(macString[i]);
	    return new String(isoString,0);
	} // translateASCII
} // QDTextUtils
