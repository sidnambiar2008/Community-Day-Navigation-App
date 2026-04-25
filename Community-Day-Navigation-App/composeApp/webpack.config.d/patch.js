if (config.resolve) {
    config.resolve.alias = {
        ...config.resolve.alias,
        "bufferutil": false,
        "utf-8-validate": false
    };

    config.resolve.fallback = {
        ...config.resolve.fallback,
        "zlib": false,
        "stream": false,
        "crypto": false,
        "fs": false,
        "net": false,
        "tls": false,
        "os": false,
        "path": false,
        "buffer": false,
        "util": false,
        "url": false,
        "assert": false,
        "http": false,
        "https": false,
        "child_process": false,
        "process": false
    };
}