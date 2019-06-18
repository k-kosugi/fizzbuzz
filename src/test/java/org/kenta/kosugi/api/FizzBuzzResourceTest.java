package org.kenta.kosugi.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FizzBuzzResourceTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isFizz001() {
        var target = new FizzBuzzResource();
        assertTrue(target.isFizz(3));
    }

    @Test
    public void isBuzz() {
    }

    @Test
    public void isFizzBuzz() {
    }

    @Test
    public void fizzbuzz() {
    }
}