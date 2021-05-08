// increases performance
module matt.kjlib {

    requires kotlin.stdlib.jdk8;
    requires kotlin.stdlib.jdk7;
    requires kotlin.reflect;

    requires transitive matt.klibexport;
    requires transitive matt.reflect;

    requires java.desktop;

    exports matt.kjlib;
//    opens matt.kjlib to com.google.gson;
}