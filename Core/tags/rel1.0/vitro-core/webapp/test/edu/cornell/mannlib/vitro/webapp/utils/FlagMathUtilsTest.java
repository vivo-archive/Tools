/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package edu.cornell.mannlib.vitro.webapp.utils;

import static edu.cornell.mannlib.vitro.webapp.utils.FlagMathUtils.bits2Numeric;
import static edu.cornell.mannlib.vitro.webapp.utils.FlagMathUtils.numeric2Portalid;
import static edu.cornell.mannlib.vitro.webapp.utils.FlagMathUtils.numeric2numerics;
import static edu.cornell.mannlib.vitro.webapp.utils.FlagMathUtils.portalId2Numeric;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import edu.cornell.mannlib.vitro.testing.AbstractTestClass;

/**
 * @author jeb228
 */
public class FlagMathUtilsTest extends AbstractTestClass {

	@Test
	public void testBits2Num() {
		boolean[] bits = { false, false, false, false };
		assertEquals(0, bits2Numeric(bits));

		boolean[] bits2 = { true, false, false, false };
		assertEquals(1, bits2Numeric(bits2));

		boolean[] bits3 = { true, true, false, false };
		assertEquals(3, bits2Numeric(bits3));

		boolean[] bits4 = { true, false, false, true };
		assertEquals(1 + 8, bits2Numeric(bits4));

		boolean[] bits5 = { false, false, false, true };
		assertEquals(8, bits2Numeric(bits5));

		boolean[] bits6 = { true, true, true, true };
		assertEquals(1 + 2 + 4 + 8, bits2Numeric(bits6));
	}

	@Test
	public void testNumeric2numerics() {
		Long[] num0 = { new Long(1), new Long(2), new Long(4), new Long(8) };
		assertTrue(Arrays.equals(num0, numeric2numerics(1 + 2 + 4 + 8)));

		Long[] num1 = { new Long(1) };
		assertTrue(Arrays.equals(num1, numeric2numerics(1)));

		Long[] num2 = {};
		assertTrue(Arrays.equals(num2, numeric2numerics(0)));

		Long[] num3 = { new Long(1), new Long(8) };
		assertTrue(Arrays.equals(num3, numeric2numerics(1 + 8)));

		Long[] num4 = { new Long(4), new Long(8) };
		assertTrue(Arrays.equals(num4, numeric2numerics(4 + 8)));

		Long[] num5 = { new Long(8) };
		assertTrue(Arrays.equals(num5, numeric2numerics(8)));

		Long[] num6 = { new Long(2), new Long(4) };
		assertTrue(Arrays.equals(num6, numeric2numerics(2 + 4)));
	}

	@Test
	public void testNumeric2Portalid() {
		assertEquals(0, numeric2Portalid(1));
		assertEquals(1, numeric2Portalid(2));
		assertEquals(2, numeric2Portalid(4));
		assertEquals(3, numeric2Portalid(8));
		assertEquals(4, numeric2Portalid(16));
		assertEquals(5, numeric2Portalid(32));
		assertEquals(6, numeric2Portalid(64));
		assertEquals(7, numeric2Portalid(128));
		assertEquals(8, numeric2Portalid(256));

		// make sure we throw errors on bad inputs
		try {
			numeric2Portalid(0);
			fail("should have thrown Error");
		} catch (Throwable e) {
		}
		try {
			numeric2Portalid(3);
			fail("should have thrown Error");
		} catch (Throwable e) {
		}
		try {
			numeric2Portalid(15);
			fail("should have thrown Error");
		} catch (Throwable e) {
		}
		try {
			numeric2Portalid(21);
			fail("should have thrown Error");
		} catch (Throwable e) {
		}
	}

	@Test
	public void testPortalId2Num() {
		assertEquals(2, portalId2Numeric(1));
		assertEquals(4, portalId2Numeric(2));
		assertEquals(8, portalId2Numeric(3));
		assertEquals(16, portalId2Numeric(4));
		assertEquals(32, portalId2Numeric(5));
		assertEquals(64, portalId2Numeric(6));
		assertEquals(128, portalId2Numeric(7));
	}

	@Test
	public void testBackAndForth() {
		for (long i = 1; i < Long.SIZE - 1; i++) {
			long num = portalId2Numeric(i);
			int portal = numeric2Portalid(num);
			assertEquals(i, portal);
		}
	}

}
