/**
 * @author lambdaprime intid@protonmail.com
 */
module drac {
    exports pinorobotics.drac;
    exports pinorobotics.drac.messages;
    exports pinorobotics.drac.impl to
            drac.tests;

    requires id.xfunction;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
}
