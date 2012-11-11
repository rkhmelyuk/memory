package com.khmelyuk.memory;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link SizeUnit}, which does some conversions between units.
 *
 * @author Ruslan Khmelyuk
 */
public class SizeUnitTest {

    @Test
    public void returnZeroIfSizeIsZero() {
        assertThat(0, is(SizeUnit.Bytes.toBytes(0)));
        assertThat(0, is(SizeUnit.KB.toBytes(0)));
        assertThat(0, is(SizeUnit.MB.toBytes(0)));
        assertThat(0, is(SizeUnit.GB.toBytes(0)));
    }

    @Test
    public void toBytesReturnCorrectValues() {
        assertThat(SizeUnit.Bytes.toBytes(10), is(10));
        assertThat(SizeUnit.KB.toBytes(10), is(1024 * 10));
        assertThat(SizeUnit.MB.toBytes(2), is(1024 * 1024 * 2));
        assertThat(SizeUnit.GB.toBytes(1), is(1024 * 1024 * 1024 * 1));
    }

    @Test
    public void fromBytesReturnsCorrectValues() {
        assertThat(SizeUnit.Bytes.fromBytes(10), is(10));
        assertThat(SizeUnit.KB.fromBytes(1024 * 10), is(10));
        assertThat(SizeUnit.MB.fromBytes(1024 * 1024 * 2), is(2));
        assertThat(SizeUnit.GB.fromBytes(1024 * 1024 * 1024 * 1), is(1));
    }

    @Test
    public void minSizeUnit() {
        assertThat(SizeUnit.min(SizeUnit.Bytes, SizeUnit.KB), is(SizeUnit.Bytes));
        assertThat(SizeUnit.min(SizeUnit.MB, SizeUnit.KB), is(SizeUnit.KB));
        assertThat(SizeUnit.min(SizeUnit.MB, SizeUnit.GB), is(SizeUnit.MB));
    }

}
