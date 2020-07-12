package com.lvhttp.net.error

/**
 * @name ErrorKey
 * @package com.www.net.error
 * @author 345 QQ:1831712732
 * @time 2020/6/24 23:00
 * @description
 */
enum class ErrorKey {

    /**
     * 全局异常处理
     */
    AllEexeption,


    /**
     *  自定义 Code 异常
     */
    ErrorCode,

    /**
     * 域名解析失败
     * 原因 ：
     *      1，网络断开
     *      2，DNS 服务器意外挂掉
     *      3，DNS 服务器故障
     */
    UnknownHostException,

    /**
     * 数据解析错误
     */
    JSON_EXCEPTION,

    /**
     * 连接超时
     * 原因：
     *      1，设备接入的网络本身带宽比较低
     *      2，设备接入的网络本身延迟比较高
     *      3，设备与服务器的网络路径中存在比较拥堵、负载比较重的节点
     *      4，网络中路由节点的临时性异常
     */
    ConnectTimeoutException,

    /**
     * socket 超时
     */
    SocketTimeoutException,

    /**
     * 客户端数据包可以到达目标主机，但由于各种原因，连接建立失败
     * 原因：
     *      1，连接的目标主机没有开对应的端口，可能服务器发生故障
     *      2，客户端设置了代理，而代理进程并没有跑起来
     */
    HttpHostConnectException,

    /**
     * 无法连接远程地址与端口
     * 原因：
     *      1，防火墙的规则设置导致数据包无法被发送出去
     *      2，中间路由节点挂掉
     */
    NoRouteToHostException,

    /**
     * SSL 失败
     */
    SSLException,

    /**
     * IO 异常
     */
    IOException,

    /**
     * 连接失败
     */
    ConnectException
}