package de.tudortmund.cs.iltis.folalib.transform;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConstrainedSupplierTest {

    @Test
    public void testExcelColumnNameSupplier() {
        ConstrainedSupplier<String> supplier =
                ConstrainedSupplier.constrainedExcelColumnNameSupplier();
        assertEquals("A", supplier.get());
        assertEquals("B", supplier.get());
        assertEquals("C", supplier.get());
        assertEquals("D", supplier.get());
        assertEquals("E", supplier.get());
        for (char c = 'F'; c <= 'Z'; ++c) {
            supplier.get();
        }
        assertEquals("AA", supplier.get());
        assertEquals("AB", supplier.get());
        assertEquals("AC", supplier.get());
        for (char c = 'D'; c <= 'Z'; ++c) {
            supplier.get();
        }
        assertEquals("BA", supplier.get());
        assertEquals("BB", supplier.get());
    }

    @Test
    public void testExcelColumnSupplierWithConstrainedOutputs() {
        ConstrainedSupplier<String> supplier =
                ConstrainedSupplier.constrainedExcelColumnNameSupplier();
        supplier.constrain("S");
        assertEquals("A", supplier.get());
        assertEquals("B", supplier.get());
        assertEquals("C", supplier.get());
        for (char c = 'D'; c <= 'R'; ++c) {
            supplier.get();
        }
        // output "S" is skipped
        assertEquals("T", supplier.get());
        assertEquals("U", supplier.get());
    }
}
