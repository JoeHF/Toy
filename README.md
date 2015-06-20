# Toy
启动：
数据一开始在master上
master 启动pagerank 文件路径参数，迭代次数
读图文件，切割文件在slave上？slave_id-thread_id时间戳.txt
master有一个DHT，启动working thread，告诉working thread数据
working thread有一个DHTPeer，读数据然后建hash table

运算：
master通知thread运算代码；
workingThread每一轮先读本地图数据，对于每个图节点，调用dhtpeer找到与之相邻的节点的pagerank值，计算出该节点的新pagerank值，并更新。

检测线程挂掉：
dhtpeer:检测到远程调用失败后忽略，将异常抛给workingthread
workingThread:检测到异常后，通知master那个线程异常，然后suspend自己；
master：检测到异常后，等待所有线程的通知后，新建一个线程代替挂掉的线程；通知所有workingthread修改诸如dht表项等信息，重新启动运算。wait notify