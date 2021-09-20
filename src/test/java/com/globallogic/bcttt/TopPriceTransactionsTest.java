package com.globallogic.bcttt;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TopPriceTransactionsTest {

    @Test
    public void test() {
        TopPriceTransactions topPrices = new TopPriceTransactions(2);

        assertTrue(topPrices.add(10.0, "a"));
        List<String[]> output = split(topPrices.toString());
        assertEquals(2, output.size());
        assertEquals("10.00", output.get(1)[0]);
        assertEquals("a", output.get(1)[1]);

        assertTrue(topPrices.add(20.0, "b"));
        output = split(topPrices.toString());
        assertEquals(3, output.size());
        assertEquals("20.00", output.get(1)[0]);
        assertEquals("b", output.get(1)[1]);
        assertEquals("10.00", output.get(2)[0]);
        assertEquals("a", output.get(2)[1]);

        assertTrue(topPrices.add(20.0, "c"));
        output = split(topPrices.toString());
        assertEquals(3, output.size());
        assertEquals("20.00", output.get(1)[0]);
        assertEquals("c", output.get(1)[1]);
        assertEquals("10.00", output.get(2)[0]);
        assertEquals("a", output.get(2)[1]);

        assertTrue(topPrices.add(30.0, "d"));
        output = split(topPrices.toString());
        assertEquals(3, output.size());
        assertEquals("30.00", output.get(1)[0]);
        assertEquals("d", output.get(1)[1]);
        assertEquals("20.00", output.get(2)[0]);
        assertEquals("c", output.get(2)[1]);

        assertFalse(topPrices.add(5.0, "e"));
        output = split(topPrices.toString());
        assertEquals(3, output.size());
        assertEquals("30.00", output.get(1)[0]);
        assertEquals("d", output.get(1)[1]);
        assertEquals("20.00", output.get(2)[0]);
        assertEquals("c", output.get(2)[1]);
    }

    private List<String[]> split(String text) {
        String[] lines = text.trim().split("\n");
        return Arrays.stream(lines).map(line -> line.trim().split("\\s+"))
                .collect(Collectors.toList());
    }
}
