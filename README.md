# Requirements
* Java 8
* Maven 3.3.9+

# Test
* mvn test

# Build
* mvn clean package

# Examples
* com.neil.example.MultipleServicesExample
* com.neil.example.MultipleServicesWithGraceExample
* com.neil.example.MultipleServicesWithOutageExample

# Main classes
* com.neil.MonitorService
  <p>Major entry class.</p>

* com.neil.TCPConnectionTask
  <p>It tries to establish a TCP connection to a service.</p>

* com.neil.TCPConnectionStatusListener
  <p>Represents a client's interest in a service.</p>

* com.neil.TCPConnectionMonitor
  <p>One instance per service. It takes interests(TCPConnectionStatusListener) from TCPConnectionTaskStatusListenerScheduler. Main loop: Every second it checks if there is interests, if yes, run a TCPConnectionTask on a separate thread. During the execution of TCPConnectionTask, any new interests are saved. After the execution of TCPConnectionTask, on the same thread, it cleans up all stored interests and send execution result to interests.</p>

* com.neil.TCPConnectionTaskStatusListenerScheduler
  <p>One instance per service. It takes interests(TCPConnectionStatusListener) from client, schedules them for future execution based on frequency. When a scheduled interest fires, it send interest to TCPConnectionMonitor. Also when a grace time expires, it checks if there is any interests whose frequency are great than grace time on a separate thread, if yes, asks TCPConnectionMonitor to do a immediate check.</p>

# Status
* Features as specified in the assignment are all completed
* Unit tests still needs improvements
