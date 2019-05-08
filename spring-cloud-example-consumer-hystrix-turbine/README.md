Hystrix 集群监控

- 默认的集群监控： 通过URL http://turbine-hostname:port/turbine.stream开启， 实现对默认集群的监控。
- 指定的集群监控： 通过URL http://turbine-hostname:port/turbine.strearn?cluster = [clusterName]开启， 实现对clusterName集群的监控。