package com.sun.jimi.core.encoder.jpg;

public class Fwddct
{
	static final int DCTSIZE = 8;
	static final int CONST_BITS = 13;
	static final short PASS1_BITS = 2;

	static final int CONST_SCALE = (1 << CONST_BITS);

	// Convert a positive real constant to an integer scaled by CONST_SCALE. 

	static public int scaleToInt(float f)
	{
		return (int)(f * CONST_SCALE + 0.5);
	}

	static final int FIX_0_298631336  =  2446;	// scaleToInt(0.298631336) 
	static final int FIX_0_390180644  =  3196;	// scaleToInt(0.390180644) 
	static final int FIX_0_541196100  =  4433;	// scaleToInt(0.541196100) 
	static final int FIX_0_765366865  =  6270;	// scaleToInt(0.765366865) 
	static final int FIX_0_899976223  =  7373;	// scaleToInt(0.899976223) 
	static final int FIX_1_175875602  =  9633;	// scaleToInt(1.175875602) 
	static final int FIX_1_501321110  =  12299;	// scaleToInt(1.501321110) 
	static final int FIX_1_847759065  =  15137;	// scaleToInt(1.847759065) 
	static final int FIX_1_961570560  =  16069;	// scaleToInt(1.961570560) 
	static final int FIX_2_053119869  =  16819;	// scaleToInt(2.053119869) 
	static final int FIX_2_562915447  =  20995;	// scaleToInt(2.562915447) 
	static final int FIX_3_072711026  =  25172;	// scaleToInt(3.072711026) 


	/* Descale and correctly round an int value that's scaled by N bits.
	 * We assume RIGHT_SHIFT rounds towards minus infinity
	 */

	public static int deScale(int f, int n)
	{
		return (int)((f + (1 << (n-1)))>> n);
	}


	/*
	 * Perform the forward DCT on one block of samples.
	 */

