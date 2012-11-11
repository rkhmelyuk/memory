package com.khmelyuk.memory;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link MemorySize}
 *
 * @author Ruslan Khmelyuk
 */
public class MemorySizeTest {

    @Test
    public void factoryMethodsWorkCorrectly() {
        assertSizeAndUnit(MemorySize.bytes(123), 123, SizeUnit.Bytes);
        assertSizeAndUnit(MemorySize.kilobytes(124), 124, SizeUnit.KB);
        assertSizeAndUnit(MemorySize.megabytes(153), 153, SizeUnit.MB);
        assertSizeAndUnit(MemorySize.gigabytes(153), 153, SizeUnit.GB);
    }

    @Test
    public void createMemorySize() {
        MemorySize size = MemorySize.kilobytes(10);

        assertThat(size.getSize(), is(10));
        assertThat(size.getUnit(), is(SizeUnit.KB));
    }

    @Test
    public void convertToBytes() {
        MemorySize kbs = MemorySize.kilobytes(10);
        MemorySize bytes = kbs.convertTo(SizeUnit.Bytes);

        assertThat(bytes.getSize(), is(10 * 1024));
        assertThat(bytes.getUnit(), is(SizeUnit.Bytes));
    }

    @Test
    public void convertToKB() {
        MemorySize bytes = MemorySize.bytes(2000);
        MemorySize kbs = bytes.convertTo(SizeUnit.KB);

        assertThat(kbs.getSize(), is(2));
        assertThat(kbs.getUnit(), is(SizeUnit.KB));
    }

    @Test
    public void convertToMB() {
        MemorySize kbs = MemorySize.kilobytes(2000);
        MemorySize mbs = kbs.convertTo(SizeUnit.MB);

        assertThat(mbs.getSize(), is(2));
        assertThat(mbs.getUnit(), is(SizeUnit.MB));
    }

    @Test
    public void convertToGB() {
        MemorySize kbs = MemorySize.megabytes(2000);
        MemorySize mbs = kbs.convertTo(SizeUnit.GB);

        assertThat(mbs.getSize(), is(2));
        assertThat(mbs.getUnit(), is(SizeUnit.GB));
    }

    @Test
    public void addSize() {
        MemorySize memSize1 = MemorySize.bytes(20);
        MemorySize memSize2 = MemorySize.kilobytes(1);

        MemorySize result = memSize1.add(memSize2);

        assertThat(result.getSize(), is(1044));
        assertThat(result.getBytes(), is(1044));
        assertThat(result.getUnit(), is(SizeUnit.Bytes));
    }

    @Test
    public void addSizeReturnsAlwaysInowestUnit() {
        MemorySize result;

        result = MemorySize.bytes(20).add(MemorySize.megabytes(1));
        assertThat(result.getUnit(), is(SizeUnit.Bytes));

        result = MemorySize.kilobytes(20).add(MemorySize.megabytes(1));
        assertThat(result.getUnit(), is(SizeUnit.KB));

        result = MemorySize.megabytes(20).add(MemorySize.megabytes(1));
        assertThat(result.getUnit(), is(SizeUnit.MB));
    }

    @Test
    public void subtractSize() {
        MemorySize memSize1 = MemorySize.kilobytes(1);
        MemorySize memSize2 = MemorySize.bytes(20);

        MemorySize result = memSize1.subtract(memSize2);

        assertThat(result.getSize(), is(1004));
        assertThat(result.getBytes(), is(1004));
        assertThat(result.getUnit(), is(SizeUnit.Bytes));
    }

    @Test
    public void subtractSizeReturnsAlwaysInLowestUnit() {
        MemorySize result;

        result = MemorySize.bytes(20).add(MemorySize.megabytes(1));
        assertThat(result.getUnit(), is(SizeUnit.Bytes));

        result = MemorySize.kilobytes(20).add(MemorySize.megabytes(1));
        assertThat(result.getUnit(), is(SizeUnit.KB));

        result = MemorySize.megabytes(20).add(MemorySize.megabytes(1));
        assertThat(result.getUnit(), is(SizeUnit.MB));
    }

    @Test
    public void equalWhenNumberOfBytesTheSame() {
        MemorySize mem1 = MemorySize.bytes(1024);
        MemorySize mem2 = MemorySize.bytes(1024);

        assertThat(mem1, equalTo(mem2));
    }

    @Test
    public void equalIgnoresUnit() {
        MemorySize mem1 = MemorySize.bytes(1024);
        MemorySize mem2 = MemorySize.kilobytes(1);

        assertThat(mem1, equalTo(mem2));
    }

    @Test
    public void ZEROConstantHasZeroSize() {
        assertThat(MemorySize.ZERO.getSize(), is(0));
        assertThat(MemorySize.ZERO.getBytes(), is(0));
    }

    private void assertSizeAndUnit(MemorySize memorySize, int size, SizeUnit unit) {
        assertThat(memorySize.getSize(), is(size));
        assertThat(memorySize.getUnit(), is(unit));
    }
}
