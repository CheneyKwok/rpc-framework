package github.cheneykwok.compress;

import github.cheneykwok.extension.SPI;

@SPI
public interface Compress {

    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
