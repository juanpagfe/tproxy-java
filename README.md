# TProxy-Java
Transparent proxy for TCP and UDP traffic.

## Code Dependencies
- Apache commons-cli:1.4
- SLF4J:1.7.25

## Compile
```
mvn clean compile assembly:single 
```
Creates a ```target``` directory with a file called ```tproxy-jar-with-dependencies.jar```

## Run
```
java -jar tproxy-jar-with-dependencies.jar --help
usage: java -jar tproxy-java <OPTIONS>

where options include:
 -a,--address <arg>       Destination address (Example: 172.24.23.22:80
 -b,--bind <arg>          Bind address (Example: 0.0.0.0:2323).  Default
                          value is /0.0.0.0:0
 -h,--help                Help
 -p,--protocol <arg>      Transmission protocol (udp, tcp).  Default value
                          is TCP
 -s,--buffer-size <arg>   Default buffer size for sockets.  Default value
                          is 524288
```

## TCP Tests (loopback interface)

### iperf3 without tproxy-java
#### Server side
```
-----------------------------------------------------------
Server listening on 5201
-----------------------------------------------------------
Accepted connection from 127.0.0.1, port 41458
[  5] local 127.0.0.1 port 5201 connected to 127.0.0.1 port 41460
[ ID] Interval           Transfer     Bitrate
[  5]   0.00-1.00   sec  7.04 GBytes  60.5 Gbits/sec                  
[  5]   1.00-2.00   sec  7.09 GBytes  60.9 Gbits/sec                  
[  5]   2.00-3.00   sec  6.98 GBytes  60.0 Gbits/sec                  
[  5]   3.00-4.00   sec  7.19 GBytes  61.8 Gbits/sec                  
[  5]   4.00-5.00   sec  6.78 GBytes  58.3 Gbits/sec                  
[  5]   5.00-6.00   sec  6.71 GBytes  57.6 Gbits/sec                  
[  5]   6.00-7.00   sec  6.74 GBytes  57.9 Gbits/sec                  
[  5]   7.00-8.00   sec  6.72 GBytes  57.7 Gbits/sec                  
[  5]   8.00-9.00   sec  7.04 GBytes  60.5 Gbits/sec                  
[  5]   9.00-10.00  sec  7.04 GBytes  60.5 Gbits/sec                  
[  5]  10.00-10.00  sec   640 KBytes  56.4 Gbits/sec                  
- - - - - - - - - - - - - - - - - - - - - - - - -
[ ID] Interval           Transfer     Bitrate
[  5]   0.00-10.00  sec  69.3 GBytes  59.6 Gbits/sec                  receiver
```

#### Client side
```
iperf3 -c 127.0.0.1  -p 5201                                                                                                                                                                    master
Connecting to host 127.0.0.1, port 5201
[  5] local 127.0.0.1 port 41460 connected to 127.0.0.1 port 5201
[ ID] Interval           Transfer     Bitrate         Retr  Cwnd
[  5]   0.00-1.00   sec  7.04 GBytes  60.5 Gbits/sec    0   1.69 MBytes       
[  5]   1.00-2.00   sec  7.09 GBytes  60.9 Gbits/sec    0   1.81 MBytes       
[  5]   2.00-3.00   sec  6.98 GBytes  60.0 Gbits/sec    3   2.25 MBytes       
[  5]   3.00-4.00   sec  7.19 GBytes  61.8 Gbits/sec    7   2.75 MBytes       
[  5]   4.00-5.00   sec  6.78 GBytes  58.3 Gbits/sec    0   2.87 MBytes       
[  5]   5.00-6.00   sec  6.71 GBytes  57.6 Gbits/sec    3   3.12 MBytes       
[  5]   6.00-7.00   sec  6.74 GBytes  57.9 Gbits/sec    0   3.12 MBytes       
[  5]   7.00-8.00   sec  6.72 GBytes  57.7 Gbits/sec    0   3.12 MBytes       
[  5]   8.00-9.00   sec  7.04 GBytes  60.5 Gbits/sec    3   3.12 MBytes       
[  5]   9.00-10.00  sec  7.04 GBytes  60.5 Gbits/sec    0   3.12 MBytes       
- - - - - - - - - - - - - - - - - - - - - - - - -
[ ID] Interval           Transfer     Bitrate         Retr
[  5]   0.00-10.00  sec  69.3 GBytes  59.6 Gbits/sec   16             sender
[  5]   0.00-10.00  sec  69.3 GBytes  59.6 Gbits/sec                  receiver

iperf Done.
```

### iperf3 using tproxy-java
#### Server side
```
-----------------------------------------------------------
Server listening on 5201
-----------------------------------------------------------
Accepted connection from 127.0.0.1, port 41476
[  5] local 127.0.0.1 port 5201 connected to 127.0.0.1 port 41480
[ ID] Interval           Transfer     Bitrate
[  5]   0.00-1.00   sec  2.90 GBytes  24.9 Gbits/sec                  
[  5]   1.00-2.00   sec  3.74 GBytes  32.1 Gbits/sec                  
[  5]   2.00-3.00   sec  3.70 GBytes  31.8 Gbits/sec                  
[  5]   3.00-4.00   sec  4.29 GBytes  36.8 Gbits/sec                  
[  5]   4.00-5.00   sec  3.68 GBytes  31.6 Gbits/sec                  
[  5]   5.00-6.00   sec  4.02 GBytes  34.5 Gbits/sec                  
[  5]   6.00-7.00   sec  4.13 GBytes  35.5 Gbits/sec                  
[  5]   7.00-8.00   sec  3.80 GBytes  32.7 Gbits/sec                  
[  5]   8.00-9.00   sec  3.93 GBytes  33.7 Gbits/sec                  
[  5]   9.00-10.00  sec  4.24 GBytes  36.5 Gbits/sec                  
[  5]  10.00-10.00  sec  1.72 MBytes  31.9 Gbits/sec                  
- - - - - - - - - - - - - - - - - - - - - - - - -
[ ID] Interval           Transfer     Bitrate
[  5]   0.00-10.00  sec  38.4 GBytes  33.0 Gbits/sec                  receiver
```

