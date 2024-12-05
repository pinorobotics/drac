/*
 * Copyright 2024 drac project
 * 
 * Website: https://github.com/pinorobotics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pinorobotics.drac.metrics;

/**
 * Complete list of metrics emitted by <b>drac</b>
 *
 * <p><b>drac</b> metrics are integrated with <a href="https://opentelemetry.io/">OpenTelemetry</a>.
 *
 * <p>To receive OpenTelemetry metrics users suppose to configure OpenTelemetry exporter. List of
 * Java exporters can be found in <a
 * href="https://opentelemetry.io/ecosystem/registry/?language=java&component=exporter">OpenTelemetry
 * registry</a> or in <a
 * href="https://github.com/lambdaprime/opentelemetry-exporters-pack">opentelemetry-exporters-pack</a>
 *
 * <p>Example of Elasticsearch dashboard (with exporter from opentelemetry-exporters-pack):
 *
 * <p><img alt="" src="doc-files/elasticsearch.png"/>
 *
 * @author lambdaprime intid@protonmail.com
 */
public interface DracMetrics {

    String MOTIO1N_MESSAGE_COUNT_METRIC = "motion_total";
    String MOTION_MESSAGE_COUNT_METRIC_DESCRIPTION = "Total number of motion messages received";

    String SENT_BYTES_COUNT_METRIC = "bytes_sent_total";
    String SENT_BYTES_COUNT_METRIC_DESCRIPTION = "Total number of sent bytes";

    String VERSION_COUNT_METRIC = "version_total";
    String VERSION_COUNT_METRIC_DESCRIPTION = "Total number of version operations";

    String VERSION_FAILED_COUNT_METRIC = "version_failed_total";
    String VERSION_FAILED_COUNT_METRIC_DESCRIPTION = "Total number of failed version operations";

    String VERSION_TIME_METRIC = "version_time_ms";
    String VERSION_TIME_METRIC_DESCRIPTION = "Version operation in millis";

    String JOINT_COUNT_METRIC = "joint_total";
    String JOINT_COUNT_METRIC_DESCRIPTION = "Total number of joint operations";

    String JOINT_FAILED_COUNT_METRIC = "joint_failed_total";
    String JOINT_FAILED_COUNT_METRIC_DESCRIPTION = "Total number of failed joint operations";

    String JOINT_TIME_METRIC = "joint_time_ms";
    String JOINT_TIME_METRIC_DESCRIPTION = "Joint operation in millis";

    String JMOVE_COUNT_METRIC = "jmove_total";
    String JMOVE_COUNT_METRIC_DESCRIPTION = "Total number of jmove operations";

    String JMOVE_FAILED_COUNT_METRIC = "jmove_failed_total";
    String JMOVE_FAILED_COUNT_METRIC_DESCRIPTION = "Total number of failed jmove operations";

    String JMOVE_TIME_METRIC = "jmove_time_ms";
    String JMOVE_TIME_METRIC_DESCRIPTION = "Jmove operation in millis";

    String MOTOR_COUNT_METRIC = "motor_total";
    String MOTOR_COUNT_METRIC_DESCRIPTION = "Total number of motor operations";

    String MOTOR_FAILED_COUNT_METRIC = "motor_failed_total";
    String MOTOR_FAILED_COUNT_METRIC_DESCRIPTION = "Total number of failed motor operations";

    String MOTOR_TIME_METRIC = "motor_time_ms";
    String MOTOR_TIME_METRIC_DESCRIPTION = "Motor operation in millis";

    String PLAY_COUNT_METRIC = "play_total";
    String PLAY_COUNT_METRIC_DESCRIPTION = "Total number of play operations";

    String PLAY_FAILED_COUNT_METRIC = "play_failed_total";
    String PLAY_FAILED_COUNT_METRIC_DESCRIPTION = "Total number of failed play operations";

    String PLAY_TIME_METRIC = "play_time_ms";
    String PLAY_TIME_METRIC_DESCRIPTION = "Play operation in millis";
}