	public static void fwd_dct (int [] data)
	{
		int tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7;
		int tmp10, tmp11, tmp12, tmp13;
		int z1, z2, z3, z4, z5;
		int rowctr;
		int row, col;

		// Pass 1: process rows. 
		// Note results are scaled up by sqrt(8) compared to a true DCT; 
		// furthermore, we scale the results by 2**PASS1_BITS. 
		row = 0;
		for (rowctr = DCTSIZE; --rowctr >= 0; )
		{
			tmp0 = data[row+0] + data[row+7];
			tmp7 = data[row+0] - data[row+7];
			tmp1 = data[row+1] + data[row+6];
			tmp6 = data[row+1] - data[row+6];
			tmp2 = data[row+2] + data[row+5];
			tmp5 = data[row+2] - data[row+5];
			tmp3 = data[row+3] + data[row+4];
			tmp4 = data[row+3] - data[row+4];

			// Even part per LL&M figure 1 --- note that published figure is faulty;
			// rotator "sqrt(2)*c1" should be "sqrt(2)*c6".
			tmp10 = tmp0 + tmp3;
			tmp13 = tmp0 - tmp3;
			tmp11 = tmp1 + tmp2;
			tmp12 = tmp1 - tmp2;

			data[row+0] = (tmp10 + tmp11) << PASS1_BITS;
			data[row+4] = (tmp10 - tmp11) << PASS1_BITS;

			z1 = (tmp12 + tmp13) * FIX_0_541196100;
			data[row+2] = deScale(z1 + (tmp13 * FIX_0_765366865),
						   CONST_BITS-PASS1_BITS);
			data[row+6] = deScale(z1 + (tmp12 * (-1) * FIX_1_847759065),
						   CONST_BITS-PASS1_BITS);

			// Odd part per figure 8 --- note paper omits factor of sqrt(2).
			// cK represents cos(K*pi/16).
			// i0..i3 in the paper are tmp4..tmp7 here.
			z1 = tmp4 + tmp7;
			z2 = tmp5 + tmp6;
			z3 = tmp4 + tmp6;
			z4 = tmp5 + tmp7;
			z5 = (z3 + z4) * FIX_1_175875602; // sqrt(2) * c3 

			tmp4 = tmp4 * FIX_0_298631336; 		// sqrt(2) * (-c1+c3+c5-c7) 
			tmp5 = tmp5 * FIX_2_053119869; 		// sqrt(2) * ( c1+c3-c5+c7) 
			tmp6 = tmp6 * FIX_3_072711026; 		// sqrt(2) * ( c1+c3+c5-c7) 
			tmp7 = tmp7 * FIX_1_501321110; 		// sqrt(2) * ( c1+c3-c5-c7) 
			z1 = z1 * (-1) * FIX_0_899976223; // sqrt(2) * (c7-c3) 
			z2 = z2 * (-1) * FIX_2_562915447; // sqrt(2) * (-c1-c3) 
			z3 = z3 * (-1) * FIX_1_961570560; // sqrt(2) * (-c3-c5) 
			z4 = z4 * (-1) * FIX_0_390180644; // sqrt(2) * (c5-c3) 

			z3 += z5;
			z4 += z5;

			data[row+7] = deScale(tmp4 + z1 + z3, CONST_BITS-PASS1_BITS);
			data[row+5] = deScale(tmp5 + z2 + z4, CONST_BITS-PASS1_BITS);
			data[row+3] = deScale(tmp6 + z2 + z3, CONST_BITS-PASS1_BITS);
			data[row+1] = deScale(tmp7 + z1 + z4, CONST_BITS-PASS1_BITS);

			row += DCTSIZE;		// advance to next row 
	    }

		// Pass 2: process columns. 
		// Note that we must descale the results by a factor of 8 == 2**3, 
		// and also undo the PASS1_BITS scaling. 
		col = 0;
		for (rowctr = DCTSIZE; --rowctr >= 0; )
		{
			tmp0 = data[DCTSIZE*0+col] + data[DCTSIZE*7+col];
			tmp7 = data[DCTSIZE*0+col] - data[DCTSIZE*7+col];
			tmp1 = data[DCTSIZE*1+col] + data[DCTSIZE*6+col];
			tmp6 = data[DCTSIZE*1+col] - data[DCTSIZE*6+col];
			tmp2 = data[DCTSIZE*2+col] + data[DCTSIZE*5+col];
			tmp5 = data[DCTSIZE*2+col] - data[DCTSIZE*5+col];
			tmp3 = data[DCTSIZE*3+col] + data[DCTSIZE*4+col];
			tmp4 = data[DCTSIZE*3+col] - data[DCTSIZE*4+col];

			// Even part 
			tmp10 = tmp0 + tmp3;
			tmp13 = tmp0 - tmp3;
			tmp11 = tmp1 + tmp2;
			tmp12 = tmp1 - tmp2;

			data[DCTSIZE*0+col] = deScale(tmp10 + tmp11, PASS1_BITS+3);
			data[DCTSIZE*4+col] = deScale(tmp10 - tmp11, PASS1_BITS+3);

			z1 = (tmp12 + tmp13) * FIX_0_541196100;
			data[DCTSIZE*2+col] = deScale(z1 + tmp13 * FIX_0_765366865,
							   CONST_BITS+PASS1_BITS+3);
			data[DCTSIZE*6+col] = deScale(z1 + tmp12 * (-1) * FIX_1_847759065,
							   CONST_BITS+PASS1_BITS+3);

			// Odd part 
			z1 = tmp4 + tmp7;
			z2 = tmp5 + tmp6;
			z3 = tmp4 + tmp6;
			z4 = tmp5 + tmp7;
			z5 = (z3 + z4) * FIX_1_175875602; // sqrt(2) * c3 

			tmp4 = tmp4 * FIX_0_298631336; // sqrt(2) * (-c1+c3+c5-c7) 
			tmp5 = tmp5 * FIX_2_053119869; // sqrt(2) * ( c1+c3-c5+c7) 
			tmp6 = tmp6 * FIX_3_072711026; // sqrt(2) * ( c1+c3+c5-c7) 
			tmp7 = tmp7 * FIX_1_501321110; // sqrt(2) * ( c1+c3-c5-c7) 
			z1 = z1 * (-1) * FIX_0_899976223; // sqrt(2) * (c7-c3) 
			z2 = z2 * (-1) * FIX_2_562915447; // sqrt(2) * (-c1-c3) 
			z3 = z3 * (-1) * FIX_1_961570560; // sqrt(2) * (-c3-c5) 
			z4 = z4 * (-1) * FIX_0_390180644; // sqrt(2) * (c5-c3) 

			z3 += z5;
			z4 += z5;

			data[DCTSIZE*7+col] = deScale(tmp4 + z1 + z3, CONST_BITS+PASS1_BITS+3);
			data[DCTSIZE*5+col] = deScale(tmp5 + z2 + z4, CONST_BITS+PASS1_BITS+3);
			data[DCTSIZE*3+col] = deScale(tmp6 + z2 + z3, CONST_BITS+PASS1_BITS+3);
			data[DCTSIZE*1+col] = deScale(tmp7 + z1 + z4, CONST_BITS+PASS1_BITS+3);

			col++;			// advance to next column 
		}
	}
}