#### Client side
```
iperf3 -c 127.0.0.1  -p 2323                                                                                                                                                                    master
Connecting to host 127.0.0.1, port 2323
[  5] local 127.0.0.1 port 44580 connected to 127.0.0.1 port 2323
[ ID] Interval           Transfer     Bitrate         Retr  Cwnd
[  5]   0.00-1.00   sec  3.08 GBytes  26.4 Gbits/sec    1    320 KBytes       
[  5]   1.00-2.00   sec  3.74 GBytes  32.2 Gbits/sec    0    320 KBytes       
[  5]   2.00-3.00   sec  3.71 GBytes  31.9 Gbits/sec    0    320 KBytes       
[  5]   3.00-4.00   sec  4.29 GBytes  36.9 Gbits/sec    0    320 KBytes       
[  5]   4.00-5.00   sec  3.76 GBytes  32.3 Gbits/sec    1    320 KBytes       
[  5]   5.00-6.00   sec  4.04 GBytes  34.7 Gbits/sec    0    320 KBytes       
[  5]   6.00-7.00   sec  4.14 GBytes  35.5 Gbits/sec    0    320 KBytes       
[  5]   7.00-8.00   sec  3.86 GBytes  33.2 Gbits/sec    1    320 KBytes       
[  5]   8.00-9.00   sec  3.94 GBytes  33.8 Gbits/sec    0    320 KBytes       
[  5]   9.00-10.00  sec  4.25 GBytes  36.5 Gbits/sec    0    320 KBytes       
- - - - - - - - - - - - - - - - - - - - - - - - -
[ ID] Interval           Transfer     Bitrate         Retr
[  5]   0.00-10.00  sec  38.8 GBytes  33.3 Gbits/sec    3             sender
[  5]   0.00-10.00  sec  38.4 GBytes  33.0 Gbits/sec                  receiver

iperf Done.
```

## UDP Tests (loopback interface)

### iperf without tproxy-java
#### Server side
```
iperf -s -u                  
------------------------------------------------------------
Server listening on UDP port 5001
UDP buffer size:  208 KByte (default)
------------------------------------------------------------
[  1] local 127.0.0.1 port 5001 connected with 127.0.0.1 port 33578
[ ID] Interval       Transfer     Bandwidth        Jitter   Lost/Total Datagrams
[  1] 0.0000-10.0149 sec  1.25 MBytes  1.05 Mbits/sec   0.005 ms 0/895 (0%)
```

#### Client side
```
iperf -c 127.0.0.1 -p 5001 -u                                                                                                                                                                   master
------------------------------------------------------------
Client connecting to 127.0.0.1, UDP port 5001
Sending 1470 byte datagrams, IPG target: 11215.21 us (kalman adjust)
UDP buffer size:  208 KByte (default)
------------------------------------------------------------
[  1] local 127.0.0.1 port 33578 connected with 127.0.0.1 port 5001
[ ID] Interval       Transfer     Bandwidth
[  1] 0.0000-10.0154 sec  1.25 MBytes  1.05 Mbits/sec
[  1] Sent 896 datagrams
[  1] Server Report:
[ ID] Interval       Transfer     Bandwidth        Jitter   Lost/Total Datagrams
[  1] 0.0000-10.0149 sec  1.25 MBytes  1.05 Mbits/sec   0.004 ms 0/895 (0%)
```

### iperf using tproxy-java
#### Server side
```
iperf -s -u                  
------------------------------------------------------------
Server listening on UDP port 5001
UDP buffer size:  208 KByte (default)
------------------------------------------------------------
[  1] local 127.0.0.1 port 5001 connected with 127.0.0.1 port 47550
[ ID] Interval       Transfer     Bandwidth        Jitter   Lost/Total Datagrams
[  1] 0.0000-10.0140 sec  1.25 MBytes  1.05 Mbits/sec   0.054 ms 0/895 (0%)
```

#### Client side
```
iperf -c 127.0.0.1 -p 2323 -u                                                                                                                                                                   master
------------------------------------------------------------
Client connecting to 127.0.0.1, UDP port 2323
Sending 1470 byte datagrams, IPG target: 11215.21 us (kalman adjust)
UDP buffer size:  208 KByte (default)
------------------------------------------------------------
[  1] local 127.0.0.1 port 34140 connected with 127.0.0.1 port 2323
[ ID] Interval       Transfer     Bandwidth
[  1] 0.0000-10.0153 sec  1.25 MBytes  1.05 Mbits/sec
[  1] Sent 896 datagrams
[  1] Server Report:
[ ID] Interval       Transfer     Bandwidth        Jitter   Lost/Total Datagrams
[  1] 0.0000-10.0140 sec  1.25 MBytes  1.05 Mbits/sec   0.054 ms 0/895 (0%)
```