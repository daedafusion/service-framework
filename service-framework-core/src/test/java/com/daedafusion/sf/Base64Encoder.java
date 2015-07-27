package com.daedafusion.sf;

/**
 * Created by mphilpot on 7/2/14.
 */
public interface Base64Encoder
{
    String encode(byte[] bytes);
    byte[] decode(String s);
}
