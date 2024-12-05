/**
 * <b>drac</b> - Java client for controlling <a href="https://dorna.ai/">Dorna</a> robotic arm.
 * 
 * <p>NOTE: This is unofficial Dorna Java client. For official Dorna software follow <a href="https://dorna.ai">dorna.ai</a>
 * 
 * <p>Interaction with Dorna robots is done by communicating with Command Server over a websocket.
 *  
 * <p>Available features:
 * <ul>
 * <li>Configuration of moving velocity, acceleration, and jerk through {@link pinorobotics.drac.DornaClientConfig.Builder}
 * <li>Support for asynchronous move commands and caching of last serialized messages (see {@link pinorobotics.drac.DornaClient#jmove(Joints, boolean, boolean, double, double, double)})
 * <li>Integration with <a href="https://opentelemetry.io/">OpenTelemetry</a> for metrics tracking and performance analysis (see {@link pinorobotics.drac.metrics.DracMetrics})
 * <li>Verification of joint limits before each move command to prevent potential damage.
 * <li>Support for different Dorna models (see {@link pinorobotics.drac.DornaRobotModel})
 * <li>Safety warnings before turning off the motor (see {@link pinorobotics.drac.DornaClientConfig.Builder#confirmMotorShutOff})
 * <li>Recording of all commands sent to the Dorna arm in a log file (see {@link pinorobotics.drac.DornaClientConfig.Builder#outputLog(Path)})
 * <li>Implementation of various predefined Dorna command statuses (see {@link pinorobotics.drac.CommandStatus.Predefined})
 * </ul>
 * 
 * <b>drac</b> may be helpful for those working with Dorna robots in research, manufacturing, or other relevant fields.
 * 
 * <h2>Example</h2>
 * 
 * {@snippet lang="java" :
var restPosition = new Joints(179.94375, 118.206, -77.506, 96.01875, -0.73125);
var configBuilder =
        new DornaClientConfig.Builder(
                URI.create("ws://dorna:443"), DornaRobotModel.DORNA2_BLACK);
try (var client = new DornaClientFactory().createClient(configBuilder.build())) {
    var motion = client.getLastMotion();
    System.out.println("Last motion message: " + motion);
    
    System.out.println("Press Enter to turn on the motors");
    System.in.read();
    client.motor(true);
    
    System.out.println("Press Enter to move arm in the rest position");
    System.in.read();
    client.jmove(restPosition, false);
}
 * }
 * 
 * @see <a href="http://pinoweb.freetzi.com/drac">Documentation</a>
 * @see <a
 *     href="https://github.com/pinorobotics/drac/blob/main/drac/release/CHANGELOG.md">Releases</a>
 * @see <a href="https://github.com/pinorobotics/drac">GitHub repository</a>
 * @author lambdaprime intid@protonma
 */
module drac {
    exports pinorobotics.drac;
    exports pinorobotics.drac.messages;
    exports pinorobotics.drac.metrics;
    exports pinorobotics.drac.exceptions;
    exports pinorobotics.drac.impl to
            drac.tests;

    requires id.xfunction;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires io.opentelemetry.api;
}
