package com.practice;

import java.net.InetSocketAddress;

public class Pair {
    public InetSocketAddress first;
    public String second;

    public Pair(InetSocketAddress first, String second)
    {
        this.first = first;
        this.second = second;
    }
}
