package org.kenta.kosugi.api;

import org.junit.Test;

import static org.junit.Assert.*;

public class FizzBuzzResourceTest {

    @Test
    public void testIsFizz001() {
        var test = new FizzBuzzResource();

        // 3は3の倍数なのでisFizz()はtrue
        assertTrue(test.isFizz(3));
    }

    @Test
    public void testIsFizz002() {
        var test = new FizzBuzzResource();

        // 15は3の倍数なのでisFizz()はtrue
        assertTrue(test.isFizz(15));
    }

    @Test
    public void testIsFizz003() {
        var test = new FizzBuzzResource();

        // 0は3の倍数(0倍)なのでisFizz()はtrue
        assertTrue(test.isFizz(0));
    }

    @Test
    public void testIsFizz004(){
        var test = new FizzBuzzResource();

        // 1は3の倍数ではないのでisFizz()はfalse
        assertFalse(test.isFizz(1));
    }

    @Test
    public void testIsFizz005(){
        var test = new FizzBuzzResource();

        // 5は3の倍数ではないのでisFizz()はfalse
        assertFalse(test.isFizz(5));
    }

    @Test
    public void testIsFizz006(){
        var test = new FizzBuzzResource();

        // -1は3の倍数ではないのでisFizz()はfalse
        assertFalse(test.isFizz(-1));
    }

    @Test
    public void testIsFizz007(){
        var test = new FizzBuzzResource();

        // 14は3の倍数ではないのでisFizz()はfalse
        assertFalse(test.isFizz(14));
    }

    @Test
    public void testIsFizz008(){
        var test = new FizzBuzzResource();

        // 16は3の倍数ではないのでisFizz()はfalse
        assertFalse(test.isFizz(16));
    }

    @Test
    public void testIsBuzz001(){
        var test = new FizzBuzzResource();

        // 5は5の倍数なのでisBuzz()はtrue
        assertTrue(test.isBuzz(5));
    }

    @Test
    public void testIsBuzz002(){
        var test = new FizzBuzzResource();

        // 0は5の倍数なのでisBuzz()はtrue
        assertTrue(test.isBuzz(0));
    }


    @Test
    public void testIsBuzz003(){
        var test = new FizzBuzzResource();

        // 15は5の倍数なのでisBuzz()はtrue
        assertTrue(test.isBuzz(15));
    }

    @Test
    public void testIsBuzz004(){
        var test = new FizzBuzzResource();

        // -1は5の倍数ではないのでisBuzz()はfalse
        assertFalse(test.isBuzz(-1));
    }

    @Test
    public void testIsBuzz005(){
        var test = new FizzBuzzResource();

        // 4は5の倍数ではないのでisBuzz()はfalse
        assertFalse(test.isBuzz(4));
    }

    @Test
    public void testIsBuzz006(){
        var test = new FizzBuzzResource();

        // 6は5の倍数ではないのでisBuzz()はfalse
        assertFalse(test.isBuzz(6));
    }


    @Test
    public void testIsBuzz007(){
        var test = new FizzBuzzResource();

        // 14は5の倍数ではないのでisBuzz()はfalse
        assertFalse(test.isBuzz(14));
    }

    @Test
    public void testIsBuzz008(){
        var test = new FizzBuzzResource();

        // 16は5の倍数ではないのでisBuzz()はfalse
        assertFalse(test.isBuzz(16));
    }

    @Test
    public void testIsFizzBuzz001(){
        var test = new FizzBuzzResource();

        // 0は3と5の倍数なのでisFizzBuzz()はtrue
        assertTrue(test.isFizzBuzz(0));
    }

    @Test
    public void testIsFizzBuzz002(){
        var test = new FizzBuzzResource();

        // 15は3と5の倍数なのでisFizzBuzz()はtrue
        assertTrue(test.isFizzBuzz(15));
    }

    @Test
    public void testIsFizzBuzz003(){
        var test = new FizzBuzzResource();

        // -15は3と5の倍数なのでisFizzBuzz()はtrue
        assertTrue(test.isFizzBuzz(-15));
    }
}