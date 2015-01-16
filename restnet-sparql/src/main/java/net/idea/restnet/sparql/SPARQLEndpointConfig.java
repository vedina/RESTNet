package net.idea.restnet.sparql;

public enum SPARQLEndpointConfig {
    // tdb or sdb
    persistence,
    // where to store TDB files. Default is temp directory
    tdb
}

enum Persistence {
    tdb, sdb
}