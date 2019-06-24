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

        // isFizz(3)がtrueであることを確認する
        assertTrue(target.isFizz(3));
    }

    @Test
    public void isFizz002(){
        var target = new FizzBuzzResource();

        // 3の境界値
        assertFalse(target.isFizz(2));
    }

    @Test
    public void isFizz003(){
        var target = new FizzBuzzResource();

        // 3(Fizz)と5(Buzz)の境界値
        assertFalse(target.isFizz(4));
    }

    @Test
    public void isFizz004(){
        var target = new FizzBuzzResource();

        // 5(Buzz)そのもの
        assertFalse(target.isFizz(5));
    }

    @Test
    public void isFizz005(){
        var target = new FizzBuzzResource();

        // 境界値0
        assertTrue(target.isFizz(0));
    }

    @Test
    public void isFizz006(){
        var target = new FizzBuzzResource();

        // 境界値0の隣
        assertFalse(target.isFizz(1));
    }

    @Test
    public void isFizz007(){
        var target = new FizzBuzzResource();

        // 境界値0の隣
        assertFalse(target.isFizz(-1));
    }

    @Test
    public void isFizz008(){
        var target = new FizzBuzzResource();

        // 3と5両方の倍数
        assertTrue(target.isFizz(15));
    }

    @Test
    public void isFizz009(){
        var target = new FizzBuzzResource();

        // 境界値15の隣
        assertFalse(target.isFizz(14));
    }

    @Test
    public void isFizz010(){
        var target = new FizzBuzzResource();

        // 境界値16の隣
        assertFalse(target.isFizz(16));
    }

    @Test
    public void isBuzz000() {
        var target = new FizzBuzzResource();

        // 境界値0
        assertTrue(target.isBuzz(0));
    }

    @Test
    public void isBuzz001(){
        var target = new FizzBuzzResource();

        // 境界値0の隣
        assertFalse(target.isFizz(-1));
    }

    @Test
    public void isBuzz002(){
        var target = new FizzBuzzResource();

        // 境界値0の隣
        assertFalse(target.isFizz(1));
    }

    @Test
    public void isBuzz003(){
        var target = new FizzBuzzResource();

        // 境界値5の隣
        assertFalse(target.isBuzz(4));
    }

    @Test
    public void isBuzz004(){
        var target = new FizzBuzzResource();

        // 境界値
        assertTrue(target.isBuzz(5));
    }

    @Test
    public void isFizzBuzz001() {
        var target = new FizzBuzzResource();

        // 15
        assertTrue(target.isFizzBuzz(15));
    }

    @Test
    public void isFizzBuzz002(){
        var target = new FizzBuzzResource();

        assertFalse(target.isFizzBuzz(14));
    }

    @Test
    public void isFizzBuzz003(){
        var target = new FizzBuzzResource();

        assertFalse(target.isFizzBuzz(16));
    }

    @Test
    public void isFizzBuzz004(){
        var target = new FizzBuzzResource();

        assertTrue(target.isFizzBuzz(0));
    }

    @Test
    public void isFizzBuzz005(){
        var target = new FizzBuzzResource();

        assertFalse(target.isFizzBuzz(-1));
    }

    @Test
    public void isFizzBuzz006(){
        var target = new FizzBuzzResource();

        assertFalse(target.isFizzBuzz(1));
    }

    @Test
    public void isFizzBuzz007(){
        var target = new FizzBuzzResource();

        assertFalse(target.isFizzBuzz(3));
    }

    @Test
    public void isFizzBuzz008(){
        var target = new FizzBuzzResource();

        assertFalse(target.isFizzBuzz(5));
    }

    @Test
    public void fizzbuzz001() {
        var target = new FizzBuzzResource();

        assertEquals("FizzBuzz", target.fizzbuzz(0));
    }

    @Test
    public void fizzbuzz002(){
        var target = new FizzBuzzResource();

        assertEquals("-1", target.fizzbuzz(-1));
    }

    @Test
    public void fizzbuzz003(){
        var target = new FizzBuzzResource();

        assertEquals("1", target.fizzbuzz(1));
    }

    @Test
    public void fizzbuzz004(){
        var target = new FizzBuzzResource();

        assertEquals("FizzBuzz", target.fizzbuzz(15));
    }

    @Test
    public void fizzbuzz005(){
        var target = new FizzBuzzResource();

        assertEquals("14", target.fizzbuzz(14));
    }

    @Test
    public void fizzbuzz006(){
        var target = new FizzBuzzResource();

        assertEquals("16", target.fizzbuzz(16));
    }

}