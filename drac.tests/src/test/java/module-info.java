/**
 * @author lambdaprime intid@protonmail.com
 */
module drac.tests {
    exports pinorobotics.drac.tests;

    requires org.junit.jupiter.api;
    requires drac;
    requires transitive java.net.http;
    requires id.xfunction;
}
