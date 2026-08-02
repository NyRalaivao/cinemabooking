package com.cinema.depo.file.hash;

import com.cinema.depo.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
